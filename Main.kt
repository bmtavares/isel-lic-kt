import isel.leic.UsbPort
import isel.leic.utils.*

/** If this constant is set, the program will try to run all possible tests.
 *  Best used with UsbPort simulator. */
const val TEST_MODE = false
private val hal = HAL()
private val serialEmitter = SerialEmitter(hal)
private val kbd = KBD(hal)
private val ticketDispenser = TicketDispenser(serialEmitter)

fun main(args: Array<String>) {
        if(TEST_MODE)
            testFPGA()
            //runLCDTest()
            //runTests()
        else {
            val lcd = LCD(serialEmitter)
            lcd.init()
            lcd.clear()
            lcd.write("LCD initted")
            while(true){
                val keyCode = kbd.getKey()
                if (keyCode != NONE)
                    println(keyCode)
            }
        }
    }

fun runTests() {
    //hal.unitTest()
    //serialEmitter.unitTest()
    ticketDispenser.unitTest()

    println("Tests finished. Close the UsbPort Simulator to stop.")
}

fun runLCDTest() {
    // val ticketDispenser = TicketDispenser(serialEmitter)
    val lcd = LCD(serialEmitter)
    lcd.init()
    print("_yessss___")
    for(i in 0..100){
        lcd.clear()
        lcd.teste()
    }
    lcd.clear()
    lcd.write("Test finished")
    print("_yessss___")

    //ticketDispenser.main()
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