package org.example

import org.example.KBD.NONE
import org.example.TUI.nextStation
import org.example.TUI.previousStation

fun main() {

    TUI.init()
    while (true) {

        val key = TUI.readKey()
        if (TUI.beginSellingProcess) {
            CoinAcceptor.readCoin()
        }
        if (key != NONE) {
            when (key) {
                '#' -> {
                    if (TUI.beginSellingProcess) {
                        TUI.submitTicket()
                    } else {
                        TUI.sellTicket()
                    }
                }

                '*' -> {
                    if (TUI.beginSellingProcess) {
                        TUI.toggleRoundTrip()
                    }
                }

                'A' -> nextStation()
                'B' -> previousStation()
                else -> TUI.pickStation(key)
            }
        }
    }
}