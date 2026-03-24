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

    // Envia um a trama para o Serial Receiver
    // identificado o periférico de destino em 'addr',
    // os bits de dados em 'data'
    // e em 'size' o número de bits a enviar.
    fun send(addr: Peripheral, data: Int) {
        if (isBusy()) return
        if (addr == Peripheral.LCD) {
            busy = true
            println("\nNova Trama")


            /*
            * O BIT (0) VAI SER USADO PARA ENVIAR O COMANDO (SDX)
            * O BIT (1) VAI SER O CLOCK DO SERIAL RECIEVER (SCKL)
            * O BIT (2) VAI SER O ENABLE DO PROCESSO [ACTIVE LOW].
            *
            * */

            println("DADOS A ENVIAR: ${Integer.toBinaryString(data).padStart(10, '0').reversed()}")


            HAL.clrBits(mask = 0b00000111) // LIMPA OS 3 BITS QUE VAO SER USADOS
            //------000
            Integer.toBinaryString(data).padStart(10, '0').reversed().mapIndexed { index, it ->

                if (it.digitToInt() == 1) {
                    HAL.setBits(mask = 0b00000001) //Fica o ultimo bit ON
                } else {
                    HAL.clrBits(mask = 0b00000001) //Fica o ultimo bit OFF
                }

                HAL.setBits(mask = 0b00000010)
                /*println("index: $index -> val: $it")*/
                Time.sleep(10)
                HAL.clrBits(mask = 0b00000010)
                Time.sleep(10)
            }
            HAL.clrBits(0b00000111)
            HAL.setBits(0b00000100)
            println("Fim do Envio de dados \n")
            busy = false
        }
    }

    // Retorna informação se o periférico está ocupado
    fun isBusy(): Boolean {
        return busy
    }
}