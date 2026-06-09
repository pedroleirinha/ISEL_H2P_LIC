package org.example

import isel.leic.utils.Time.getTimeInMillis
import org.example.HAL.isBit
import org.example.TicketMachine.KEYPRESS_TIMEOUT
import org.example.TicketMachine.finishTicketCollectionProcess
import org.example.TicketMachine.inactiveTimeout
import org.example.TicketMachine.isMaintenanceModeActive
import org.example.TicketMachine.nextCoinCount
import org.example.TicketMachine.nextStationTicketsSold
import org.example.TicketMachine.previousCoinCount
import org.example.TicketMachine.previousStationTicketsSold
import org.example.TicketMachine.printMaintenanceOptions
import org.example.TicketMachine.resetCounters
import org.example.TicketMachine.shutdownSystem
import org.example.TicketMachine.waitForKeyPressedWithAbort

object Maintenance {
    const val maintenanceBit = 0b01000000

    enum class MAINTENANCEOPTIONS(val title: String, val key: Char) {
        STATION_COUNT(title = "Station Cnt", key = 'A'),
        COIN_COUNT(title = "Coins Cnt", key = 'B'),
        RESET_COUNT(title = "Reset Cnt", key = 'C'),
        SHUTDOWN(title = "ShutDown", key = 'D'),
        PRINT_TICKET(title = "Print_Ticket", key = '#'),
    }

    const val CAROUSEL_TIME_REF: Long = 1000
    var carouselTimer: Long = getTimeInMillis()     // Define o tempo limite para avaliar se algo aconteceu
    var maintenanceOptionCounter = 0

    fun isMaintenanceMode(): Boolean = isBit(maintenanceBit)
    fun isMaintenanceModeOff(): Boolean = !isBit(maintenanceBit)

    fun getMaintenanceOption(): MAINTENANCEOPTIONS {
        val option = MAINTENANCEOPTIONS.entries[maintenanceOptionCounter]
        incrementMaintenanceOptions()
        return option
    }

    fun incrementMaintenanceOptions() {
        maintenanceOptionCounter = (maintenanceOptionCounter + 1) % MAINTENANCEOPTIONS.entries.size
    }

    fun isMaintenanceBitActive(): Boolean {
        return isMaintenanceMode()
    }

    fun isMaintenanceModeInactive(): Boolean {
        return isMaintenanceModeOff()
    }

    fun startCarouselTimer() {
        carouselTimer = getTimeInMillis() + CAROUSEL_TIME_REF
    }

    fun maintenanceRoutine() {
        startCarouselTimer()

        while (isMaintenanceModeActive()) {
            CoinAcceptor.coinCounter = 0
            Stations.stationCount = 0

            if (TicketMachine.checkIfTimerIsUp(carouselTimer)) {
                printMaintenanceOptions()
                startCarouselTimer()
            }
            val key = TicketMachine.waitForKeyPressed()
            when (key) {
                'A' -> stationTicketCount()
                'B' -> coinsDepositCount()
                'C' -> resetCoinsCounters()
                'D' -> shutdownRequest()

                '#' -> {
                    LCD.clear()
                    maintenanceSellingProcess()
                }
            }
        }

        TUI.showWelcomeMessage()

    }

    fun maintenanceSellingProcess() {
        Stations.stationCount = 0
        TicketMachine.startInactiveTimer()
        TUI.printStation()

        do {
            val key = waitForKeyPressedWithAbort()
            when {
                key == 'A' -> nextStationTicketsSold()
                key == 'B' -> previousStationTicketsSold()
                key.isDigit() -> {
                    Stations.stationCount = key.digitToInt()
                    TUI.printStation()
                }
            }

            if (inactiveTimeout()) return

        } while (key != '#' && isMaintenanceModeActive())

        maintenancePaymentProcess()
    }

    fun maintenancePaymentProcess() {
        TUI.askConfirmationToPrintTicket()

        do {
            val key = waitForKeyPressedWithAbort()

            when (key) {
                '*' -> {
                    TUI.updateStationName(Stations.getCurrentStation().name)
                    TUI.showMessageCenterAlign("Collect Ticket", 1)
                }
            }

        } while (key != '*' && isMaintenanceModeActive())

        maintenancePrintingTicketProcess()
    }

    fun maintenancePrintingTicketProcess() {
        while (!TicketDispenser.isTicketCollectedBitUp() && isMaintenanceModeActive()) {
            val key = KBD.waitKey(KEYPRESS_TIMEOUT)

            when (key) {
                '#' -> {
                    TUI.showAbortVendingMessage()
                    return
                }
            }
        }

        finishTicketCollectionProcess()

        while (!TicketDispenser.isTicketCollected() && isMaintenanceModeActive()) {

        }
    }

    fun shutdownRequest() {
        TicketMachine.startInactiveTimer()

        TUI.askConfirmationShutDown()
        do {
            val key = waitForKeyPressedWithAbort()

            when {
                key == '*' -> shutdownSystem()
                key != KBD.NONE -> break
            }

        } while (key != '*')

    }

    fun resetCoinsCounters() {
        TicketMachine.startInactiveTimer()
        TUI.askConfirmationResetCoins()
        do {
            val key = waitForKeyPressedWithAbort()

            when (key) {
                '*' -> resetCounters()
            }
            if (inactiveTimeout()) return
        } while (key != '#')
    }

    fun stationTicketCount() {
        LCD.clear()
        TicketMachine.startInactiveTimer()
        TUI.printStationTicketsSold()
        do {
            val key = waitForKeyPressedWithAbort()

            when (key) {
                'A' -> nextStationTicketsSold()
                'B' -> previousStationTicketsSold()
            }
            if (inactiveTimeout()) return
        } while (key != '#')
    }

    fun coinsDepositCount() {
        LCD.clear()
        TicketMachine.startInactiveTimer()
        TUI.printCoinsCount()
        do {
            val key = waitForKeyPressedWithAbort()

            when (key) {
                'A' -> nextCoinCount()
                'B' -> previousCoinCount()
            }
            if (inactiveTimeout()) return
        } while (key != '#')
    }
}