package org.example

import isel.leic.utils.Time

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
        val state = !bit && lastBit
        lastBit = bit

        return state
    }
}

fun main() {
    HAL.init()
    TicketDispenser.init()

    println("<- Ticket Dispenser ->")


    val stationOrigin = 0b0001
    val stationDestination = 0b0100
    val isRoundTrip = true

    println("Enviando comando de impressão: Origem $stationOrigin -> Destino $stationDestination")

    // A trama de 10 bits será: [Prt=1][Origin=0001][Dest=0100][RT=1]
    TicketDispenser.emitPrintingTicketUp(isRoundTrip, stationOrigin, stationDestination)

    println("Emitido o primeiro sinal com PRT = 1")
    while (!HAL.isTicketCollectedBitOn()) {
        Time.sleep(100)
        // Aguarda que o bit TicketCollected fique ligado
    }
    println("Detetado o fim da impressao")

    TicketDispenser.emitPrintingTicketDown(isRoundTrip, stationOrigin, stationDestination)
    println("Emitido o ultimo sinal com PRT = 0")

    println("Retire o bilhete para concluir")
    while (!TicketDispenser.isTicketCollected()) {
        Time.sleep(100) // Aguarda a transição de 1 para 0 no sinal Fn
    }

    println("Bilhete recolhido com sucesso. Teste terminado.")
}
