package org.example


fun main() {

    TicketMachine.init()

    while (true) {
        TicketMachine.waitForKeyPressed()

        when {
            TicketMachine.isPaymentState() -> TicketMachine.checkForPaymentCompleted()
            TicketMachine.isTicketEmittingState() -> TicketMachine.checkForTickedCollected()
            Maintenance.isMaintenanceInitialState() -> TicketMachine.printMaintenanceOptions()
        }

        if (Maintenance.isShuttingDownState()) {
            break
        }

        TicketMachine.isMaintenanceModeActive()
    }
}