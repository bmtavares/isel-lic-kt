import isel.leic.UsbPort

class HAL {
    companion object{
        const val SS_MASK = 0b0000_0100
        const val SCLK_MASK = 0b0000_0010
        const val SDX_MASK = 0b0000_0001
    }

    /*** Last value written using [UsbPort] */
    private var output = 0b0000_0000

    fun readBits(mask: Int): Int = UsbPort.read() and mask

    fun isBit(mask: Int): Boolean = (readBits(mask) > 0)

    fun setBits(mask: Int){
        output = output or mask
        UsbPort.write(output)
    }

    fun clrBits(mask: Int){
        output = output and mask.inv()
        UsbPort.write(output)
    }

    fun writeBits(mask: Int, value: Int){
        output = (output and mask.inv()) or (mask and value)
        UsbPort.write(output)
    }
}