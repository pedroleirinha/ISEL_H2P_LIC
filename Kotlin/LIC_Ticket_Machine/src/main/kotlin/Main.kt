package org.example

import org.example.KBD.NONE

fun main() {

    TUI.init()
    while (true) {

        val key = TUI.readKey()
        if (key != NONE) {
            when (key) {
                '#' -> TUI.sellTicket()
                '0' -> {
                    val key = SerialReceiver.receiveKeyInSerie(9)

                }

                else -> LCD.write(c = key)
            }
        }

    }
}