class KeyReceiver(private val hal:HAL) {
    companion object{
        const val TXD_MASK = 0b0010_0000
        const val TCLK_MASK = 0b0001_0000
    }

    fun init() {
        TODO()
    }

    fun rcv(): Int {
        var keyPressed=0b0

        for(i in 0..5){
            hal.setBits(TCLK_MASK)
            val read = if (hal.isBit(TXD_MASK)) 1 else 0
            hal.clrBits(TCLK_MASK)
            if(i == 0) {
                if(read == 0) {
                    println("Start bit was ${read.toString(2)}!")
                    return 0b10000
                }
            }
            else if(i == 5) {
                if (read == 1) {
                    println("End bit was ${read.toString(2)}!")
                    return 0b10000
                }
            }
            else {
                keyPressed = (keyPressed or (read shl i - 1))
            }
        }
        hal.setBits(TCLK_MASK)
        hal.clrBits(TCLK_MASK)
        return keyPressed
    }
}