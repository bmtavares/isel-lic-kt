enum class Destination {
    LCD,
    TICKET_DISPENSER
}

class SerialEmitter {
    companion object {
        const val writeMask = 0b0000_0111
        const val readMask = 0b0000_1000
    }

    var hal = HAL()

    fun init() {
        if(!DEBUG_MODE)
            // Make sure the output is empty
            hal.clrBits(0b1111_1111)
    }

    /**
     * Prepares the given [data] vector to be sent to the destination [addr].
     * If [DEBUG_MODE] is on, the [HAL] will not be used.
     */
    fun send(addr: Destination, data: Int) {
        init()

        // Set TnL
        val data = when(addr) {
            Destination.LCD -> data or 0b00_0000_0000
            Destination.TICKET_DISPENSER -> data or 0b10_0000_0000
        }

        // This is currently not working for LCD for the obvious reason that the highest bit _isn't_ the TnL
        val size = data.takeHighestOneBit().countTrailingZeroBits()

        if(DEBUG_MODE){
            println("Full data is ${data.toString(2)}")
            println("Parity is ${data.findParity().toInt()}")
        }

        var sdx = (data ushr size) and HAL.SDX_MASK
        // frameBlock will contain the 3 bits we will use to send data via the [HAL]
        // 0b[SS][SCLK][SDX]
        var frameBlock : Int
        var parity = 0b0 xor sdx

        // Wait for busy signal to end in case it is happening before new transmission
        if(!DEBUG_MODE)
            while(isBusy()){ }

        // Send full data vector (with TnL)
        for(i in 1..size + 1){
            // Clock low
            frameBlock = sdx

            // Write to USB
            writeToUSB(frameBlock)

            // Clock high
            frameBlock = frameBlock xor HAL.SCLK_MASK

            // Write to USB
            writeToUSB(frameBlock)

            // Get next sdx
            sdx = (data ushr (size - i))
            // Mask for first bit
            sdx = sdx and HAL.SDX_MASK

            // Calculate parity
            parity = parity xor sdx
        }

        // Time for parity
        // Set on low
        frameBlock = parity

        // Write to USB
        writeToUSB(frameBlock)

        // Send on high & set ss to low
        frameBlock = frameBlock xor (HAL.SS_MASK or HAL.SCLK_MASK)

        // Write to USB
        writeToUSB(frameBlock)

        // Wait for busy signal to end
        if(!DEBUG_MODE)
            while(isBusy()){ }
    }

    /**
     * Writes to USB on the Class' [writeMask].
     * If [DEBUG_MODE] is on, writes the [message] to console instead.
     */
    private fun writeToUSB(message : Int) =
        if(DEBUG_MODE)
            println(message.toString(2))
        else{
            hal.writeBits(writeMask, message)
//            Thread.sleep(1000)
        }

    /**
     * Reports the busy status of the IOS.
     */
    fun isBusy(): Boolean = hal.isBit(readMask)
}