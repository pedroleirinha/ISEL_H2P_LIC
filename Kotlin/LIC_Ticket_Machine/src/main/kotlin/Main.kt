package org.example

import org.example.KBD.NONE

fun main() {

    TUI.init()
    while (true) {

        val key = TUI.readKey()
        if (key != NONE) {
            when (key) {
                '#' -> TUI.sellTicket()
                else -> LCD.write(c = key)
            }
        }
    }
}