import isel.leic.UsbPort
import java.lang.IndexOutOfBoundsException

class CoinAcceptor(private val hal: HAL, private val fs:FileService) {
    companion object{
        const val FILENAME = "CoinDeposit.txt"
    }

    var inserted_coin_value : Int = 0;
    var inserted_coins : Int = 0;
    var arr_inserted_coins = arrayOf<Int>(0, 0, 0,0,0,0);
    var stored_coins : Int = 0;
    var arr_stored_coins = arrayOf<Int>(0, 0, 0,0,0,0);
    var totalCoinsInserted = 0
    val coinValues = arrayOf<Int>(5, 10, 20,50,100,200)

    fun init() {
        hal.clrBits(0b1110_0000)
        readCoins()
    }

    fun hasCoin() = hal.isBit(HAL.COIN_MASK)

    private fun getCoinValue(): Int = coinValues.getOrNull(getCoinIndex()) ?: 0

    private fun getCoinIndex(): Int = hal.readBits(HAL.COIN_VALUE_MASK)

    fun acceptCoin() = try{
        arr_inserted_coins[getCoinIndex()] +=1;
        totalCoinsInserted += getCoinValue()
        hal.setBits(HAL.COIN_ACCEPT_MASK)

        while (hasCoin()){}

        hal.clrBits(HAL.COIN_ACCEPT_MASK)
    }
    catch(ex:IndexOutOfBoundsException){ }

    fun ejectCoins() {
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

    fun readCoins(){
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