import isel.leic.utils.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

const val TIMEOUT_FOR_SELECTION = 12000L
const val TIMEOUT_FOR_MAINTENANCE = 1000L
const val TIMEOUT_FOR_MAINTENANCE_PROMPT = 5000L




class TUI( private val lcd:LCD,
           private val m:Maintenance,
           private val kbd:KBD,
           private val stationService: StationService,
           private val coinacpt: CoinAcceptor,
           private val ticketDispenser: TicketDispenser,
            ){

    private var usingArrows = false
    private var selection = 0
    private var returnTrip = false
    private var finish = false

   fun waitingScreen(){
   	   val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
       var write = true
       while(true){
           finish = false
           lcd.clear()
           lcd.writeCentered("Ticket to Ride")
           lcd.jumpLine()

           var currentDate = sdf.format(Date())
           lcd.write(currentDate.toString())
           lcd.home()
           while (!finish){
               if (m.isMaintenanceKey()){
                   goToMaintenanceMenu()
               }
               if(kbd.getKey() == '#'){
                   initStationSelection()
                   goToStationSelection()
               }
               val newcurrentDate = sdf.format(Date())
               if(newcurrentDate != currentDate ){
                   lcd.jumpLine()
                   lcd.write(newcurrentDate.toString())
                   currentDate = newcurrentDate
                   lcd.home()
               }


           }
       }
   }

   private fun initStationSelection() {
       //usingArrows = false
       selection = 0
   }

   private fun goToMaintenanceMenu() {
       while(true){
           lcd.clear()
           lcd.write(Maintenance.LINE_TOP)
           lcd.cursor(2,1)
           lcd.write(m.getMenuLine()) // LOW
           lcd.home()
           val start = Time.getTimeInMillis()
           while(Time.getTimeInMillis() < start + TIMEOUT_FOR_MAINTENANCE){
               when(kbd.getKey()){
                     '1' -> goToStationSelection(true)
                     '2' -> stationsCount()
                     '3' -> coinsCount()
                   '4' -> resetCountersScreen()
                   '5' -> shutdownScreen()
               }

               if(!m.isMaintenanceKey()) return
           }
       }
   }


   private fun resetCountersScreen() {
       lcd.clear()
       lcd.writeCentered("Reset Counters")
       lcd.cursor(2, 1)
       lcd.write("5-Yes  other-No")
       if(kbd.waitKey(TIMEOUT_FOR_MAINTENANCE_PROMPT) == '5') resetCounters()
   }

   private fun resetCounters() {
       stationService.resetCounters()
       coinacpt.resetCounters()
   }

    private fun shutdownScreen() {
        lcd.clear()
        lcd.writeCentered("Shutdown")
        lcd.cursor(2, 1)
        lcd.write("5-Yes  other-No")
        if(kbd.waitKey(TIMEOUT_FOR_MAINTENANCE_PROMPT) == '5') shutdownMachine()
    }

    private fun shutdownMachine() {
        stationService.writeStations()
        coinacpt.writeCoins()
        exitProcess(0)
    }

    private fun timeout(){
        finish = true
    }

   private fun goToStationSelection(Maintenance:Boolean = false){
       inputSelection('0')
       finish = false
       while(!finish){
           when (val k = kbd.waitKey(TIMEOUT_FOR_SELECTION)){
               NONE -> return
               '*' -> alternateSelectionMode()
               '#' -> goToPaymentScreen(Maintenance)
              // else -> inputSelection(k)
               else -> selectionMode(k)
           }
       }
   }

    private fun selectionMode(k:Char){
        if(usingArrows){
            return inputSelectionUsingArrows(k)
        }

        return inputSelection(k)
    }


    private  fun inputSelectionUsingArrows(k:Char){
        lcd.clear()
        var newSelect = selection
        if(k == '2'){
            newSelect +=1
            if (newSelect >= stationService.listOfStations.size){
                newSelect = 0
            }
        }else if (k == '8'){
            newSelect -=1
            if (newSelect <= -1){
                newSelect = stationService.listOfStations.size-1
            }
        }
        var station = stationService.listOfStations.getOrNull(newSelect)
        dispaySelection(station,newSelect)
    }

    private fun dispaySelection(station:Station?,newSelect:Int){
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

        selection = newSelect
    }

    private  fun inputSelectionUsingArrows(i :Int){

        var station = stationService.listOfStations.getOrNull(i)
        dispaySelection(station,i)


    }



    private fun inputSelection(i:Int) {
        var station = stationService.listOfStations.getOrNull(i)

        dispaySelection(station,i)
    }

   private fun inputSelection(k:Char) {

       var newSelect = (selection * 10 + (k.toInt()-48)) % 100  // char to digit only implemented on kotlin 15
       var station = stationService.listOfStations.getOrNull(newSelect)
       if(station == null){
           newSelect = (k.toInt()-48)
           station = stationService.listOfStations.getOrNull(newSelect)
           if(station == null) newSelect = selection
       }
       dispaySelection(station,newSelect)
   }

    private fun refreshPaymentScreen(StationName:String,price: Int){
        lcd.clear()
        lcd.write(StationName)
        lcd.jumpLine()
        lcd.write(lcd.priceToText(price))
        lcd.writeData(126)
        if(returnTrip){
            lcd.writeData(127)
        }

    }

   private fun goToPaymentScreen(Maintenance:Boolean = false) {
       var station = stationService.listOfStations.getOrNull(selection)
       var price = station!!.price!!
       if(returnTrip) price *=2
       refreshPaymentScreen(station.name,price)
       while(!finish){

           if(coinacpt.hasCoin()){
               coinacpt.acceptCoin()
               var newprice = price - coinacpt.totalCoinsInserted
               refreshPaymentScreen(station.name,newprice)
           }

           if((coinacpt.totalCoinsInserted >= price) or Maintenance){
               print("dispense tiket")
               lcd.jumpLine()
               lcd.write("coletc tiket")
               ticketDispenser.print(selection,stationService.originStation!!.ID,returnTrip)
               if(!Maintenance){
                   station.counter++
                   coinacpt.collectCoins()
               }
               lcd.jumpLine()
               lcd.write("have a nice trip")
               Thread.sleep(2000)
               finish = true

           }

           val key = kbd.getKey()
           if (key == '0'){
               alternateTripReturn()
               if(returnTrip){
                   price *=2
               }else{
                   price /=2
               }
               refreshPaymentScreen(station.name,price - coinacpt.totalCoinsInserted)

           }
           if (key == '#'){
               goToAbort()
           }
       }
   }

    private fun alternateTripReturn(){
        returnTrip = !returnTrip
    }

    private fun goToAbort(){
        finish = true
        coinacpt.ejectCoins()
        lcd.clear()
        lcd.write("Vending Aborted")
        Thread.sleep(2000)
    }

   private fun alternateSelectionMode() {
       usingArrows = !usingArrows

       if(usingArrows){
           return inputSelectionUsingArrows(selection)
       }

       return inputSelection(selection)
       //refreshcren
   }







    private  fun refreshSwoStation(station:Station,newSelect: Int){
        lcd.clear()
        lcd.write(station!!.name)
        lcd.jumpLine()
        if(newSelect<10) lcd.write('0')
        lcd.write(station.counter.toString())





        selection = newSelect


    }



    private fun inputSelectioncount(k:Char) {
        var newSelect = (selection * 10 + (k.toInt()-48)) % 100  // char to digit only implemented on kotlin 15
        var station = stationService.listOfStations.getOrNull(newSelect)
        if(station == null){
            newSelect = (k.toInt()-48)
            station = stationService.listOfStations.getOrNull(newSelect)
            if(station == null) newSelect = selection
        }
        refreshSwoStation(station!!,newSelect)

    }

    private fun coinsCount() {
        finish = false
        switchcoins(0)

        while(!finish){

            when (val k = kbd.waitKey(TIMEOUT_FOR_SELECTION)){
                NONE -> return
                '#' -> goToAbort()
                '*' -> continue
                else -> inputSelectionCountCoin(k)
            }

            val key = kbd.getKey()

            if (key == '#'){
                goToAbort()
            }
        }
    }

    private fun switchcoins(idx:Int) {
        lcd.clear()
        lcd.write(lcd.priceToText(coinacpt.coinValues[idx]))
        lcd.jumpLine()
        if(idx<10) lcd.write('0')
        lcd.write(coinacpt.arr_stored_coins[idx].toString())

        selection = idx
    }

    private fun inputSelectionCountCoin(k : Char) {
        var newSelect = (selection * 10 + (k.toInt()-48)) % 100  // char to digit only implemented on kotlin 15
        var coin = coinacpt.coinValues.getOrNull(newSelect)
        if(coin == null){
            newSelect = (k.toInt()-48)
            coin = coinacpt.coinValues.getOrNull(newSelect)
            if(coin == null) newSelect = selection
        }
        switchcoins(newSelect)
    }

    private fun stationsCount(){
        finish = false
        var station = stationService.listOfStations.getOrNull(selection)

        refreshSwoStation(station!!,selection)
        while(!finish){

            when (val k = kbd.waitKey(TIMEOUT_FOR_SELECTION)){
                NONE -> return
                '#' -> goToAbort()
                 '*' -> continue
                else -> inputSelectioncount(k)
            }

            val key = kbd.getKey()

            if (key == '#'){
                goToAbort()
            }
        }


    }
}