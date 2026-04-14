package org.example

import isel.leic.UsbPort
import org.example.KBD.NONE
import org.example.TicketDispenser.activatePrintingTicket
import java.io.BufferedReader
import java.io.FileReader

object TUI {
    var firstKey = true

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
                else -> LCD.write(c = key)
            }
        }
    }

    var index =-1

    fun stations() {

        val stations =
            arrayOf<String>("Lisboa", "Porto", "Braga", "Faro", "Viana do Castelo", "Coimbra", "Évora", "Algarve")

        val tecla = KBD.getKey()

        if (tecla != NONE) {

            if (firstKey) {
                LCD.clear()
                firstKey = false
            }

            if (tecla == 'A') {

                if (index < stations.size - 1) {

                    index++
                } else {
                    index = 0
                }
            }

            if (tecla == 'B' ) {

                if(index <= 0 ){

                    index = stations.size-1
                }
                else{
                    index--
                }
            }

            LCD.clear()
            LCD.write(stations[index])

        }

    }
    }


