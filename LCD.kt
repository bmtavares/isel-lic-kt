const val LINES = 2
const val COLS = 16



class LCD(_serialEmitter: SerialEmitter){
    var serialEmitter : SerialEmitter = _serialEmitter


    private fun writeByteParallel(rs: Boolean, data: Int) {
        TODO()
    }

    private fun writeByteSerial(rs: Boolean, data: Int) {
        TODO()
    }

    private fun writeByte(rs: Boolean, data: Int) {
        TODO()
    }

    private fun writeCMD(data: Int) {
        TODO()
    }

    private fun data_rs(_b: Boolean, i: Int): Int {
        var dada: Int = i shl 1
        dada = if (_b) (i or 0b00_0000_0000) else (i or 0b00_0000_0001)
        return dada
    }

    fun dataa_creator(_b: Boolean,a: Int): Int {
        //rotates bits to send, for a clear reading
        var result = 0;
       // if(a and 0b0000_0000 == 0b0000_0000){
       //     result += 0b0000_0000;
       // }

        if(a and 0b0000_0001 == 0b0000_0001){
            result += 0b1000_0000;
        }
        if(a and 0b0000_0010 == 0b0000_0010){
            result += 0b0100_0000;
        }
        if(a and 0b0000_0100 == 0b0000_0100){
            result += 0b0010_0000;
        }
        if(a and 0b0000_1000 == 0b0000_1000){
            result += 0b0001_0000;
        }
        if(a and 0b0001_0000 == 0b0001_0000){
            result += 0b0000_1000;
        }
        if(a and 0b0010_0000 == 0b0010_0000){
            result += 0b0000_0100;
        }
        if(a and 0b0100_0000 == 0b0100_0000){
            result += 0b0000_0010;
        }
        if(a and 0b1000_0000 == 0b1000_0000){
            result += 0b0000_0001;
        }
        //result = result shr 1
        if(_b){
            result +=0b1_0000_0000
        }

        return result
    }


    fun init() {
       // serialEmitter.send(Destination.TICKET_DISPENSER,0b1110)

        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_0000))  //Function set
        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_0000))   //Function set
        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_0000))   //Function set
        Thread.sleep(100, 100)
//n = 1 F = 0
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_1000))
        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_1000))   //Display off
        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_0001))   //Display clear
        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_0111))   //Entry mode set testado na aula
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_0110))  //Sets cursor move direction   A0 = 0 = no shift ??
        //and specifies display shift.
        //These operations are
        //performed during data write
        //and read.



        Thread.sleep(100, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_1111))   //Display on/off control
        Thread.sleep(100, 100)
        //fim de init



    }

    fun write(text: String) {
        //serialEmitter.send(Destination.LCD, 32)
        var i = 0
        for (element in text) {
            write(element)
            if (i >= 15){
                serialEmitter.send(Destination.LCD,dataa_creator(false,0b0001_1000)) //
            }
            i++
        }
    }

   fun setDDRAGM(i: Int){
       //i must be 0 .. 127
       var il : Int = i + 127
       serialEmitter.send(Destination.LCD,dataa_creator(false, il))
   }



    fun teste() {

        write("TesteOK yuiuytruytrhj")




    }

    fun write(c: Char) {
        var b = c.toInt()
        serialEmitter.send(Destination.LCD,dataa_creator(true,b))

    }

    fun cursor(line: Int, column: Int) {
        TODO()
    }

    fun clear() {
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_0001))
    }

}