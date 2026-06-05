package org.example

fun main() {
    TicketMachine.init()
    while (true) {
        when {
            TicketMachine.isMaintenanceModeActive() -> {
                Maintenance.maintenanceRoutine()
            }

            Maintenance.isMaintenanceModeInactive() -> {
                TicketMachine.pickStationRoutine()
            }
        }
    }
}