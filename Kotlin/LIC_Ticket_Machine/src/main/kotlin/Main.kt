package org.example

import isel.leic.UsbPort

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

}

fun Int.numToBinStringPadded(length: Int, padChar: Char = '0'): String {
    return Integer.toBinaryString(this).padStart(length, padChar)
}
fun Int.numToBinString(): String {
    return Integer.toBinaryString(this)
}
