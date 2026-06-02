package org.example

import isel.leic.utils.Time
import isel.leic.utils.Time.getTimeInMillis
import org.example.CoinAcceptor.totalAddedCoinsValue
import org.example.KBD.NONE


object TicketMachine {
    enum class TicketMachineState {
        PICK_STATION,
        PAYMENT,
        TICKET,
        MAINTENANCE
    }

    const val KEYPRESS_TIMEOUT: Long = 1000
    const val KEYPRESS_FOLLOW_TIMEOUT: Long = 5000
    var roundTrip = false
    var state: TicketMachineState = TicketMachineState.PICK_STATION

    var timer: Long = 0     // Define o tempo limite para avaliar se algo aconteceu
    var lastKey: Int = 0    // Regista a ultima key pressionada para permitir concatenar numeros ate 16.


    fun hasInterruption(): Boolean {
        if (CoinAcceptor.isBusy()) {
            return true
        }
        if (isMaintenanceModeActive() && state != TicketMachineState.MAINTENANCE) {
            return true
        }
        return false
    }

    fun abortPickingProcess() {
        if (isPickingStationState()) {
            Stations.destStation = null
            TUI.showWelcomeMessage()
        } else {
            Maintenance.resetMaintenanceState()
        }
    }

    fun abortVendingProcess() {
        state = TicketMachineState.PICK_STATION
        Stations.destStation = null
        CoinAcceptor.ejectCoinsAndCleanDeposit()
        TUI.showAbortVendingMessage()
    }

    fun isMaintenanceModeActive(): Boolean {
        return Maintenance.isMaintenanceBitActive()
    }

    fun initMaintenanceMode() {
        state = TicketMachineState.MAINTENANCE

        if (Maintenance.isMaintenanceInitialState()) {
            printMaintenanceOptions()
        }
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
        TUI.printStation(roundTrip)
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
            TUI.showMessageLeftAlign("Ticket Collected")
            state = TicketMachineState.PICK_STATION

            TicketDispenser.emitPrintingTicketDown(
                roundTrip = roundTrip,
                origin = Stations.originStation?.code ?: 0,
                destination = Stations.destStation?.code ?: 0
            )

            TUI.showWelcomeMessage()
            Stations.incrementDestinationStationSoldTickets()
        }
    }

    fun getTotalTicketPrice(): Int {
        return Stations.getCurrentStation().price * if (roundTrip) 2 else 1
    }

    fun isPaymentCompleted(): Boolean {
        return totalAddedCoinsValue() >= getTotalTicketPrice()
    }

    fun submitTicket() {
        TUI.showPrintingMessage()
        TicketDispenser.emitPrintingTicketUp(
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )
    }

    fun sellTicket() {
        state = TicketMachineState.PAYMENT
        TUI.printStation()
    }

    fun checkForPaymentCompleted() {
        when {
            isPaymentCompleted() && CoinAcceptor.isCoinCollectionDone() -> {
                state = TicketMachineState.TICKET
                submitTicket()
                CoinAcceptor.transferTicketCoinsToSafe()
            }

            CoinAcceptor.checkForCoin() -> {
                CoinAcceptor.readAndAcceptCoin()
                TUI.printStation()
            }

            CoinAcceptor.isCoinCollectionDone() -> CoinAcceptor.coinHandshake()
        }
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

    fun checkIfTimerIsUp(): Boolean = getTimeInMillis() > timer

    fun pickingStationKeyActions(key: Char) {
        if (checkIfTimerIsUp()) {
            abortPickingProcess()
            return
        }

        when (key) {
            '#' -> sellTicket()
            'A' -> nextStation()
            'B' -> previousStation()
            else -> pickStation(keyNumber = checkForFollowupKey(key))
        }
    }

    fun paymentKeyActions(key: Char) {
        when (key) {
            '*' -> toggleRoundTrip()
            '#' -> abortVendingProcess()
        }
    }

    fun waitForKeyPressed() {
        val key = KBD.waitKey(KEYPRESS_TIMEOUT)

        if (key != NONE) {
            // Atualiza o timer para 5000ms (5 segundos)
            timer = getTimeInMillis() + KEYPRESS_FOLLOW_TIMEOUT
        }

        if (state == TicketMachineState.PICK_STATION) {
            pickingStationKeyActions(key)
        } else if (state == TicketMachineState.PAYMENT) {
            paymentKeyActions(key)
        } else if (state == TicketMachineState.MAINTENANCE) {
            Maintenance.maintenanceKeyActions(key)
        }
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

        // Se estiver em PICK_STATION ou PAYMENT, processa as teclas
        if (TicketMachine.isPickingStationState() || TicketMachine.isPaymentState()) {
            TicketMachine.waitForKeyPressed()
        }

        // Se estiver em PAYMENT, monitoriza a inserção de moedas até o valor inserido cobrir o valor para o bilhete
        if (TicketMachine.isPaymentState()) {
            TicketMachine.checkForPaymentCompleted()
        }

        // Se estiver em TICKET, aguarda que o bilhete seja emitido e coletado
        if (TicketMachine.isTicketEmittingState()) {
            // Verifica se o utilizador retirou o bilhete
            TicketMachine.checkForTickedCollected()
        }

        Time.sleep(50)
    }
}
