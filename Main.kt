import isel.leic.UsbPort
import isel.leic.utils.*

/** If this constant is set, the program will try to run all possible tests.
 *  Best used with UsbPort simulator. */
const val TEST_MODE = false

fun main(args: Array<String>) {
        if(TEST_MODE)
            runTests()
        else {
            val ticketDispenser = TicketDispenser()
            ticketDispenser.main()
        }
    }

fun runTests() {
    val ticketDispenser = TicketDispenser()
    val serialEmitter = SerialEmitter()
    val hal = HAL()

    hal.unitTest()
    serialEmitter.unitTest()
    ticketDispenser.unitTest()

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