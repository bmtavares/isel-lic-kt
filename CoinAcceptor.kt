import isel.leic.UsbPort

class CoinAcceptor(private val hal: HAL) {
    fun init() {
        hal.clrBits(0b1110_0000)
        Thread.sleep(1000)
    }

    fun hasCoin(): Boolean {
        var Inat: Int = UsbPort.read()
        return Inat and 0b0000_1000 != 0
    }

    fun getCoinValue(): Int {
        val arr = arrayOf<Int>(5, 10, 20,50,100,200)
        var Inat: Int = UsbPort.read()
        Inat = Inat and 0b0000_0111
        //if 110 or 111 error
        return arr[Inat]
    }

    fun acceptCoin() {
        if(!hasCoin()){
            print("error")
            return
        }
        hal.setBits(0b0010_0000)

        while (hasCoin()){

        }
        hal.clrBits(0b0010_0000)
    }

    fun ejectCoins() {
        if(!hasCoin()){
            print("error")
            return
        }
        hal.setBits(0b1000_0000)
        Thread.sleep(2000)
        hal.clrBits(0b1000_0000)
    }

    fun collectCoins() {
        hal.setBits(0b0100_0000)
        Thread.sleep(2000)
        hal.clrBits(0b0100_0000)
    }
}