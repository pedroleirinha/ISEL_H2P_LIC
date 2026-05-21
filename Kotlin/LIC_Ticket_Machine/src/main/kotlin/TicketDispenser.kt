package org.example

object TicketDispenser {
    var lastBit = false
    const val stationsBitsSize = 4
    const val PRT_ON = 1
    const val PRT_OFF = 0

    fun init() {
        SerialEmitter.init()
    }

    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int, prt: Int) {
        val roundTripBit = if (roundTrip) "1" else "0"
        val originBits = Integer.toBinaryString(origin).padStart(stationsBitsSize, '0')
        val destinationBits = Integer.toBinaryString(destination).padStart(stationsBitsSize, '0')

        val data = "${prt}${originBits}${destinationBits}${roundTripBit}".toInt(2)

        SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
    }

    fun emitPrintingTicketUp(roundTrip: Boolean, origin: Int, destination: Int) {
        activatePrintingTicket(roundTrip, origin, destination, PRT_ON)
    }

    fun emitPrintingTicketDown(roundTrip: Boolean, origin: Int, destination: Int) {
        activatePrintingTicket(roundTrip, origin, destination, PRT_OFF)
    }

    fun isTicketCollected(): Boolean {
        val bit = HAL.isTicketCollectedBitOn()

        if (!bit && lastBit) {
            return true
        }
        lastBit = bit
        return false
    }
}
