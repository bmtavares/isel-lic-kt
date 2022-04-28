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
        TODO()
    }

    /**
     * Prepares the given [data] vector to be sent to the destination [addr].
     * If [DEBUG_MODE] is on, the [HAL] will not be used.
     */
    fun send(addr: Destination, data: Int) {
        // Set TnL
        val data = when(addr) {
            Destination.LCD -> data or 0b00_0000_0000
            Destination.TICKET_DISPENSER -> data or 0b10_0000_0000
        }

        val size = data.takeHighestOneBit().countTrailingZeroBits()

        if(DEBUG_MODE){
            println("Full data is ${data.toString(2)}")
            println("Parity is ${data.findParity().toInt()}")
        }

        var sdx = (data ushr size) and 0b1
        var frameBlock : Int
        var parity = 0b0 xor sdx

        // Send full data vector (with TnL)
        for(i in 1..size + 1){
            // Clock low
            frameBlock = 0b100 or sdx

            // Write to USB
            writeToUSB(frameBlock)

            // Clock high
            frameBlock = frameBlock xor 0b10

            // Write to USB
            writeToUSB(frameBlock)

            // Get next sdx
            sdx = (data ushr (size - i))
            // Mask for first bit
            sdx = sdx and 0b1

            // Calculate parity
            parity = parity xor sdx
        }

        // Time for parity
        // Set on low
        frameBlock = 0b100 or parity

        // Write to USB
        writeToUSB(frameBlock)

        // Send on high & set ss to low
        frameBlock = frameBlock xor 0b110

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
        else
            hal.writeBits(writeMask, message)

    /**
     * Reports the busy status of the IOS.
     */
    fun isBusy(): Boolean = hal.isBit(readMask)
}