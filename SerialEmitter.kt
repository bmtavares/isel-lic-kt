import isel.leic.UsbPort

enum class Destination {
    LCD,
    TICKET_DISPENSER
}

class SerialEmitter {
    companion object {
        const val WRITE_MASK = (HAL.SCLK_MASK or HAL.SS_MASK) or HAL.SDX_MASK
        /** Established size of a data frame containing TnL, RoundTrip bit, Destiny ID and Origin ID. */
        const val PROTOCOL_SIZE = 10
    }

    private var hal = HAL()

    fun init() {
        // Make sure the output is empty
        hal.init()
    }

    /**
     * Prepares the given [data] vector to be sent to the destination [addr].
     */
    fun send(addr: Destination, data: Int) {
       // init()
        print("going to send   ")
        println(data.toString(2))
        // Set TnL
        val data = when(addr) {
            Destination.LCD -> data or 0b00_0000_0000
            Destination.TICKET_DISPENSER -> data or 0b10_0000_0000
        }

        // This is currently not working for LCD for the obvious reason that the highest bit _isn't_ the TnL
        // val size = data.takeHighestOneBit().countTrailingZeroBits()
        // Taking from the protocol specification, we set the size according to a constant
        val size = PROTOCOL_SIZE

        var sdx = (data ushr size - 1) and HAL.SDX_MASK
        // frameBlock will contain the 3 bits we will use to send data via the [HAL]
        // 0b[SS][SCLK][SDX]
        var frameBlock : Int
        var parity = 0b0
        Thread.sleep(1000, 100)
        // Wait for busy signal to end in case it is happening before new transmission
        while(isBusy()){ }

        // Send full data vector (with TnL)
        for(i in 1..size){
            // Clock low
          //  frameBlock = sdx
            sdx = (data ushr size - i) and HAL.SDX_MASK
            parity = parity xor sdx
            // Write to USB
            hal.writeBits(HAL.SCLK_MASK, 0)
            hal.writeBits(HAL.SS_MASK, 0)
            hal.writeBits(HAL.SDX_MASK, sdx)
            Thread.sleep(1, 100)
            // Clock high
        //    frameBlock = frameBlock xor HAL.SCLK_MASK
            hal.writeBits(HAL.SCLK_MASK, 255)
            Thread.sleep(1, 100)

            // Write to USB
            //hal.writeBits(WRITE_MASK, frameBlock)
           // UsbPort.write()

            // Get next sdx

            println(sdx)
            // Calculate parity

        }

        // Time for parity
        // Set on low
        frameBlock = parity

        // Write to USB
        hal.writeBits(HAL.SCLK_MASK, 0)
        Thread.sleep(1, 100)
        hal.writeBits(HAL.SDX_MASK, parity)

        // Clock high
        //    frameBlock = frameBlock xor HAL.SCLK_MASK
        hal.writeBits(HAL.SCLK_MASK, 255)
        hal.writeBits(HAL.SS_MASK, 255)
        Thread.sleep(1, 100)
        hal.writeBits(HAL.SCLK_MASK, 0)
        Thread.sleep(1, 100)
        println(parity)
        // Wait for busy signal to end
        while(isBusy()){ }
    }

    /** Reports the busy status of the IOS. */
    fun isBusy(): Boolean = hal.isBit(HAL.BUSY_BIT)

    fun unitTest(){
        println("Starting SerialEmitter test in 5s.")
        println("If busy bit is set (0b${HAL.BUSY_BIT.toString(2)}) the emission will be frozen until it is set to low.")

        // Make sure the UsbPort simulator is open before
        hal.readBits(0xFF)
        Thread.sleep(5000)

        println("Destination ID 0xE")
        println("Origin ID 0xA")
        println("Round trip is true")
        println("Sending to Ticket Dispenser")

        val destination = Destination.TICKET_DISPENSER
        val data = 0b1_1110_1010

        send(destination, data)
    }
}