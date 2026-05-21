package org.example

import java.io.BufferedReader
import java.io.FileReader

data class Station(
    val code: Int,
    val name: String,
    val distance: Int,
    val price: Int
)


object Stations {

    val stationsList = mutableListOf<Station>()
    var stationCount = 0
    var originStation: Station? = null
    var destStation: Station? = null

    fun init() {
        readStationsFromFile()

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

    fun readStationsFromFile() {
        var stationCounter = 1
        BufferedReader(FileReader("stations.csv"))
            .forEachLine {
                val info = it.split(";")
                stationsList.add(Station(stationCounter++, info[2], info[1].toInt(), info[0].toInt()))
            }
        setOriginStation((stationsList.find { it.price == 0 }?.code ?: 0))
    }
}