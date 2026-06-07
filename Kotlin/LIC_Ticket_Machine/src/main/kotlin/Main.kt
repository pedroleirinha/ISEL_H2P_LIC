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

fun Int.numToBinStringPadded(length: Int, padChar: Char = '0'): String {
    return Integer.toBinaryString(this).padStart(length, padChar)
}
fun Int.numToBinString(): String {
    return Integer.toBinaryString(this)
}
