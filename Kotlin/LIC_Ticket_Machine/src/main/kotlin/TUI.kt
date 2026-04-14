package org.example

import isel.leic.UsbPort
import org.example.KBD.NONE
import org.example.TicketDispenser.activatePrintingTicket
import java.io.BufferedReader
import java.io.FileReader

object TUI {
    var firstKey = true
    val stations =
        arrayOf<String>("Lisboa", "Porto", "Braga", "Faro", "Viana do Castelo", "Coimbra", "Evora", "Algarve")

    fun init() {
        HAL.init()
        KBD.init()
        LCD.init()
        TicketDispenser.init()
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
                    LCD.write(stations[index])
                }
                'B' -> {
                    index--
                    index = if(index >= 0 ) index % stations.size else  stations.size  - 1
                    LCD.clear()
                    LCD.write(stations[index])

                }
                else -> LCD.write(c = key)
            }




        }
    }

    var index = 0


    }


