package org.example

import isel.leic.utils.Time
import org.example.HAL.clrBits
import org.example.HAL.isBit
import org.example.HAL.setBits

object TicketDispenser {
    var lastBit = false
    const val stationsBitsSize = 4
    const val PRT_ON = 1
    const val PRT_OFF = 0

    const val ticketCollectedBit = 0b00010000
    const val ticketSerialBits = 0b00001111
    const val tdSSBit = 0b00001000
    const val sdxBits = 0b00000001
    const val sCLKBits = 0b00000010

    fun isTicketCollectedBitOn(): Boolean {
        return isBit(ticketCollectedBit)
    }



    fun init() {
        SerialEmitter.init()
    }

    fun activatePrintingTicket(roundTrip: Boolean, origin: Int, destination: Int, prt: Int) {
        val roundTripBit = if (roundTrip) "1" else "0"
        val originBits = origin.numToBinStringPadded(stationsBitsSize)
        val destinationBits = destination.numToBinStringPadded(stationsBitsSize)

        val data = "${prt}${originBits}${destinationBits}${roundTripBit}".toInt(2)

        SerialEmitter.send(SerialEmitter.Peripheral.TICKET, data)
    }

    fun emitPrintingTicketUp(roundTrip: Boolean, origin: Int, destination: Int) {
        activatePrintingTicket(roundTrip, origin, destination, PRT_ON)
    }

    fun emitPrintingTicketDown(roundTrip: Boolean, origin: Int, destination: Int) {
        activatePrintingTicket(roundTrip, origin, destination, PRT_OFF)
    }

    fun isTicketCollectedBitUp() = isTicketCollectedBitOn()

    fun isTicketCollected(): Boolean {
        val bit = isTicketCollectedBitOn()

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
    while (!TicketDispenser.isTicketCollectedBitOn()) {
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
