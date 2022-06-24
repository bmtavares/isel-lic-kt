const val LINES = 2
const val COLS = 16
const val DOT_HEIGHT = 8
const val LINE_2_ADDR = 65
const val START_CGRAM = 0b0
const val END_CGRAM = 0b11_1111

class LCD(private val serialEmitter: SerialEmitter){

    private val customSymbolMap = mutableMapOf<Char,Int>()

    private var nextCGRAMAddr: Int? = START_CGRAM

    private fun writeByte(rs: Boolean, data: Int) {
        val data = if(rs) 0x100 or data.flip(8) else data.flip(8)
        serialEmitter.send(Destination.LCD,data)
    }

    private fun writeCMD(data: Int) = writeByte(false,data)

    fun writeData(data: Int) = writeByte(true,data)

    fun init() {
        writeCMD(0b0011_0000)   //Function set
        writeCMD(0b0011_0000)   //Function set
        writeCMD(0b0011_0000)   //Function set

        //n = 1 F = 0
        writeCMD(0b0011_1000)
        writeCMD(0b0000_1000)   //Display off
        clear()                     //Display clear
        writeCMD(0b0000_0110)   //Entry mode set testado na aula
//        writeCMD(0b0000_0110)   //Sets cursor move direction   A0 = 0 = no shift ??
                                      //and specifies display shift.
                                      //These operations are
                                      //performed during data write
                                      //and read.

        writeCMD(0b0000_1100)   //Display on/off control
    }

    fun home() = writeCMD(0b11)

    fun write(text: String) {
        for ((idx,element) in text.withIndex()) {
            if(element.toInt() in 32..125)
                writeSingle(element)
            else if(customSymbolMap.containsKey(element))
                customSymbolMap[element]?.let { writeSingle(it) }
            else writeSingle(0xFF)

            if (idx >= 16) writeCMD(0b0001_1000) // Shift
        }
    }

    private fun writeSingle(c: Char) = writeData(c.toInt())

    private fun writeSingle(addr: Int) = writeData(addr)

    fun writeCentered(text:String) {
        val pad = (COLS - text.length) / 2
        write(text.padStart(pad+text.length))
    }

    fun cursor(line: Int, column: Int) {
        val lineStart = LINE_2_ADDR * (line - 1)
        val addr = 127 + lineStart + (column - 1)
        writeCMD(addr)
    }

    fun clear() = writeCMD(0b0000_0001)

    fun priceToText(i: Int):String{
        var unidades = 0
        var cent = 0
        cent = i % 100
        unidades = (i - cent)/100
        var s_cent = cent.toString()
        if( cent < 10) s_cent = "0$cent"
        var s = "$unidades.$s_cent$"

        if (cent == 0) s = "$unidades.00$"

        return s
    }

    fun jumpLine(){
        setDDRAGM(65)
    }

    fun setDDRAGM(i: Int){
        //i must be 0 .. 127
        var il : Int = i + 127
        writeCMD(il)
    }

    private fun setCGRAM(i:Int){
        if (i in 0..0x3F){
            val i = 0x40 + i
            writeCMD(i)
        }
    }

    fun writeCharacterPattern(char: Char, symbol: List<Int>): Boolean{
        nextCGRAMAddr?.let {
            setCGRAM(it)
            for(line in symbol){
                writeData(if(line in 0x0..0xFF) line else 0x0)
            }
            clear()
            customSymbolMap[char] = it
            nextCGRAMAddr = if((it + DOT_HEIGHT) > 0xFF) null else it + DOT_HEIGHT
            return true
        }
        return false
    }

    fun unitTest() {
        init()
        print("Starting LCD test")
        for(i in 0..15){
            if (i % 2 == 1) cursor(2,1)
            write("TESTING LIC $i")
            Thread.sleep(500) //Simulator is too fast to test without a sleep
            if (i % 2 == 1) clear()
        }
        write("TestFinish!")
    }
}