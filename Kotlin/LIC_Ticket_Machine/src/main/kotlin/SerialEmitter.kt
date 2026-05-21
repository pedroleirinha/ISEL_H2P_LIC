package org.example

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialEmitter {

    enum class Peripheral { LCD, TICKET }

    const val serialInformationSize = 10

    // Inicia a classe
    fun init() {

    }

    fun sendInSerie(data: Int, addr: Peripheral) {
        /*
       * O BIT (0) VAI SER USADO PARA ENVIAR O COMANDO (SDX)
       * O BIT (1) VAI SER O CLOCK DO SERIAL RECIEVER (SCKL)
       * O BIT (2) VAI SER O ENABLE DO PROCESSO [ACTIVE LOW].
       *
       * */
        if (addr == Peripheral.LCD) {
            HAL.clearLCDSerialBits() // LIMPA OS 3 BITS QUE VAO SER USADOS
        } else {
            HAL.clearTDSerialBits() // LIMPA OS 3 BITS QUE VAO SER USADOS
        }
        Integer.toBinaryString(data).padStart(serialInformationSize, '0')
            .reversed()
            .mapIndexed { index, it ->

                if (it.digitToInt() == 1) {
                    HAL.setSDXBit() //Fica o ultimo bit ON
                } else {
                    HAL.clearSDXBit() //Fica o ultimo bit OFF
                }

                HAL.setSCKLBit()
                HAL.clearSCKLBit()
            }

        if (addr == Peripheral.LCD) {
            HAL.clearLCDSerialBits()
            HAL.turnOffLcdSS()
        } else {
            HAL.clearTDSerialBits()
            HAL.turnOffTdSS()
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

    // Retorna informação se o periférico está ocupado
    fun isBusy(): Boolean {
        return !HAL.isBit(0b10000000)
    }
}