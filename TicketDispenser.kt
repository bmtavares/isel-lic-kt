class TicketDispenser {

    private var serialEmitter = SerialEmitter()

    fun init(){

    }

    fun main() {
        print(0b1110, 0b0010, false)
        print(0b0010, 0b1110, false)
        print(0b1001, 0b0110, true)
    }

    fun print(destinyId: Int, originId: Int, roundTrip: Boolean) {
        // 0bRDDDDOOOO
        var data  = 0b0
        data = data or (roundTrip.toInt() shl 8)
        data = data or (destinyId shl 4)
        data = data or originId

        serialEmitter.send(Destination.TICKET_DISPENSER, data)
    }
}