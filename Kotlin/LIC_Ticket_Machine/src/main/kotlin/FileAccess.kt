package org.example

import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

interface DateAccessInterface {
    fun loadStations(): List<Station>
    fun saveStations(stations: List<Station>)
    fun loadCoins(): Array<Coin>
    fun saveCoins(coins: Array<Coin>)
}

class FileAccess : DateAccessInterface {
    val stationsFileName = "stations.csv"
    val coinsFileName = "coins.csv"


    override fun loadStations(): List<Station> {
        var stationCounter = 0
        val list = readFromFile(stationsFileName)
        val stationsList = mutableListOf<Station>()

        val allStations = list.split("\n")
        for (line in allStations) {
            if (line.isEmpty()) break
            val info = line.split(";")
            val price = info[0].toInt()

            stationsList.add(
                Station(stationCounter++, info[2], info[1].toInt(), price)
            )
        }
        return stationsList
    }

    override fun saveStations(stations: List<Station>) {
        var text = ""
        stations.forEach {
            text += "${it.toText()}\n"
        }
        writeToFile(stationsFileName, text)
    }

    fun readFromFile(fileName: String): String {
        var lines = ""
        BufferedReader(FileReader(fileName)).forEachLine {
            lines += "$it\n"
        }

        return lines
    }

    fun writeToFile(fileName: String, info: String) {
        val pw = PrintWriter(fileName)

        val allLines = info.split("\n")
        allLines.forEach {
            pw.write("$it\n")
        }
        pw.close()
    }

    override fun loadCoins(): Array<Coin> {
        val list = readFromFile(coinsFileName)
        val coins = Array(6) { Coin() }

        val allCoins = list.split("\n")

        var index = 0
        for (coinInfo in allCoins) {
            if (coinInfo.isEmpty()) break
            val info = coinInfo.split(";")
            coins[index++] = Coin(info[0].toInt(), info[1].toInt())

        }

        return coins
    }

    override fun saveCoins(coins: Array<Coin>) {
        var text = ""
        coins.forEach {
            text += "${it.faceValue};${it.count}\n"
        }

        writeToFile(coinsFileName, text)
    }
}