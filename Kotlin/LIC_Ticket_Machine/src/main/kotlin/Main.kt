package org.example


fun main() {

    TicketMachine.init()

    while (true) {
        TicketMachine.waitForKeyPressed()

        when {
            TicketMachine.isPaymentState() -> TicketMachine.checkForPaymentCompleted()
            TicketMachine.isTicketEmittingState() -> TicketMachine.checkForTickedCollected()
            TicketMachine.isMaintenanceState() -> TicketMachine.printMaintenanceOptions()
        }

        TicketMachine.isMaintenanceModeActive()
    }
}