import java.io.*

class FileService {
    companion object{
        const val SYMBOLS_FILE = "symbols.txt"
    }
    fun readFromFile(filename:String) : List<String> {
        val lines = mutableListOf<String>()

        try{
            val br = BufferedReader(FileReader(filename))
            for (line in br.lines()){
                if (line.isNotEmpty()) lines.add(line)
            }
            br.close()
        }
        catch (ex:FileNotFoundException){}

        return lines.toList()
    }

    fun writeToFile(filename:String, lines:List<String>) {
        val bw = BufferedWriter(FileWriter(filename))
        bw.flush()
        try{ lines.map{bw.appendLine(it)} }
        catch(ex:Exception){
            println(ex.message)
        }
        finally{
            bw.close()
        }
    }
}