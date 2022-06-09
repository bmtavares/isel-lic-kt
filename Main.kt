import isel.leic.UsbPort
import isel.leic.utils.*

/** If this constant is set, the program will try to run all possible tests.
 *  Best used with UsbPort simulator. */
const val TEST_MODE = true
private val hal = HAL()
private val serialEmitter = SerialEmitter(hal)
private val kbd = KBD(hal)
private val ticketDispenser = TicketDispenser(serialEmitter)
private val lcd = LCD(serialEmitter)

fun main(args: Array<String>) {
        if(TEST_MODE)
            //testFPGA()
            runTests()
        else {
//            var Inat: Int = UsbPort.read()
//            while(true) {
//                val keyCode = kbd.getKey()
//                if (keyCode != NONE)
//                    println(keyCode)
//                //val keyCode = kbd.getKey()
//                //if (keyCode != NONE)
//                //   println(keyCode)
//                Inat = UsbPort.read()
//                if (Inat and 0b0000_1000 >= 1) {
//                    println(Inat)
//                    UsbPort.write(0b0010_0000)
//
//                    Inat = UsbPort.read()
//                    UsbPort.write(0b0000_0000)
//                }
//            }
        }
    }

fun runTests() {
    hal.unitTest()
    serialEmitter.unitTest()
    ticketDispenser.unitTest()
    lcd.unitTest()
    kbd.unitTest()

    println("Tests finished. Close the UsbPort Simulator to stop.")
}

/** Sets the LEDs on a back and forth pattern at a variable speed by using the switches.
 *  To be used with UsbPortDebug project. */
fun testFPGA() {
    var value = 1
    var multiplier = 1
    while(true) {
        val time = UsbPort.read()

        UsbPort.write(value)
        when(multiplier){
            1 -> value *= 2
            -1 -> value /= 2
        }
        if (value == 128 || value == 1 ) multiplier *= -1
        if(time > 0)
            Time.sleep(time.toLong())
        else
            Time.sleep(200)
    }
}