fun Boolean.toInt() = if (this) 1 else 0

fun Int.findParity(): Boolean {
    var y = this xor (this shr 1)
    y = y xor (y shr 2)
    y = y xor (y shr 4)
    y = y xor (y shr 8)
    y = y xor (y shr 16)
    return y and 1 > 0
}

fun Int.flip(dimm:Int) : Int {
    val dimm = dimm - 1
    var result = 0
    for(i in 0..dimm){
        val masked = (this and (0b1 shl i))
        val bitAtI = if(masked > 0b0) 0b1 else 0b0
        result = result or (bitAtI shl (dimm - i))
    }
    return result
}