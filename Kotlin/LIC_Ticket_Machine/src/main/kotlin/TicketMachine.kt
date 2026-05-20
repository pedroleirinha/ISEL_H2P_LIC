package org.example

import isel.leic.utils.Time
import org.example.CoinAcceptor.totalAddedCoinsValue
import org.example.KBD.NONE
import org.example.TUI.printStation
import org.example.TUI.showMessageLeftAlign
import org.example.TUI.showWelcomeMessage

object TicketMachine {
    enum class TicketMachineState {
        PICK_STATION,
        PAYMENT,
        TICKET
    }

    var roundTrip = false
    var state: TicketMachineState = TicketMachineState.PICK_STATION


    fun init() {
        TUI.init()
        Stations.init()
        TicketDispenser.init()
        CoinAcceptor.init()
    }

    fun nextStation() {
        Stations.incrementStationsCount()
        printStation()
    }

    fun previousStation() {
        Stations.decrementStationsCount()
        printStation()
    }

    fun toggleRoundTrip() {
        roundTrip = !roundTrip
        printStation(roundTrip)
    }

    fun isPaymentState(): Boolean {
        return state == TicketMachineState.PAYMENT
    }

    fun isTicketEmittingState(): Boolean {
        return state == TicketMachineState.TICKET
    }

    fun isPickingStationState(): Boolean {
        return state == TicketMachineState.PICK_STATION
    }

    fun checkForTickedCollected() {
        if (TicketDispenser.isTicketCollected()) {
            println("Ticket Collected")

            showMessageLeftAlign("Ticket Collected")
            state = TicketMachineState.PICK_STATION

            TicketDispenser.emitPrintingTicketDown(
                roundTrip = roundTrip,
                origin = Stations.originStation?.code ?: 0,
                destination = Stations.destStation?.code ?: 0
            )
            Time.sleep(500)
            showWelcomeMessage()
        }
    }

    fun getTotalTicketPrice(): Int {
        return Stations.getCurrentStation().price * if (roundTrip) 2 else 1
    }

    fun isPaymentCompleted(): Boolean {
        return totalAddedCoinsValue() >= getTotalTicketPrice()
    }

    fun submitTicket() {
        LCD.clear()
        showMessageLeftAlign(message = "Imprimir Ticket")
        TicketDispenser.emitPrintingTicketUp(
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )

    }

    fun sellTicket() {
        state = TicketMachineState.PAYMENT
        printStation()
    }

    fun checkForPaymentCompleted() {

        if (isPaymentCompleted()) {
            state = TicketMachineState.TICKET
            submitTicket()
            CoinAcceptor.transferTicketCoinsToSafe()
        } else if (CoinAcceptor.checkForCoin()) {
            CoinAcceptor.readAndAcceptCoin()
        } else if (CoinAcceptor.isHandshakeDone()) {
            CoinAcceptor.coinHandshake()
        }

    }

    fun pickStation(key: Char) {
        if (key.isDigit()) {
            val keyNumber = key.digitToInt()
            Stations.stationCount = keyNumber
            Stations.setDestinationStation(keyNumber)
            println("Destination set to ${Stations.getCurrentStation().name}")
            printStation()
        }
    }

    fun waitForKeyPressed() {
        val key = TUI.readKey()

        if (key == NONE) return

        if (state == TicketMachineState.PICK_STATION) {
            when (key) {
                '#' -> sellTicket()
                'A' -> nextStation()
                'B' -> previousStation()
                else -> pickStation(key)
            }
        }

        if (state == TicketMachineState.PAYMENT) {
            when (key) {
                '*' -> toggleRoundTrip()
            }
        }
    }
}