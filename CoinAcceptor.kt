import isel.leic.UsbPort

class CoinAcceptor(private val hal: HAL, private val fs:FileService) {
    companion object{
        const val FILENAME = "CoinDeposit.txt"
    }

    public  var inserted_coin_value : Int = 0;
    public  var inserted_coins : Int = 0;
    public  var arr_inserted_coins = arrayOf<Int>(0, 0, 0,0,0,0);
    public  var stored_coins : Int = 0;
    public  var arr_stored_coins = arrayOf<Int>(0, 0, 0,0,0,0);
    public  var totalCoinsInserted = 0
    private val coinValues = arrayOf<Int>(5, 10, 20,50,100,200)

    fun init() {
        hal.clrBits(0b1110_0000)
//        Thread.sleep(1000)
        readCoins()
    }

    fun hasCoin() = hal.isBit(HAL.COIN_MASK)

    fun getCoinValue(): Int {
        //if 110 or 111 error
        return coinValues[getCoinIndex()]
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
        totalCoinsInserted += getCoinValue()
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
        totalCoinsInserted = 0
        Thread.sleep(2000)
        hal.clrBits(HAL.COIN_EJECT_MASK)
    }

    fun collectCoins() {
        arr_inserted_coins.forEachIndexed { i, element ->
            arr_stored_coins[i] +=element
            arr_inserted_coins[i]  = 0;

        }
        totalCoinsInserted =0
        hal.setBits(HAL.COIN_COLLECT_MASK)
        Thread.sleep(2000)
        hal.clrBits(HAL.COIN_COLLECT_MASK)
    }

    private fun readCoins(){
        val coinsFile = fs.readFromFile(FILENAME)
        for (line in coinsFile){
            line.split(';').let{
                val idx = coinValues.indexOf(it[0].toInt())
                if(idx != -1)
                    arr_stored_coins[idx] = it[1].toInt()
            }
        }
    }

    fun writeCoins(){
        val coinsFile = mutableListOf<String>()
        for((idx, coin) in arr_stored_coins.withIndex()){
            coinsFile.add("${coinValues[idx]};${coin}")
        }
        fs.writeToFile(FILENAME,coinsFile)
    }

    fun resetCounters(){
        for(i in arr_stored_coins.indices) arr_stored_coins[i] = 0
    }
}