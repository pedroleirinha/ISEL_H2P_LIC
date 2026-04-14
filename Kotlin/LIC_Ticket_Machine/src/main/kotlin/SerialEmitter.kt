package org.example

import isel.leic.utils.Time

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialEmitter {
    var busy = false

    enum class Peripheral { LCD, TICKET }

    // Inicia a classe
    fun init() {
        busy = false
    }

    fun sendInSerie(data: Int) {
        /*
       * O BIT (0) VAI SER USADO PARA ENVIAR O COMANDO (SDX)
       * O BIT (1) VAI SER O CLOCK DO SERIAL RECIEVER (SCKL)
       * O BIT (2) VAI SER O ENABLE DO PROCESSO [ACTIVE LOW].
       *
       * */
        println("DADOS A ENVIAR: ${Integer.toBinaryString(data).padStart(10, '0').reversed()}")
        HAL.clrBits(mask = 0b00000111) // LIMPA OS 3 BITS QUE VAO SER USADOS
        Integer.toBinaryString(data).padStart(10, '0')
            .reversed()
            .mapIndexed { index, it ->

                if (it.digitToInt() == 1) {
                    HAL.setBits(mask = 0b00000001) //Fica o ultimo bit ON
                } else {
                    HAL.clrBits(mask = 0b00000001) //Fica o ultimo bit OFF
                }

                HAL.setBits(mask = 0b00000010)
                /*println("index: $index -> val: $it")*/
                HAL.clrBits(mask = 0b00000010)
            }
        HAL.clrBits(0b00000111)
        HAL.setBits(0b00000100)
    }

    fun sendToLCD(data: Int) {
        busy = true
        println("\nDADOS PARA O LCD")
        sendInSerie(data)
        println("CONCLUIDO (LCD)")
        busy = false
    }

    fun sendToTD(data: Int) {
        busy = true
        println("\nDADOS PARA O TICKET DISPENSER")
        sendInSerie(data)
        //ATIVA O ÚLTIMO BIT PARA SINALIZAR QUE TERMINOU A IMPRESSAO
        HAL.setBits(0b01000000)
        println("CONCLUIDO (TD)")
        busy = false
    }

    // Envia um a trama para o Serial Receiver
    // identificado o periférico de destino em 'addr',
    // os bits de dados em 'data'
    // e em 'size' o número de bits a enviar.
    fun send(addr: Peripheral, data: Int) {
        if (isBusy()) return
        when (addr) {
            Peripheral.LCD -> sendToLCD(data)
            Peripheral.TICKET -> sendToTD(data)
        }
    }

    // Retorna informação se o periférico está ocupado
    fun isBusy(): Boolean {
        return busy
    }
}