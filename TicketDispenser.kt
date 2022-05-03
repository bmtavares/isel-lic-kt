class TicketDispenser {

    private var serialEmitter = SerialEmitter()

    fun init(){

    }

    fun main() {
        print(0x1, 0xC, false)
        print(0xE, 0xA, true)
        print(0x3, 0x6, true)
        print(0xB, 0x2, false)
        print(0x4, 0xB, false)
        print(0x9, 0x6, true)
    }

    fun print(destinyId: Int, originId: Int, roundTrip: Boolean) {
        // 0bRt_DDDD_OOOO
        var data  = 0b0
        data = data or (roundTrip.toInt() shl 8)
        data = data or (destinyId shl 4)
        data = data or originId

        serialEmitter.send(Destination.TICKET_DISPENSER, data)
    }
}