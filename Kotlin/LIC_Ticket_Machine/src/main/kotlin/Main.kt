package org.example

import org.example.KBD.NONE
import org.example.TUI.nextStation
import org.example.TUI.previousStation

fun main() {

    TUI.init()
    while (true) {

        val key = TUI.readKey()

        if (TUI.state == TicketMachineState.PAYMENT) {
            if(CoinAcceptor.isTicketPaymentCompleted(Stations.getCurrentStation().price))
            {
                TUI.state = TicketMachineState.TICKET
                TUI.submitTicket()
                CoinAcceptor.transferTicketCoinsToSafe()
            }else{
                CoinAcceptor.readCoin()
            }
        }

        if (TicketDispenser.isTicketCollected()) {
            println("Ticket Collected")
        }


        if (key != NONE) {
            when (key) {
                '#' -> {
                    when(TUI.state){
                        TicketMachineState.PICK_STATION -> TUI.sellTicket()
                        TicketMachineState.TICKET -> TUI.submitTicket()
                        else ->TUI.sellTicket()
                    }
                }

                '*' -> {
                    if (TUI.state == TicketMachineState.PAYMENT) {
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