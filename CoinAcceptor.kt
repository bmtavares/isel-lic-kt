import isel.leic.UsbPort

class CoinAcceptor(private val hal: HAL) {
    public  var inserted_coin_value : Int = 0;
    public  var inserted_coins : Int = 0;
    public  var arr_inserted_coins = arrayOf<Int>(0, 0, 0,0,0,0);
    public  var stored_coins : Int = 0;
    public  var arr_stored_coins = arrayOf<Int>(0, 0, 0,0,0,0);

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

        //if 110 or 111 error
        return arr[getCoinIndex()]
    }

    fun getCoinIndex(): Int {
        var Inat: Int = hal.readBits(HAL.COIN_VALUE_MASK)

        //if 110 or 111 error
        return Inat
    }

    fun acceptCoin() {
        if(!hasCoin()){
            print("error")
            return
        }

        arr_inserted_coins[getCoinIndex()] +=1;

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
        arr_inserted_coins.map { _ -> 0 }

        Thread.sleep(2000)
        hal.clrBits(HAL.COIN_EJECT_MASK)
    }

    fun collectCoins() {
        arr_inserted_coins.forEachIndexed { i, element ->
            arr_stored_coins[i] +=element
            arr_inserted_coins[i]  = 0;
        }

        hal.setBits(HAL.COIN_COLLECT_MASK)
        Thread.sleep(2000)
        hal.clrBits(HAL.COIN_COLLECT_MASK)
    }
}