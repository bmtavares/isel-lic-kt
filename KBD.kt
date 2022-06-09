import isel.leic.utils.Time

const val NONE = 0.toChar();

class KBD(private val hal:HAL) {
    private val keyMap = mapOf(
        0b0111 to '0',
        0b0000 to '1',
        0b0100 to '2',
        0b1000 to '3',
        0b0001 to '4',
        0b0101 to '5',
        0b1001 to '6',
        0b0010 to '7',
        0b0110 to '8',
        0b1010 to '9',
        0b0011 to '*',
        0b1011 to '#'
    )

    private val keyReceiver = KeyReceiver(hal)
    
    private fun getKeySerial(): Char =
        if(!hal.isBit(KeyReceiver.TXD_MASK))
            keyMap.getOrDefault(keyReceiver.rcv(), NONE)
        else
            NONE
    
    fun getKey(): Char = getKeySerial()
    
    fun waitKey(timeout: Long): Char {
        val start = Time.getTimeInMillis()
        while(Time.getTimeInMillis() < start + timeout){
            val key = getKey()
            if(key != NONE) return key
        }
        return NONE
    }

    fun unitTest() {
        println("Starting KBD test")
        println("Waiting 5s for key")
        var key = waitKey(5000)
        println(if(key != NONE) key else "No key pressed")
        println("Waiting 3s for key")
        key = waitKey(3000)
        println(if(key != NONE) key else "No key pressed")
        println("Waiting 1s for key")
        key = waitKey(1000)
        println(if(key != NONE) key else "No key pressed")
        println("KBD test finished")
    }
}