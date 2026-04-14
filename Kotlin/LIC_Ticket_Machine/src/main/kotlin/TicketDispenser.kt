package org.example

object TicketDispenser {

    fun init() {
        SerialEmitter.init()
    }

    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int) {
        val roundTripBit = if (roundTrip) "1" else "0"
        val originBits = Integer.toBinaryString(origin).padStart(4, '0')
        val destinationBits = Integer.toBinaryString(destination).padStart(4, '0')
        val prt = '1'

        val data = "${prt}${originBits}${destinationBits}${roundTripBit}".toInt(2)


        HAL.clrBits(mask = 0b00001000)
        SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
        HAL.setBits(mask = 0b00001000)
    }
}
