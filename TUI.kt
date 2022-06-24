import isel.leic.utils.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess



class TUI( private val lcd:LCD)
{


    fun resetCountersScreen() {
       lcd.clear()
       lcd.writeCentered("Reset Counters")
       lcd.cursor(2, 1)
       lcd.write("5-Yes  other-No")
   }


     fun shutdownScreen() {
        lcd.clear()
        lcd.writeCentered("Shutdown")
        lcd.cursor(2, 1)
        lcd.write("5-Yes  other-No")

    }

     fun dispaySelection(station:Station?,newSelect:Int,usingArrows:Boolean){
        lcd.clear()
        lcd.write(station!!.name)
        lcd.jumpLine()
        if(newSelect<10) lcd.write('0')
        lcd.write(newSelect.toString())
        if(usingArrows){
            lcd.writeData(126)
            lcd.writeData(127)
        }

        // lcd.write("<>")
        lcd.setDDRAGM(76)
        lcd.write(lcd.priceToText(station!!.price!!))
        if(!usingArrows){
            lcd.setDDRAGM(66)
        }


    }

     fun refreshPaymentScreen(StationName:String,price: Int,returnTrip:Boolean){
        lcd.clear()
        lcd.write(StationName)
        lcd.jumpLine()
        lcd.write(lcd.priceToText(price))
        lcd.writeData(126)
        if(returnTrip){
            lcd.writeData(127)
        }
    }

     fun ShowToAbort(){
        lcd.clear()
        lcd.write("Vending Aborted")
    }


      fun refreshSwoStation(station:Station,newSelect: Int){
        lcd.clear()
        lcd.write(station!!.name)
        lcd.jumpLine()
        if(newSelect<10) lcd.write('0')
        lcd.write(station.counter.toString())

    }


     fun switchcoins(idx:Int,coi_valu:Int,coinCount:Int) {
        lcd.clear()
        lcd.write(lcd.priceToText(coi_valu))
        lcd.jumpLine()
        if(idx<10) lcd.write('0')
        lcd.write(coinCount.toString())


    }




}