import isel.leic.UsbPort

class HAL {
    companion object{
        const val SS_MASK = 0b0000_1000
        const val SCLK_MASK = 0b0000_0100
        const val SDX_MASK = 0b0000_0001
//        const val BUSY_BIT = 0b0000_1000
        const val BUSY_BIT = 0b0100_0000
    }

    private val initialOutput = 0b0000_0000

    /*** Last value written using [UsbPort] */
    private var output = initialOutput

    fun init(){
        output = initialOutput
    }

    /** Read the [UsbPort] at the positions given by the [mask].
     *  ## Example
     *  UsbPort.read() = 0b1110_0010
     *
     *  mask = 0b0001_1111
     *
     *  result = 0b0000_0010
     * */
    fun readBits(mask: Int): Int{
        var Inat: Int = UsbPort.read() and mask
        Inat = UsbPort.read() and mask
        Inat = UsbPort.read() and mask
        return Inat
    }

    /** Read the [UsbPort] at the position given by the [mask] and check if it's set to high.
     *  [mask] **must** only contain one bit.
     *  ## Example
     *  UsbPort.read() = 0b1110_0010
     *
     *  mask = 0b0000_0010
     *
     *  result = true
     * */
    fun isBit(mask: Int): Boolean = (readBits(mask) > 0)

    /** Unconditionally write to the [UsbPort] at the positions given by the [mask].
     *  ## Example
     *  UsbPort.read() = 0b1110_0010
     *
     *  mask = 0b0011_1000
     *
     *  result = 0b1111_1010
     * */
    fun setBits(mask: Int){
        output = output or mask
        UsbPort.write(output)
    }

    /** Set to low the bits at [UsbPort] on the positions given by the [mask].
     *  ## Example
     *  UsbPort.read() = 0b1110_0010
     *
     *  mask = 0b1111_0000
     *
     *  result = 0b0000_0010
     * */
    fun clrBits(mask: Int){
        output = output and mask.inv()
        UsbPort.write(output)
    }

    /** Writes to [UsbPort] on the positions given by the [mask] with [value].
     *  ## Example
     *  UsbPort.read() = 0b1110_0010
     *
     *  mask = 0b1111_0000
     *
     *  value = 0b0011_0110
     *
     *  result = 0b0011_0010
     * */
    fun writeBits(mask: Int, value: Int){
        output = (output and mask.inv()) or (mask and value)
        UsbPort.write(output)
    }

    /** Test function for HAL class.
     *  For best testing conditions, use `simulation = true` in `USB_PORT.properties`. */
    fun unitTest(){
        println("Starting HAL test.")
        println("Writing to 0b1000_1010 to USB.")
        writeBits(0b1111_1111,0b1000_1010)
        println("Check the value of the written bits.")
        println("Please enter a value on the USB output. Reading in 7s.")
        Thread.sleep(7000)
        val sevenToFour = readBits(0b1111_0000)
        val threeToZero = readBits(0b0000_1111)
        println("7 to 4 bits are: [${(sevenToFour shr 4).toString(2)}] and 3 to 0 are [${threeToZero.toString(2)}].")
        Thread.sleep(5000)
        println("Clearing bits 5 to 2.")
        clrBits(0b0011_1100)
        Thread.sleep(5000)
        println("Setting bits 5 to 2.")
        setBits(0b0011_1100)
        println("Set bit 1 to desired state. Will check if isBit in 5s.")
        Thread.sleep(5000)
        println("Bit 1 is ${isBit(0b0000_0010)}.")
        println("Test complete.")
    }
}