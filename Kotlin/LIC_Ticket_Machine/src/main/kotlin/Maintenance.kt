package org.example

import org.example.KBD.NONE
import org.example.TicketMachine.isMaintenanceModeActive
import org.example.TicketMachine.nextCoinCount
import org.example.TicketMachine.nextStationTicketsSold
import org.example.TicketMachine.previousCoinCount
import org.example.TicketMachine.previousStationTicketsSold
import org.example.TicketMachine.printMaintenanceOptions
import org.example.TicketMachine.resetCounters
import org.example.TicketMachine.shutdownSystem
import org.example.TicketMachine.waitForKeyPressed

object Maintenance {

    enum class MAINTENANCEOPTIONS(val string: String, val key: Char) {
        PRINT_TICKET(string = "Print_Ticket", key = '#'),
        STATION_COUNT(string = "Station Cnt", key = 'A'),
        COIN_COUNT(string = "Coins Cnt", key = 'B'),
        RESET_COUNT(string = "Reset Cnt", key = 'C'),
        SHUTDOWN(string = "ShutDown", key = 'D')
    }


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

    fun maintenanceRoutine() {

        while (isMaintenanceModeActive()) {

            printMaintenanceOptions()
            val key = waitForKeyPressed()

            when (key) {
                'A' -> stationTicketCount()
                'B' -> coinsDepositCount()
                'C' -> resetCoinsCounters()
                'D' -> shutdownRequest()

                '#' -> {
                    maintenanceSellingProcess()
                    Stations.stationCount = 0
                    TUI.printStation()
                }
            }
        }

        TUI.showWelcomeMessage()

    }

    fun maintenanceSellingProcess() {
        Stations.stationCount = 0
        TUI.printStationCount()

        do {
            val key = waitForKeyPressed()

            when {
                key == 'A' -> nextStationTicketsSold()
                key == 'B' -> previousStationTicketsSold()
                key.isDigit() -> {
                    Stations.stationCount = key.digitToInt()
                    TUI.printStationCount()
                }
            }

        } while (key != '#')

        maintenancePaymentProcess()
    }

    fun maintenancePaymentProcess() {
        TUI.showMessageCenterAlign("${ICONS.ARROW_UP.code} *- to Print", 1)

        do {
            val key = waitForKeyPressed()

            when (key) {
                '*' -> {
                    TUI.showMessageCenterAlign(Stations.getCurrentStation().name, 0)
                    TUI.showMessageCenterAlign("Collect Ticket", 1)
                }
            }

        } while (key != '#')

        maintenancePrintingTicketProcess()
    }


    fun maintenancePrintingTicketProcess() {
        TUI.showMessageCenterAlign("${ICONS.ARROW_UP.code} *- to Print", 1)

        do {
            val key = waitForKeyPressed()

        } while (key != '*')

    }


    fun shutdownRequest() {
        TUI.askConfirmationShutDown()
        do {
            val key = waitForKeyPressed()

            when (key) {
                '*' -> shutdownSystem()
            }

        } while (key != NONE && key != '#')

    }

    fun resetCoinsCounters() {
        TUI.showMessageCenterAlign("Reset? Press *", 1)
        do {
            val key = waitForKeyPressed()

            when (key) {
                '*' -> resetCounters()
            }

        } while (key != NONE && key != '#')
    }

    fun stationTicketCount() {
        LCD.clear()
        TUI.printStationTicketsSold()
        do {
            val key = waitForKeyPressed()

            when (key) {
                'A' -> nextStationTicketsSold()
                'B' -> previousStationTicketsSold()
            }

        } while (key != NONE && key != '#')
    }

    fun coinsDepositCount() {
        LCD.clear()
        TUI.printCoinsCount()
        do {
            val key = waitForKeyPressed()

            when (key) {
                'A' -> nextCoinCount()
                'B' -> previousCoinCount()
            }

        } while (key != NONE && key != '#')
    }
}