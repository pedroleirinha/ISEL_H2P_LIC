package org.example

import isel.leic.UsbPort
import org.example.KBD.NONE
import org.example.TicketDispenser.activatePrintingTicket
import java.io.BufferedReader
import java.io.FileReader

data class Station(
    val numb: Int,
    val name: String,
    val price: Int=0

)

object TUI {
    var firstKey = true
    val stations = mutableListOf<Station>()
    var index = 0

    fun readStations(){
        BufferedReader(FileReader("stations.csv")).forEachLine {
            val info = it.split(";")
            stations.add(Station(info[0].toInt(),info[1]))

        }
    }

    fun init() {
        HAL.init()
        KBD.init()
        LCD.init()
        TicketDispenser.init()

        readStations()
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
                '*' -> activatePrintingTicket(roundTrip = true, origin = 2, destination = 2)
                'A' -> {
                    index++
                    index %= stations.size
                    LCD.clear()
                    LCD.write(stations[index].name)
                }
                'B' -> {
                    index--
                    index = if(index >= 0 ) index % stations.size else  stations.size  - 1
                    LCD.clear()
                    LCD.write(stations[index].name)

                }
                else -> LCD.write(c = key)
            }




        }
    }




    }


