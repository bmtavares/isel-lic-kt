import isel.leic.UsbPort

class CoinAcceptor(private val hal: HAL) {
    fun init() {
        hal.clrBits(0b1110_0000)
        Thread.sleep(1000)
    }

    fun hasCoin(): Boolean {
        var Inat: Int = hal.readBits(HAL.COIN_MASK)
        return Inat != 0
    }

    fun getCoinValue(): Int {
        val arr = arrayOf<Int>(5, 10, 20,50,100,200)
        var Inat: Int = hal.readBits(HAL.COIN_VALUE_MASK)

        //if 110 or 111 error
        return arr[Inat]
    }

    fun acceptCoin() {
        if(!hasCoin()){
            print("error")
            return
        }
        hal.setBits(HAL.COIN_ACCEPT_MASK)

        while (hasCoin()){

        }
        hal.clrBits(HAL.COIN_ACCEPT_MASK)
    }

    fun ejectCoins() {
        if(!hasCoin()){
            print("error")
            return
        }
        hal.setBits(HAL.COIN_EJECT_MASK)
        Thread.sleep(2000)
        hal.clrBits(HAL.COIN_EJECT_MASK)
    }

    fun collectCoins() {
        hal.setBits(HAL.COIN_COLLECT_MASK)
        Thread.sleep(2000)
        hal.clrBits(HAL.COIN_COLLECT_MASK)
    }
}