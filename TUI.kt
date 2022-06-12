import java.io.File
import kotlin.system.exitProcess

const val TIMEOUT_FOR_SELECTION = 12000L
const val TIMEOUT_FOR_MAINTENANCE = 1000L
const val TIMEOUT_FOR_MAINTENANCE_PROMPT = 5000L


data class Station(val name:String, val price:Int, var counter:Int)

class TUI( private val lcd:LCD,private val  m:Maintenance, private val  kbd:KBD, private val coinacpt: CoinAcceptor){

    private var usingArrows = false
   private var selection = 0

   private val listOfStations = readStations()



   private fun readStations(): List<Station> {
       val lines = File("stations.txt").readLines()
       val result = emptyList<Station>().toMutableList()
       for(line in lines){
           val values = line.split(';')
           result += Station(values[2],values[0].toInt(),0)
       }

       return result
   }

   fun waitingScreen(){
       lcd.write(" Ticket to Ride ")
       lcd.jumpLine()
       lcd.write("place holder date ")
       while(true){
           if (m.isMaintenanceKey()){
               goToMaintenanceMenu()
           }
           if(kbd.getKey() == '#'){
               initStationSelection()
               goToStationSelection()
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
       inputSelection('0') //todo
       while(true){
           when (val k = kbd.waitKey(TIMEOUT_FOR_SELECTION)){
              // NONE -> return
               '*' -> alternateSelectionMode()
               '#' -> goToPaymentScreen()
               else -> inputSelection(k)
           }
       }
   }

   private fun inputSelection(k:Char) {
       lcd.clear()

       var newSelect = (selection * 10 + (k.toInt()-48)) % 100

       var station = listOfStations.getOrNull(newSelect)

       if(station == null){
           newSelect = (k.toInt()-48)
           station = listOfStations.getOrNull(newSelect)
           if(station == null) newSelect = selection
       }
           lcd.write(station!!.name)
           lcd.jumpLine()
           lcd.write(newSelect.toString())

           lcd.setDDRAGM(76)
           lcd.write(lcd.priceToText(station!!.price!!))
           lcd.setDDRAGM(66)


       selection = newSelect
   }

   private fun goToPaymentScreen() {
       println("TODO")
   }

   private fun alternateSelectionMode() {
       usingArrows = !usingArrows
   }
}