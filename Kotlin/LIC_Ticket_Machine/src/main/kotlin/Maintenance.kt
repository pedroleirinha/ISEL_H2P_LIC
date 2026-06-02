package org.example

import org.example.TicketMachine.TicketMachineState
import org.example.TicketMachine.nextCoinCount
import org.example.TicketMachine.nextStationTicketsSold
import org.example.TicketMachine.previousCoinCount
import org.example.TicketMachine.previousStationTicketsSold
import org.example.TicketMachine.resetCounters
import org.example.TicketMachine.shutdownSystem
import org.example.TicketMachine.state

object Maintenance {
    enum class MaintenanceState {
        IDLE,
        SELLING,
        SELLING_PAYMENT,
        SELLING_TICKET,
        TICKETS,
        COINS,
        RESET_COUNTERS,
        SHUTTING_DOWN,
    }

    enum class MAINTENANCEOPTIONS(val string: String, val key: Char) {
        PRINT_TICKET(string = "Print_Ticket", key = '#'),
        STATION_COUNT(string = "Station Cnt", key = 'A'),
        COIN_COUNT(string = "Coins Cnt", key = 'B'),
        RESET_COUNT(string = "Reset Cnt", key = 'C'),
        SHUTDOWN(string = "ShutDown", key = 'D')
    }


    var maintenanceOptionCounter = 0
    var maintenanceState = MaintenanceState.IDLE

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

    fun resetMaintenanceState() {
        maintenanceState = MaintenanceState.IDLE
    }

    fun isMaintenanceInitialState(): Boolean {
        return state == TicketMachineState.MAINTENANCE && maintenanceState == MaintenanceState.IDLE
    }

    fun isSellingState(): Boolean {
        return maintenanceState == MaintenanceState.SELLING
    }

    fun isSellingPaymentState(): Boolean {
        return maintenanceState == MaintenanceState.SELLING_PAYMENT
    }

    fun isShowTicketsState(): Boolean {
        return maintenanceState == MaintenanceState.TICKETS
    }

    fun isShowCoinsState(): Boolean {
        return maintenanceState == MaintenanceState.COINS
    }

    fun isSellingTicketState(): Boolean {
        return maintenanceState == MaintenanceState.SELLING_TICKET
    }

    fun isResetState(): Boolean {
        return maintenanceState == MaintenanceState.RESET_COUNTERS
    }

    fun isShuttingDownState(): Boolean {
        return maintenanceState == MaintenanceState.SHUTTING_DOWN
    }

    fun setShowTicketsState() {
        maintenanceState = MaintenanceState.TICKETS
        println(maintenanceState)
    }

    fun setShowCoinsState() {
        maintenanceState = MaintenanceState.COINS
        println(maintenanceState)
    }

    fun setResetCountersState() {
        maintenanceState = MaintenanceState.RESET_COUNTERS
        println(maintenanceState)
    }

    fun setShuttingDownState() {
        maintenanceState = MaintenanceState.SHUTTING_DOWN
        println(maintenanceState)
    }

    fun setSellingTicketState() {
        maintenanceState = MaintenanceState.SHUTTING_DOWN
        println(maintenanceState)
    }

    fun setSellingState() {
        maintenanceState = MaintenanceState.SELLING
        println(maintenanceState)
    }

    fun maintenanceKeyActions(key: Char) {
        if (isMaintenanceInitialState()) {
            when {
                key == 'A' -> {
                    setShowTicketsState()
                    TUI.printStationTicketsSold()
                }

                key == 'B' -> {
                    setShowCoinsState()
                    TUI.printCoinsCount()
                }

                key == 'C' -> {
                    setResetCountersState()
                    TUI.showMessageCenterAlign("Reset? Press *", 1)
                }

                key == 'D' -> {
                    setShuttingDownState()
                    TUI.askConfirmationShutDown()
                }

                key == '#' -> {
                    setSellingState()
                    Stations.stationCount = 0
                    TUI.printStation(true)
                }


            }
        } else if (isSellingState()) {
            when {
                key == 'A' -> nextStationTicketsSold()
                key == 'B' -> previousStationTicketsSold()
                key == '#' -> {
                    LCD.clear()
                    TUI.showMessageCenterAlign(Stations.getCurrentStation().name, 0)
                    TUI.showMessageCenterAlign("${ICONS.ARROW_UP.code} *- to Print", 1)
                    maintenanceState = MaintenanceState.SELLING_PAYMENT
                }

                key.isDigit() -> {
                    setSellingState()
                    Stations.stationCount = key.digitToInt()
                    TUI.printStation(true)
                }
            }
        } else if (isSellingPaymentState()) {
            when (key) {
                '*' -> {
                    TUI.showMessageCenterAlign(Stations.getCurrentStation().name, 0)
                    TUI.showMessageCenterAlign("Collect Ticket", 1)
                    setSellingTicketState()
                }
            }
        } else if (isSellingTicketState()) {
            when (key) {
                '*' -> {
                    resetMaintenanceState()
                }
            }
        } else if (isShowTicketsState()) {
            when (key) {
                'A' -> nextStationTicketsSold()
                'B' -> previousStationTicketsSold()
                '#' -> resetMaintenanceState()
            }
        } else if (isShowCoinsState()) {
            when (key) {
                'A' -> nextCoinCount()
                'B' -> previousCoinCount()
                '#' -> resetMaintenanceState()
            }
        } else if (isResetState()) {
            when (key) {
                '*' -> resetCounters()
                '#' -> resetMaintenanceState()
            }
        } else if (isShuttingDownState()) {
            when (key) {
                '*' -> shutdownSystem()
                '#' -> resetMaintenanceState()
            }
        }
    }
}