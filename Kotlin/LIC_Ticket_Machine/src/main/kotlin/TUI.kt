package org.example

import isel.leic.UsbPort
import org.example.KBD.NONE
import org.example.TicketDispenser.activatePrintingTicket
import java.io.BufferedReader
import java.io.FileReader

data class Station(
    val numb: Int,
    val name: String,
    val price: Int = 0

)

object TUI {
    var firstKey = true
    val stations = mutableListOf<Station>()
    var stationCount = -1
    var originStation: Station? = null
    var destStation: Station? = null

    fun readStations() {
        BufferedReader(FileReader("stations.csv")).forEachLine {
            val info = it.split(";")
            stations.add(Station(info[0].toInt(), info[1]))
        }
    }

    fun showStation() {
        LCD.clear()
        stationCount %= stations.size
        LCD.write(text = stations[stationCount].name)
    }

    fun init() {
        HAL.init()
        KBD.init()
        LCD.init()
        TicketDispenser.init()

        readStations()
    }

    fun nextStation() {
        stationCount++
        showStation()
    }

    fun previousStation() {
        stationCount = if (stationCount > 0) stationCount - 1 else stations.size - 1
        showStation()
    }


    fun readKey() {
        println(Integer.toBinaryString(UsbPort.read()).padStart(8, '0'))
        val key = KBD.waitKey(timeout = 6000)
        if (key != NONE) {
            if (firstKey) {
                LCD.clear()
                firstKey = false
            }

            when (key) {
                '0' -> activatePrintingTicket(
                    roundTrip = true,
                    origin = originStation?.numb ?: 0,
                    destination = destStation?.numb ?: 0
                )

                'A' -> nextStation()
                'B' -> previousStation()
                '*' -> originStation = stations[stationCount]
                '#' -> destStation = stations[stationCount]
                else -> LCD.write(c = key)
            }
        }
    }
}


