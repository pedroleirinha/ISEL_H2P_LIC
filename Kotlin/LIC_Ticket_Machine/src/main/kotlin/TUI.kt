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

}