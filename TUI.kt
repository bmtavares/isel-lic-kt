import java.io.File
import kotlin.system.exitProcess

const val TIMEOUT_FOR_SELECTION = 12000L
const val TIMEOUT_FOR_MAINTENANCE = 1000L
const val TIMEOUT_FOR_MAINTENANCE_PROMPT = 5000L


data class Station(val name:String, val price:Int, var counter:Int)

class TUI( private val lcd:LCD,private val  m:Maintenance, private val  kbd:KBD, private val coinacpt: CoinAcceptor, private var ticketDispenser: TicketDispenser){

    private var usingArrows = false
   private var selection = 0
    private var originstation:Int? = null
    private var returnTrip = false
    private var finish = false
   private val listOfStations = readStations()



   private fun readStations(): List<Station> {
       val lines = File("stations.txt").readLines()
       val result = emptyList<Station>().toMutableList()
       var cont = 0
       for(line in lines){
           val values = line.split(';')
           result += Station(values[2],values[0].toInt(),0)
           if(values[0].toInt() == 0){
               if (originstation != null){
                   println("more then one oregin station")
               }
               originstation =  cont

           }
           cont += 1
       }

       return result
   }

   fun waitingScreen(){

       while(true){
           finish = false
           lcd.clear()
           lcd.write(" Ticket to Ride ")
           lcd.jumpLine()
           lcd.write("place holder date ")
           while (!finish){
               if (m.isMaintenanceKey()){
                   goToMaintenanceMenu()
               }
               if(kbd.getKey() == '#'){
                   initStationSelection()
                   goToStationSelection()
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
           lcd.write(Maintenance.LINE_TOP)
           lcd.write(m.getMenuLine()) // LOW
           when(kbd.waitKey(TIMEOUT_FOR_MAINTENANCE)){
               NONE -> continue
             //  '1' -> printTicket()
            //   '2' -> stationsCount()
               '3' -> coinacpt.arr_stored_coins.sum()
               '4' -> resetCountersScreen()
               '5' -> shutdown()
           }

           if(!m.isMaintenanceKey()) break
       }
   }

   private fun resetCountersScreen() {
       lcd.write(" Reset Counters ")
       if(kbd.waitKey(TIMEOUT_FOR_MAINTENANCE_PROMPT) == '5') resetCounters()
   }

   private fun resetCounters() {
       TODO("Not yet implemented")
   }

   private fun shutdown(): Nothing = exitProcess(0)


   private fun goToStationSelection(){
       inputSelection('0')
       while(!finish){
           when (val k = kbd.waitKey(TIMEOUT_FOR_SELECTION)){
              // NONE -> return
               '*' -> alternateSelectionMode()
               '#' -> goToPaymentScreen()
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
        var newSelect = selection
        if(k == '2'){
            newSelect +=1
            if (newSelect >= listOfStations.size){
                newSelect = 0
            }
        }else if (k == '8'){
            newSelect -=1
            if (newSelect <= -1){
                newSelect = listOfStations.size-1
            }
        }
        var station = listOfStations.getOrNull(newSelect)
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

        var station = listOfStations.getOrNull(i)
        dispaySelection(station,i)


    }



    private fun inputSelection(i:Int) {
        var station = listOfStations.getOrNull(i)

        dispaySelection(station,i)
    }

   private fun inputSelection(k:Char) {

       var newSelect = (selection * 10 + (k.toInt()-48)) % 100  // char to digit only implemented on kotlin 15
       var station = listOfStations.getOrNull(newSelect)
       if(station == null){
           newSelect = (k.toInt()-48)
           station = listOfStations.getOrNull(newSelect)
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

   private fun goToPaymentScreen() {
       var station = listOfStations.getOrNull(selection)
       var price = station!!.price!!
       if(returnTrip) price *=2
       refreshPaymentScreen(station.name,price)
       while(!finish){

           if(coinacpt.hasCoin()){
               coinacpt.acceptCoin()
               var newprice = price - coinacpt.totalCoinsInserted
               refreshPaymentScreen(station.name,newprice)
           }

           if(coinacpt.totalCoinsInserted >= price){
               print("dispense tiket")
               lcd.clear()
               lcd.write("coletc tiket")
               ticketDispenser.print(selection,originstation!!,returnTrip)
               coinacpt.collectCoins()
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
        //TODO
    }

   private fun alternateSelectionMode() {
       usingArrows = !usingArrows

       if(usingArrows){
           return inputSelectionUsingArrows(selection)
       }

       return inputSelection(selection)
       //refreshcren
   }
}