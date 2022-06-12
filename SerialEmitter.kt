enum class Destination {
    LCD,
    TICKET_DISPENSER
}

class SerialEmitter(private val hal: HAL) {
    companion object {
        /** Established size of a data frame containing TnL, RoundTrip bit, Destiny ID and Origin ID. */
        const val PROTOCOL_SIZE = 10
    }


    /**
     * Prepares the given [data] vector to be sent to the destination [addr].
     */
    fun send(addr: Destination, data: Int) {
        // Set TnL
        val data = when(addr) {
            Destination.LCD -> data or 0b00_0000_0000
            Destination.TICKET_DISPENSER -> data or 0b10_0000_0000
        }

        var sdx : Int
        var parity = 0b0

        // Wait for busy signal to end in case it is happening before new transmission
        while(isBusy()){ }

        // Send full data vector (with TnL)
        for(i in 1..PROTOCOL_SIZE){

            sdx = (data ushr PROTOCOL_SIZE - i) and 0b1
            parity = parity xor sdx // Calculate parity

            // Set clock low
            hal.clrBits(HAL.SCLK_MASK)

            // Set nSS
            hal.clrBits(HAL.SS_MASK)

            // Write the SDX bit
            if(sdx > 0) hal.setBits(HAL.SDX_MASK) else hal.clrBits(HAL.SDX_MASK)

            // Set clock high
            hal.setBits(HAL.SCLK_MASK)
        }

        // Set clock low
        hal.clrBits(HAL.SCLK_MASK)

        // Write parity to SDX bit
        if(parity > 0) hal.setBits(HAL.SDX_MASK) else hal.clrBits(HAL.SDX_MASK)

        // Set clock to high
        hal.setBits(HAL.SCLK_MASK)

        // Clear nSS
        hal.setBits(HAL.SS_MASK)

        // Set clock to low
        hal.clrBits(HAL.SCLK_MASK)

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
        Thread.sleep(100)

        println("Destination ID 0xE")
        println("Origin ID 0xA")
        println("Round trip is true")
        println("Sending to Ticket Dispenser")

        val destination = Destination.TICKET_DISPENSER
        val data = 0b1_1110_1010

        send(destination, data)
    }
}