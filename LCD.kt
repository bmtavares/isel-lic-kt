const val LINES = 2
const val COLS = 16
const val LINE_2_ADDR = 65

class LCD(private val serialEmitter: SerialEmitter){

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
        clear()                 //Display clear
        writeCMD(0b0000_0111)   //Entry mode set testado na aula
        writeCMD(0b0000_0110)   //Sets cursor move direction   A0 = 0 = no shift ??
                                      //and specifies display shift.
                                      //These operations are
                                      //performed during data write
                                      //and read.

        writeCMD(0b0000_1111)   //Display on/off control
    }

    fun write(text: String) {
        var i = 0
        for (element in text) {
            write(element)
            if (i >= 15){
                writeCMD(0b0001_1000)
            }
            i++
        }
    }

    fun write(c: Char) = writeData(c.toInt())

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