package org.example

import isel.leic.utils.Time.getTimeInMillis
import isel.leic.utils.Time


// Ler teclas. Funções retornam '0'..'9', 'A'..'D', '#', '*' ou NONE.
object KBD {
    const val NONE = '_'


    val teclas = arrayOf(
        arrayOf('1', '2', '3', 'A'),
        arrayOf('4', '5', '6', 'B'),
        arrayOf('7', '8', '9', 'C'),
        arrayOf('*', '0', '#', 'D')
    )

    // Inicia a classe
    fun init() {
        SerialEmitter.init()
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {
        val row = HAL.readBits(0b0011)
        val col = (HAL.readBits(0b1100) shr 2) //Signed Right Shift de dois bits
        println("Coluna: $col; Linha: $row")

        sendAckBit()

        return teclas[row][col]
    }

    fun sendAckBit() {
        HAL.setBits(mask = 0b10000000) //Define o ACK a 1
        println("ACK SENT a 1")
        Time.sleep(20)
        HAL.clrBits(mask = 0b10000000) //Limpa o ACK para finalizar o processo
        Time.sleep(20)
        println("ACK CLEAR.\n FINISHED")
    }

    fun isValOff(): Boolean {
        return !HAL.isBit(0b10000000)
    }

    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos),
    // ou NONE caso contrário.
    fun waitKey(timeout: Long): Char {
        val time = getTimeInMillis() + timeout
        while (getTimeInMillis() < time) {

            if (!isValOff()) {
                val key = getKey()
                println("KEY: $key pressed")
                return key
            }
        }
        println("NO KEY PRESS")
        return NONE
    }
}