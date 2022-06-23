class StationService{
    companion object{
        const val FILENAME = "stations.txt"
    }

    constructor(fs: FileService){
        this.fs = fs
        readStations()
    }
    private val fs:FileService
    var originStation : Station? = null
    var listOfStations : MutableList<Station> = mutableListOf()

    fun resetCounters(){
        for (station in listOfStations) station.counter = 0
    }

    private fun readStations(){
        val stationsFile = fs.readFromFile(FILENAME)
        for ((idx,line) in stationsFile.withIndex()){
            line.split(';').let{
                val station = Station(idx, it[2], it[0].toInt(), it[1].toInt())
                if(station.price == 0)
                    if(originStation != null)
                        println("More than one origin station detected")
                    else
                        originStation = station
                listOfStations.add(station)
            }
        }
    }

    fun writeStations(){
        val stationsFile = mutableListOf<String>()
        for(station in listOfStations)
            stationsFile.add(station.serialize())
        fs.writeToFile(FILENAME,stationsFile)
    }
}

data class Station(val ID:Int,val name:String, val price:Int, var counter:Int)

fun Station.serialize() : String {
    return "${this.price};${this.counter};${this.name}"
}