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
        //
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
        Thread.sleep(150)
       // serialEmitter.send(Destination.TICKET_DISPENSER,0b1110)

        Thread.sleep(1000, 100)


        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_0000))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_0000))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_0000))
        Thread.sleep(1000, 100)
//n = 1 F = 0
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0011_1000))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_1000))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_0001))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_0111))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0000_1111))
        Thread.sleep(1000, 100)
        //fim de init

        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0001_0100))  //
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(true,0b0100_0001)) // escrita A
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0001_0100))
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(true,0b0100_0101))  //escrita E
        Thread.sleep(1000, 100)
        serialEmitter.send(Destination.LCD,dataa_creator(false,0b0001_0100))
    }

    fun write(text: String) {
        serialEmitter.send(Destination.LCD, 32)
    }

    fun write(c: Char) {
        TODO()
    }

    fun cursor(line: Int, column: Int) {
        TODO()
    }

    fun clear() {
        TODO()
    }

}