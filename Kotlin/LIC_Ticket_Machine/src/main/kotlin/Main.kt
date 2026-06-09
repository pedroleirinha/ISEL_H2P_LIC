package org.example

fun main() {
    TicketMachine.init()
    while (TicketMachine.isAppRunning()) {
        when {
            TicketMachine.isMaintenanceModeActive() -> {
                Maintenance.maintenanceRoutine()
            }

            Maintenance.isMaintenanceModeInactive() -> {
                TicketMachine.pickStationRoutine()
            }
        }
    }
    println("SYSTEM DOWN")

}

fun Int.numToBinStringPadded(length: Int, padChar: Char = '0'): String {
    return Integer.toBinaryString(this).padStart(length, padChar)
}

fun Int.numToBinString(): String {
    return Integer.toBinaryString(this)
}
