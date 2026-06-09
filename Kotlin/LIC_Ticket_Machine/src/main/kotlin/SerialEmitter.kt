package org.example

import isel.leic.utils.Time
import org.example.HAL.clrBits
import org.example.HAL.setBits
import org.example.LCD.lcdSSBit
import org.example.LCD.lcdSerialBits
import org.example.LCD.sCLKBits
import org.example.LCD.sdxBits
import org.example.TicketDispenser.tdSSBit
import org.example.TicketDispenser.ticketSerialBits

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialEmitter {

    enum class Peripheral { LCD, TICKET }

    const val serialInformationSize = 10

    // Inicia a classe
    fun init() {
        turnOffLcdSS()
        turnOffTdSS()
        clearSCKLBit()
    }

    fun sendInSerie(data: Int, addr: Peripheral) {
        /*
       * O BIT (0) VAI SER USADO PARA ENVIAR O COMANDO (SDX)
       * O BIT (1) VAI SER O CLOCK DO SERIAL RECIEVER (SCKL)
       * O BIT (2) VAI SER O ENABLE DO PROCESSO [ACTIVE LOW].
       *
       * */
        if (addr == Peripheral.LCD) {
            clearLCDSerialBits() // LIMPA OS 3 BITS QUE VAO SER USADOS
        } else {
            clearTDSerialBits() // LIMPA OS 3 BITS QUE VAO SER USADOS
        }
        data.numToBinStringPadded(serialInformationSize)
            .reversed()
            .mapIndexed { index, it ->

                if (it.digitToInt() == 1) {
                    setSDXBit() //Fica o ultimo bit ON
                } else {
                    clearSDXBit() //Fica o ultimo bit OFF
                }

                setSCKLBit()
                clearSCKLBit()
            }

        if (addr == Peripheral.LCD) {
            clearLCDSerialBits()
            turnOffLcdSS()
        } else {
            clearTDSerialBits()
            turnOffTdSS()
        }
    }

    fun sendToLCD(data: Int) {
        sendInSerie(data, Peripheral.LCD)
    }

    fun sendToTD(data: Int) {
        sendInSerie(data, Peripheral.TICKET)
    }

    // Envia um a trama para o Serial Receiver
    // identificado o periférico de destino em 'addr',
    // os bits de dados em 'data'
    // e em 'size' o número de bits a enviar.
    fun send(addr: Peripheral, data: Int) {
        when (addr) {
            Peripheral.LCD -> sendToLCD(data)
            Peripheral.TICKET -> sendToTD(data)
        }
    }

    /* LCD */
    private fun clearLCDSerialBits() {
        clrBits(lcdSerialBits)
    }

    private fun turnOffLcdSS() {
        setBits(mask = lcdSSBit) //Fica o ultimo bit OFF
    }

    private fun setSDXBit() {
        setBits(mask = sdxBits) //Fica o ultimo bit ON
    }

    private fun clearSDXBit() {
        clrBits(mask = sdxBits) //Fica o ultimo bit OFF
    }

    private fun setSCKLBit() {
        setBits(mask = sCLKBits) //Fica o ultimo bit ON
    }

    private fun clearSCKLBit() {
        clrBits(mask = sCLKBits) //Fica o ultimo bit OFF
    }

    private fun clearTDSerialBits() {
        clrBits(ticketSerialBits) // LIMPA OS 3 BITS QUE VAO SER USADOS
    }

    private fun turnOffTdSS() {
        setBits(mask = tdSSBit) //Fica o ultimo bit OFF
    }


    // Retorna informação se o periférico está ocupado
    fun isBusy(): Boolean {
        return !HAL.isBit(0b10000000)
    }
}

fun main() {
    HAL.init()
    SerialEmitter.init()

    println(" <- SerialEmitter -> ")
    println("Iniciando teste do Serial Emitter...")

    // --- TESTE 1: Enviar o carater 'A' com RS = 1 e E = 1
    val dataLCD = 0b1010000011
    println(
        "A enviar trama para o LCD: ${
            dataLCD.numToBinStringPadded(SerialEmitter.serialInformationSize)
        }"
    )

    SerialEmitter.send(SerialEmitter.Peripheral.LCD, dataLCD)

    // Pequena pausa para observação nos LED da placa se necessário
    Time.sleep(1000)

    // --- TESTE 2: Simulação de envio da trama : [Prt][D3...D0][O3...O0][RT]
    // RT=1, Origem=1, Destino=4, Prt=1
    val dataTicket = 0b1010000011
    println("A enviar trama para o Ticket Dispenser...")

    SerialEmitter.send(SerialEmitter.Peripheral.TICKET, dataTicket)

    // --- TESTE 3: Verificação de isBusy ---
    if (SerialEmitter.isBusy()) {
        println("O emitter sinaliza que está ocupado.")
    } else {
        println("O emitter está pronto para nova trama.")
    }

    println("Concluído.")
}
