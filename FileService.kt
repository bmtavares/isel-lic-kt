import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

class FileService {
    fun readFromFile(filename:String) : List<String> {
        val br = BufferedReader(FileReader(filename))
        val lines = mutableListOf<String>()
        for (line in br.lines()){
            if(line.isNotEmpty()){
                lines.add(line)
            }
        }
        return lines
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