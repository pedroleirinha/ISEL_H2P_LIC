package org.example

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialEmitter {

    enum class Peripheral { LCD, TICKET }

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
        //println("DADOS A ENVIAR: ${Integer.toBinaryString(data).padStart(10, '0').reversed()}")
        if(addr == Peripheral.LCD){
            HAL.clrBits(mask = 0b00000111) // LIMPA OS 3 BITS QUE VAO SER USADOS
        }else{
            HAL.clrBits(mask = 0b00001111) // LIMPA OS 3 BITS QUE VAO SER USADOS
        }
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
        if(addr == Peripheral.LCD){
            HAL.clrBits(mask = 0b00000111) // LIMPA OS 3 BITS QUE VAO SER USADOS
            HAL.setBits(0b00000100)
        }else{
            HAL.clrBits(mask = 0b00001111) // LIMPA OS 3 BITS QUE VAO SER USADOS
            HAL.setBits(0b00001000)
        }
    }

    fun sendToLCD(data: Int) {
        //println("\nDADOS PARA O LCD")
        sendInSerie(data, Peripheral.LCD)
        //println("CONCLUIDO (LCD)")
    }

    fun sendToTD(data: Int) {
        //println("\nDADOS PARA O TICKET DISPENSER")
        sendInSerie(data, Peripheral.TICKET)
        //ATIVA O ÚLTIMO BIT PARA SINALIZAR QUE TERMINOU A IMPRESSAO
        HAL.setBits(0b01000000)
        //println("CONCLUIDO (TD)")
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