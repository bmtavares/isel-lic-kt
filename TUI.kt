class TUI( private val lcd:LCD)
{


    fun resetCountersScreen() {
       lcd.clear()
       lcd.writeCentered("Reset Counters")
       lcd.cursor(2, 1)
       lcd.write("5-Yes  other-No")
   }

    fun waitingScreen(currentDate:String){
        lcd.clear()
        lcd.writeCentered("Ticket to Ride")
        lcd.cursor(2,1)
        lcd.write(currentDate)
        lcd.home()
    }

    fun updsteDate(newDate:String){
        lcd.cursor(2,1)
        lcd.write(newDate)
        lcd.home()
    }

     fun shutdownScreen() {
        lcd.clear()
        lcd.writeCentered("Shutdown")
        lcd.cursor(2, 1)
        lcd.write("5-Yes  other-No")

    }

    fun showMaintenace(lineTop:String,menuLine:String){
        lcd.clear()
        lcd.write(Maintenance.LINE_TOP)
        lcd.cursor(2,1)
        lcd.write(menuLine) // LOW
        lcd.home()
    }

     fun dispaySelection(station:Station?,newSelect:Int,usingArrows:Boolean){
        lcd.clear()
        lcd.write(station!!.name)
        lcd.cursor(2,1)

        if(newSelect<10) lcd.write("0")

        lcd.write(newSelect.toString())

        if(usingArrows) lcd.write("${Symbols.ARROW_UP}${Symbols.ARROW_DOWN}")

        lcd.cursor(2,12)
        lcd.write(priceToText(station.price))
        if(!usingArrows){
            lcd.cursor(2,2)
        }
    }

     fun refreshPaymentScreen(StationName:String,price: Int,returnTrip:Boolean){
        lcd.clear()
        lcd.write(StationName)
        lcd.cursor(2,1)
        lcd.write(priceToText(price))
        lcd.write(Symbols.ARROW_UP)
        if(returnTrip)
            lcd.write(Symbols.ARROW_DOWN)
    }

     fun ShowToAbort(){
        lcd.clear()
        lcd.write("Vending Aborted")
    }


    fun refreshSwoStation(station:Station,newSelect: Int){
        lcd.clear()
        lcd.write(station.name)
        lcd.cursor(2,1)
        if(newSelect<10) lcd.write("0")
        lcd.write(station.counter.toString())
    }

    fun secondLine(msg:String){
        lcd.cursor(2,1)
        lcd.write(msg)
    }

     fun switchcoins(idx:Int,coi_valu:Int,coinCount:Int) {
        lcd.clear()
        lcd.write(priceToText(coi_valu))
         lcd.cursor(2,1)
        if(idx<10) lcd.write("0")
        lcd.write(coinCount.toString())
     }

    fun priceToText(i: Int):String{
        val cent = i % 100
        val unidades = (i - cent)/100
        var sCent = cent.toString()
        if( cent < 10) sCent = "0$cent"
        var s = "$unidades.$sCent€"

        if (cent == 0) s = "$unidades.00€"

        return s
    }
}