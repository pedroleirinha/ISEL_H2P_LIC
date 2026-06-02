package org.example

import org.example.TicketMachine.TicketMachineState
import org.example.TicketMachine.abortPickingProcess
import org.example.TicketMachine.checkIfTimerIsUp
import org.example.TicketMachine.nextCoinCount
import org.example.TicketMachine.nextStationTicketsSold
import org.example.TicketMachine.previousCoinCount
import org.example.TicketMachine.previousStationTicketsSold
import org.example.TicketMachine.resetCounters
import org.example.TicketMachine.shutdownSystem
import org.example.TicketMachine.state

object Maintenance {
    enum class MaintenanceState {
        ROTATION,
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
    var maintenanceState = MaintenanceState.ROTATION

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


    fun resetMaintenanceState() {
        maintenanceState = MaintenanceState.ROTATION
    }

    fun isMaintenanceInitialState(): Boolean {
        return state == TicketMachineState.MAINTENANCE && maintenanceState == MaintenanceState.ROTATION
    }

    fun isShowTicketsState(): Boolean {
        return maintenanceState == MaintenanceState.TICKETS
    }

    fun isShowCoinsState(): Boolean {
        return maintenanceState == MaintenanceState.COINS
    }

    fun isResetState(): Boolean {
        return maintenanceState == MaintenanceState.RESET_COUNTERS
    }

    fun isShuttingDownState(): Boolean {
        return maintenanceState == MaintenanceState.SHUTTING_DOWN
    }


    fun setShowTicketsState() {
        maintenanceState = MaintenanceState.TICKETS
    }

    fun setShowCoinsState() {
        maintenanceState = MaintenanceState.COINS
    }

    fun setResetCountersState() {
        maintenanceState = MaintenanceState.RESET_COUNTERS
    }

    fun setShuttingDownState() {
        maintenanceState = MaintenanceState.SHUTTING_DOWN
    }

    fun maintenanceKeyActions(key: Char) {
        if (checkIfTimerIsUp()) {
            abortPickingProcess()
            return
        }

        if (isMaintenanceInitialState()) {
            when (key) {
                'A' -> {
                    setShowTicketsState()
                    TUI.printStationTicketsSold()
                }

                'B' -> {
                    setShowCoinsState()
                    TUI.printCoinsCount()
                }

                'C' -> {
                    setResetCountersState()
                    TUI.showMessageCenterAlign("Reset? Press *", 1)
                }

                'D' -> {
                    setShuttingDownState()
                    shutdownSystem()
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
        }
    }
}