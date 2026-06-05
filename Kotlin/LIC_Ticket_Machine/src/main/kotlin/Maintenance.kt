package org.example

import isel.leic.utils.Time.getTimeInMillis
import org.example.TicketMachine.KEYPRESS_TIMEOUT
import org.example.TicketMachine.abortVendingProcess
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

    enum class MAINTENANCEOPTIONS(val string: String, val key: Char) {
        PRINT_TICKET(string = "Print_Ticket", key = '#'),
        STATION_COUNT(string = "Station Cnt", key = 'A'),
        COIN_COUNT(string = "Coins Cnt", key = 'B'),
        RESET_COUNT(string = "Reset Cnt", key = 'C'),
        SHUTDOWN(string = "ShutDown", key = 'D')
    }

    const val CAROUSEL_TIME_REF: Long = 1000
    var carouselTimer: Long = getTimeInMillis()     // Define o tempo limite para avaliar se algo aconteceu
    var maintenanceOptionCounter = 0

    fun getMaintenanceOption(): MAINTENANCEOPTIONS {
        val option = MAINTENANCEOPTIONS.entries[maintenanceOptionCounter]
        incrementMaintenanceOptions()
        return option
    }

    fun incrementMaintenanceOptions() {
        maintenanceOptionCounter = (maintenanceOptionCounter + 1) % MAINTENANCEOPTIONS.entries.size
    }

    fun isMaintenanceBitActive(): Boolean {
        return HAL.isMaintenanceMode()
    }

    fun isMaintenanceModeInactive(): Boolean {
        return HAL.isMaintenanceModeOff()
    }

    fun startCarouselTimer() {
        carouselTimer = getTimeInMillis() + CAROUSEL_TIME_REF
    }

    fun maintenanceRoutine() {
        while (isMaintenanceModeActive()) {

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
        TUI.showMessageCenterAlign("${ICONS.ARROW_UP.code} *- to Print", 1)

        do {
            val key = waitForKeyPressedWithAbort()

            when (key) {
                '*' -> {
                    TUI.showMessageCenterAlign(Stations.getCurrentStation().name, 0)
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
                '#' -> abortVendingProcess()
            }
        }

        finishTicketCollectionProcess()

        while (!TicketDispenser.isTicketCollected() && isMaintenanceModeActive()) {

        }
    }


    fun shutdownRequest() {
        TUI.askConfirmationShutDown()
        do {
            val key = waitForKeyPressedWithAbort()

            when (key) {
                '*' -> shutdownSystem()
            }

        } while (key != '*')

    }

    fun resetCoinsCounters() {

        TUI.showMessageCenterAlign("Reset? Press *", 1)
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