fun Boolean.toInt() = if (this) 1 else 0

fun Int.findParity(): Boolean {
    var y = this xor (this shr 1)
    y = y xor (y shr 2)
    y = y xor (y shr 4)
    y = y xor (y shr 8)
    y = y xor (y shr 16)
    return y and 1 > 0
}