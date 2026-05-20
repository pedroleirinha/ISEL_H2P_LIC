package org.example

object TicketDispenser {
    var lastBit = false

    fun init() {
        SerialEmitter.init()
    }

    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int, prt: Int) {
        val roundTripBit = if (roundTrip) "1" else "0"
        val originBits = Integer.toBinaryString(origin).padStart(4, '0')
        val destinationBits = Integer.toBinaryString(destination).padStart(4, '0')

        val data = "${prt}${originBits}${destinationBits}${roundTripBit}".toInt(2)

        SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
    }

    fun emitPrintingTicketUp(roundTrip: Boolean, origin: Int, destination: Int) {
        activatePrintingTicket(roundTrip, origin, destination, 1)
    }

    fun emitPrintingTicketDown(roundTrip: Boolean, origin: Int, destination: Int) {
        activatePrintingTicket(roundTrip, origin, destination, 0)
    }


    fun collectTicket() {
        HAL.setBits(0b00010000)
        //Time.sleep(1000)
        HAL.clrBits(0b00010000)
        //Time.sleep(1000)
    }


    fun isTicketCollected(): Boolean {
        val bit = HAL.isBit(0b00010000)

        if (!bit && lastBit) {
            return true
        }
        lastBit = bit
        return false
    }
}
