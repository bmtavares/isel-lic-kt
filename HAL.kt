import isel.leic.UsbPort

class HAL {
    fun readBits(mask: Int): Int = UsbPort.read() and mask

    fun isBit(mask: Int): Boolean = ((UsbPort.read() and mask) > 0)

    fun setBits(mask: Int) = UsbPort.write(UsbPort.read() or mask)

    fun clrBits(mask: Int) = UsbPort.write(UsbPort.read() and mask.inv())

    fun writeBits(mask: Int, value: Int) = UsbPort.write((UsbPort.read() and mask.inv()) or (mask and value))
}