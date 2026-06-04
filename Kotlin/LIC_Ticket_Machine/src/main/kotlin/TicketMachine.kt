package org.example

import isel.leic.utils.Time.getTimeInMillis
import org.example.CoinAcceptor.totalAddedCoinsValue
import org.example.KBD.NONE
import org.example.TUI.showTicketPrice


object TicketMachine {

    const val KEYPRESS_TIMEOUT: Long = 1000
    const val KEYPRESS_FOLLOW_TIMEOUT: Long = 5000
    var roundTrip = false

    var timer: Long = 0     // Define o tempo limite para avaliar se algo aconteceu
    var lastKey: Int = 0    // Regista a ultima key pressionada para permitir concatenar numeros ate 16.


    fun hasInterruption(): Boolean {
        if (CoinAcceptor.isBusy()) {
            return true
        }
        return false
    }

    fun abortPickingProcess() {
        if (!isMaintenanceModeActive()) {
            Stations.destStation = null
            TUI.showWelcomeMessage()
        } else {
            Maintenance.maintenanceOptionCounter = 0
            printMaintenanceOptions()
        }
    }

    fun abortVendingProcess() {
        Stations.destStation = null
        CoinAcceptor.ejectCoinsAndCleanDeposit()
        TUI.showAbortVendingMessage()
    }

    fun isMaintenanceModeActive(): Boolean {
        return Maintenance.isMaintenanceBitActive()
    }

    fun printMaintenanceOptions() {
        val option = Maintenance.getMaintenanceOption()
        TUI.printMaintenanceOption(option)
    }

    fun init() {
        TUI.init()
        Stations.init()
        TicketDispenser.init()
        CoinAcceptor.init()
    }

    fun nextStation() {
        Stations.incrementStationsCount()
        TUI.printStation()
    }

    fun previousStation() {
        Stations.decrementStationsCount()
        TUI.printStation()
    }

    fun nextStationTicketsSold() {
        Stations.incrementStationsCount()
        TUI.printStationTicketsSold()
    }

    fun previousStationTicketsSold() {
        Stations.decrementStationsCount()
        TUI.printStationTicketsSold()
    }

    fun nextCoinCount() {
        CoinAcceptor.incrementCoinsCount()
        TUI.printCoinsCount()
    }

    fun previousCoinCount() {
        CoinAcceptor.decrementCoinsCount()
        TUI.printCoinsCount()
    }

    fun toggleRoundTrip() {
        roundTrip = !roundTrip
    }

    fun getTotalTicketPrice(): Int {
        return Stations.getCurrentStation().price * if (roundTrip) 2 else 1
    }

    fun isPaymentCompleted(): Boolean {
        return totalAddedCoinsValue() >= getTotalTicketPrice()
    }

    fun submitTicket() {
        TicketDispenser.emitPrintingTicketUp(
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )
    }

    fun shutdownSystem() {
        TUI.showShuttingDownMessage()
        CoinAcceptor.saveCoins()
        Stations.saveStations()
    }

    fun pickStation(keyNumber: Int) {
        if (keyNumber < Stations.stationsList.size) {
            Stations.stationCount = keyNumber
            Stations.setDestinationStation(keyNumber)
            println("Destination set to ${Stations.getCurrentStation().name}")
            TUI.printStation()
        }
    }

    fun checkForFollowupKey(key: Char): Int {
        var newKey = lastKey
        if (key.isDigit()) {

            val newAccumulatedValueKey = "$lastKey$key".toInt()

            // Se premir dentro do intervalo de 5 segundos, acumula
            if (!checkIfTimerIsUp() && newAccumulatedValueKey < 16) {
                newKey = newAccumulatedValueKey
            } else {
                // Se o tempo expirou, o dígito atual é o início de uma nova sequência de numeros
                newKey = key.digitToInt()
            }

            lastKey = newKey

            println("Valor acumulado: $newKey")
        }
        return newKey
    }

    fun resetCounters() {
        CoinAcceptor.resetCoinCounters()
    }

    fun waitForKeyPressed(): Char {
        val key = KBD.waitKey(KEYPRESS_TIMEOUT)

        if (key != NONE) {
            // Atualiza o timer para 5000ms (5 segundos)
            timer = getTimeInMillis() + KEYPRESS_FOLLOW_TIMEOUT
        } else {
            if (checkIfTimerIsUp()) {
                abortPickingProcess()
                firstKey = true
            }
            return NONE
        }
        return key
    }

    fun checkIfTimerIsUp(): Boolean = getTimeInMillis() > timer


    var firstKey = true
    fun pickStationRoutine() {
        do {
            val key = waitForKeyPressed()
            if (key != NONE) {
                if (firstKey) {
                    LCD.clear()
                    firstKey = false
                }
                when (key) {
                    'A' -> nextStation()
                    'B' -> previousStation()
                    else -> pickStation(keyNumber = checkForFollowupKey(key))
                }
            }

            if (isMaintenanceModeActive()) return
        } while (key != '#')

        TUI.showTicketRoundTripInformation(roundTrip)
        paymentRoutine()
    }

    fun paymentRoutine() {
        while (!(isPaymentCompleted() && !CoinAcceptor.checkForCoin())) {
            val key = waitForKeyPressed()

            when (key) {
                '*' -> {
                    toggleRoundTrip()
                    TUI.showTicketRoundTripInformation(roundTrip)
                }

                '#' -> abortVendingProcess()
            }


            when {
                CoinAcceptor.checkForNewCoin() -> {
                    CoinAcceptor.readAndAcceptCoin()
                    showTicketPrice(getTotalTicketPrice().toDouble())
                }

                CoinAcceptor.isCoinCollectionDone() -> CoinAcceptor.coinHandshake()
            }
            if (isMaintenanceModeActive()) return
        }

        ticketRoutine()

    }

    fun ticketRoutine() {
        CoinAcceptor.transferTicketCoinsToSafe()
        TUI.showPrintingMessage()
        submitTicket()

        while (!TicketDispenser.isTicketCollected()) {
            val key = waitForKeyPressed()

            when (key) {
                '#' -> abortVendingProcess()
            }
            if (isMaintenanceModeActive()) return
        }

        TUI.showMessageLeftAlign("Ticket Collected")

        TicketDispenser.emitPrintingTicketDown(
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )

        Stations.incrementDestinationStationSoldTickets()
        TUI.showWelcomeMessage()
    }
}

fun main() {
    TicketMachine.init()

    println(" <- TicketMachine -> ")
    println("Ticket Machine iniciada [Modo Venda].")
    println("Comandos: Digitos (0-9) ou A/B para selecionar estação.")
    println("'#' para iniciar pagamento.")
    println("'*' para alternar Ida/Volta (no estado de pagamento).")

    while (true) {

        TicketMachine.pickStationRoutine()
    }
}
