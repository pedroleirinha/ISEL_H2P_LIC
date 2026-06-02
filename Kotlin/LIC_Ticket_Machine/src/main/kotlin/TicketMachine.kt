package org.example

import isel.leic.utils.Time
import isel.leic.utils.Time.getTimeInMillis
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

    var timer: Long = 0
    var lastKey: Int = 0

    fun hasInterruption(): Boolean {
        return CoinAcceptor.isBusy()
    }

    fun abortPickingProcess() {
        state = TicketMachineState.PICK_STATION
        Stations.destStation = null
        TUI.showWelcomeMessage()
    }


    fun abortVendingProcess() {
        state = TicketMachineState.PICK_STATION
        Stations.destStation = null
        CoinAcceptor.ejectCoinsAndCleanDeposit()

        LCD.clear()
        TUI.showMessageCenterAlign("Vending Aborted!")
        Time.sleep(1000)

        TUI.showWelcomeMessage()

    }

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
        when {
            isPaymentCompleted() && CoinAcceptor.isCoinCollectionDone() -> {
                state = TicketMachineState.TICKET
                submitTicket()
                CoinAcceptor.transferTicketCoinsToSafe()
            }

            CoinAcceptor.checkForCoin() -> {
                CoinAcceptor.readAndAcceptCoin()
                printStation()
            }

            CoinAcceptor.isCoinCollectionDone() -> CoinAcceptor.coinHandshake()
        }
    }

    fun pickStation(keyNumber: Int) {
        if (keyNumber < Stations.stationsList.size) {
            Stations.stationCount = keyNumber
            Stations.setDestinationStation(keyNumber)
            println("Destination set to ${Stations.getCurrentStation().name}")
            printStation()
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
                // Se o tempo expirou, o dígito atual é o início de uma nova sequência
                newKey = key.digitToInt()
            }

            lastKey = newKey

            println("Valor acumulado: $newKey")
        }
        return newKey
    }

    fun checkIfTimerIsUp(): Boolean = getTimeInMillis() > timer

    fun waitForKeyPressed() {
        val key = TUI.readKey(5000)

        if (key != NONE) {
            // Atualiza o timer para 5000ms (5 segundos)
            timer = getTimeInMillis() + 5000
        }

        if (state == TicketMachineState.PICK_STATION) {
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

        if (key == NONE) {
            return
        }

        if (state != TicketMachineState.PICK_STATION) {
            when (key) {
                '#' -> abortVendingProcess()
            }
        }

        if (state == TicketMachineState.PAYMENT) {
            when (key) {
                '*' -> toggleRoundTrip()
            }
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
