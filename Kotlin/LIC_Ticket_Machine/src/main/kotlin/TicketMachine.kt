package org.example

import isel.leic.utils.Time
import isel.leic.utils.Time.getTimeInMillis
import org.example.KBD.NONE
import org.example.TUI.showTicketPrice
import org.example.TUI.showWelcomeMessage


object TicketMachine {

    const val KEYPRESS_TIMEOUT: Long = 500
    const val KEYPRESS_FOLLOW_TIMEOUT: Long = 5000
    const val INACTIVE_KEYPRESS_TIMEOUT: Long = 10000
    var roundTrip = false
    var shutdown = false

    var inactiveTimer: Long = getTimeInMillis()     // Define o tempo limite para avaliar se algo aconteceu
    var followUpTimer: Long = getTimeInMillis()     // Define o tempo limite para avaliar se algo aconteceu
    var lastKey: Int = 0        // Regista a ultima key pressionada para permitir concatenar numeros ate 16.

    fun isAppRunning() = !shutdown

    fun hasInterruption(): Boolean {
        if (CoinAcceptor.isBusy() || TicketDispenser.isTicketCollectedBitUp() || isMaintenanceModeActive()) {
            return true
        }
        return false
    }

    fun abortVendingProcess() {
        Stations.destStation = null
        CoinAcceptor.ejectCoinsAndCleanDeposit()
        TUI.showAbortVendingMessage()
        showWelcomeMessage()
        firstKey = true
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

        shutdown = Stations.originStation == null
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

    fun submitTicket() {
        println(Stations.originStation?.name + ": " + Stations.destStation?.name)
        TicketDispenser.emitPrintingTicketUp(
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )
    }

    fun finishTicketCollectionProcess() {
        TUI.clearScreen()
        TUI.showMessageCenterAlign("Thank You!", 0)
        TUI.showMessageCenterAlign("Have a nice Trip", 1)

        println(Stations.originStation?.name + ": " + Stations.destStation?.name)
        TicketDispenser.emitPrintingTicketDown(
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )
    }

    fun shutdownSystem() {
        TUI.showShuttingDownMessage()
        CoinAcceptor.saveCoins()
        Stations.saveStations()
        println("Data stored. Shutting Down..")
        Time.sleep(1000)
        shutdown = true
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
            if (!checkIfTimerIsUp(followUpTimer) && newAccumulatedValueKey < 16) {
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
        println("Contadores limpos")
        CoinAcceptor.resetCoinCounters()
        Stations.resetStationsTicketCounters()
    }

    fun startInactiveTimer() {
        println("Inactive timer started")
        inactiveTimer = getTimeInMillis() + INACTIVE_KEYPRESS_TIMEOUT
    }

    fun startFollowUpTimer() {
        followUpTimer = getTimeInMillis() + KEYPRESS_FOLLOW_TIMEOUT
    }

    fun waitForKeyPressedWithAbort(): Char {
        val key = KBD.waitKey(KEYPRESS_TIMEOUT)

        if (key != NONE) {
            // Atualiza o timer para 5000ms (5 segundos)
            startFollowUpTimer()
            startInactiveTimer()
        } else if (checkIfTimerIsUp(followUpTimer)) {
            firstKey = true
        }
        return key
    }

    fun waitForKeyPressed(): Char {
        val key = KBD.waitKey(KEYPRESS_TIMEOUT)
        return key
    }

    fun checkIfTimerIsUp(timeRef: Long): Boolean = getTimeInMillis() > timeRef

    fun inactiveTimeout(): Boolean {
        if (checkIfTimerIsUp(inactiveTimer)) {
            startInactiveTimer()
            println("Inative timeout")
            return true
        }
        return false
    }

    var firstKey = true
    fun pickStationRoutine() {
        showWelcomeMessage()
        do {
            val key = waitForKeyPressedWithAbort()
            if (key != NONE) {
                if (firstKey && (key.isDigit() || key == 'A' || key == 'B')) {
                    TUI.clearScreen()
                    firstKey = false
                }
                when {
                    key == 'A' -> nextStation()
                    key == 'B' -> previousStation()
                    key.isDigit() -> pickStation(keyNumber = checkForFollowupKey(key))
                }
            }

            if (isMaintenanceModeActive() || inactiveTimeout()) return
        } while (key != '#' || (Stations.getCurrentStation().price == 0))

        TUI.showTicketRoundTripInformation(roundTrip)
        paymentRoutine()
    }

    fun paymentRoutine() {
        while (!CoinAcceptor.isPaymentProcessedCompleted(ticketPrice = getTotalTicketPrice())) {
            val key = KBD.waitKey(KEYPRESS_TIMEOUT)

            when (key) {
                '*' -> {
                    toggleRoundTrip()
                    TUI.showTicketRoundTripInformation(roundTrip)
                    showTicketPrice(getTotalTicketPrice().toDouble())
                }

                '#' -> {
                    abortVendingProcess()
                    return
                }
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
        TUI.showPrintingMessage()
        submitTicket()

        while (!TicketDispenser.isTicketCollectedBitUp()) {
            val key = KBD.waitKey(KEYPRESS_TIMEOUT)

            when (key) {
                '#' -> abortVendingProcess()
            }
            if (isMaintenanceModeActive()) return
        }

        finishTicketCollectionProcess()

        while (!TicketDispenser.isTicketCollected()) {
            if (isMaintenanceModeActive()) return
        }

        CoinAcceptor.transferTicketCoinsToSafe()
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
