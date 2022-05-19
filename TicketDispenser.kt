import SerialEmitter

class TicketDispenser (_serialEmitter: SerialEmitter){
private var serialEmitter : SerialEmitter = _serialEmitter
    fun main() {
       // print(0x1, 0xC, false)
       // print(0xE, 0xA, true)
       // print(0x3, 0x6, true)
       // print(0xB, 0x2, false)
       // print(0x4, 0xB, false)
       // print(0x9, 0x6, true)



    }

    /** Constructs a frame containing the data required for printing a ticket
     *  using the supplied [destinyId], [originId] and if it's a [roundTrip]. */
    fun print(destinyId: Int, originId: Int, roundTrip: Boolean) {
        // 0bRt_DDDD_OOOO
        var data  = 0b0
        data = data or (roundTrip.toInt() shl 8)
        data = data or (destinyId shl 4)
        data = data or originId

        serialEmitter.send(Destination.TICKET_DISPENSER, data)
    }

    fun unitTest() {
        println("Starting TicketDispenser test.")
        println("Printing 6 tickets with 5s of delay between each other.")
        println("If busy bit is set (0b${HAL.BUSY_BIT.toString(2)}) the emission will be frozen until it is set to low.")
        print(0x1, 0xC, false)
        println("Ticket 1 printed.")
        Thread.sleep(5000)
        print(0xE, 0xA, true)
        println("Ticket 2 printed.")
        Thread.sleep(5000)
        print(0x3, 0x6, true)
        println("Ticket 3 printed.")
        Thread.sleep(5000)
        print(0xB, 0x2, false)
        println("Ticket 4 printed.")
        Thread.sleep(5000)
        print(0x4, 0xB, false)
        println("Ticket 5 printed.")
        Thread.sleep(5000)
        print(0x9, 0x6, true)
        println("Ticket 6 printed.")
    }
}