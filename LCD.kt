const val LINES = 2
const val COLS = 16

class LCD {
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

    fun init() {
        TODO()
    }

    fun write(text: String) {
        TODO()
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