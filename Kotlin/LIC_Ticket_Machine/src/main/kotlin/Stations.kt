package org.example

data class Station(
    val code: Int,
    val name: String,
    val ticketsSold: Int,
    val price: Int
)

fun Station.toText() = "${price};${ticketsSold};${name}"

object Stations {

    val stationsList = mutableListOf<Station>()
    var stationCount = 0
    var originStation: Station? = null
    var destStation: Station? = null

    fun init() {
        loadStations()
        originStation = stationsList[0]
    }

    fun getCurrentStation(): Station {
        return stationsList[stationCount]
    }

    fun setDestinationStation(index: Int) {
        destStation = stationsList[index]
    }

    fun setOriginStation(index: Int) {
        originStation = stationsList[index]
    }

    fun decrementStationsCount() {
        stationCount = if (stationCount > 0) stationCount - 1 else stationsList.size - 1
    }

    fun incrementStationsCount() {
        stationCount = ++stationCount % stationsList.size
    }

    fun loadStations() {
        var stationCounter = 1
        val list = FileAccess.readStationsFromFile()

        val allStations = list.split("\n")
        for (line in allStations) {
            if (line.isEmpty()) break
            val info = line.split(";")
            stationsList.add(
                Station(stationCounter++, info[2], info[1].toInt(), info[0].toInt())
            )
        }

        setOriginStation((stationsList.find { it.price == 0 }?.code ?: 0))
    }

    fun saveStations() {
        var text = ""
        stationsList.forEach {
            text += "${it.toText()}\n"
        }

        FileAccess.writeStationsToFile(text)
    }
}