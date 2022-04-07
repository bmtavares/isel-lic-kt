import isel.leic.UsbPort
import isel.leic.utils.*

fun main(args: Array<String>) {
    //test

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