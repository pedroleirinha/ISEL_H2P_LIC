package org.example

object TicketDispenser {

    fun init() {
        SerialEmitter.init()
    }

    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int) {
        val roundTripBit = if (roundTrip) "1" else "0"
        val originBits = Integer.toBinaryString(origin)
        val destinationBits = Integer.toBinaryString(destination)

        val data = "${roundTripBit}${originBits}${destinationBits}".toInt(2)

        SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
    }
}
