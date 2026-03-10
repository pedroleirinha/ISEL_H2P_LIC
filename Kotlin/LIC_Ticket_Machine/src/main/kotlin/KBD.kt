package org.example

import isel.leic.UsbPort
import isel.leic.simul.module.Keyboard

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
        /*Keyboard("Keyboard", "0123A456B789C*0#D", 4, 4, 5)*/
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {

        val blank = HAL.readBits(0b11111111) //Se os 8 bits estiverem a 1, então a tecla é o NONE
        if (blank == 255) return NONE

        val row = HAL.readBits(0b0011)
        val col = (HAL.readBits(0b1100) shr 2) //Signed Right Shift de dois bits
        println("Coluna: $col; Linha: $row")

        return teclas[row][col]
    }

    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos),
    // ou NONE caso contrário.
    fun waitKey(timeout: Long): Char {
        for (i in 0..timeout) {
            val key = getKey()
            if (key != NONE) {
                return key
            }
        }

        return NONE
    }
}