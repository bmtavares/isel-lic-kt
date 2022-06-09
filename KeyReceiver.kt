class KeyReceiver(private val hal:HAL) {
    companion object{

    }

    fun rcv(): Int {
        var keyPressed=0b0

        for(i in 0..5){
            hal.setBits(HAL.TCLK_MASK)
            val read = if (hal.isBit(HAL.TXD_MASK)) 1 else 0
            hal.clrBits(HAL.TCLK_MASK)
            if(i == 0) {
                if(read == 0) {
                    println("Start bit was ${read.toString(2)}!")
                    flushRemoveBuffer()
                    return 0b10000
                }
            }
            else if(i == 5) {
                if (read == 1) {
                    println("End bit was ${read.toString(2)}!")
                    flushRemoveBuffer()
                    return 0b10000
                }
            }
            else {
                keyPressed = (keyPressed or (read shl i - 1))
            }
        }
        hal.setBits(HAL.TCLK_MASK)
        hal.clrBits(HAL.TCLK_MASK)
        return keyPressed
    }

    fun pollKey(): Boolean = !hal.isBit(HAL.TXD_MASK)

    private fun flushRemoveBuffer(){
        for(i in 0..6){
            hal.setBits(HAL.TCLK_MASK)
            hal.clrBits(HAL.TCLK_MASK)
        }
    }
}