package org.example

import isel.leic.utils.Time.getTimeInMillis


// Ler teclas. Funções retornam '0'..'9', 'A'..'D', '#', '*' ou NONE.
object KBD {
    const val NONE = '_'
    const val keyBitsSize = 4
    const val colsMask = 0b1100
    const val rowsMask = 0b0011

    val teclas = arrayOf(
        arrayOf('1', '2', '3', 'A'),
        arrayOf('4', '5', '6', 'B'),
        arrayOf('7', '8', '9', 'C'),
        arrayOf('*', '0', '#', 'D')
    )

    // Inicia a classe
    fun init() {

    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(keyBits: Int): Char {
        if (keyBits == -1) return NONE

        val row = keyBits and rowsMask
        val col = (keyBits and colsMask) shr 2

        return teclas[row][col]
    }

    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos),
    // ou NONE caso contrário.
    fun waitKey(timeout: Long): Char {
        var key = NONE
        val time = getTimeInMillis() + timeout

        while (getTimeInMillis() < time) {
            val keyCode = SerialReceiver.getData()
            key = getKey(keyCode)

            if (key != NONE || TicketMachine.hasInterruption()) {
                return key
            }
        }
        return key
    }
}


fun main() {
    HAL.init()
    KBD.init()

    var key: Char
    do {
        key = KBD.waitKey(5000)

        // 4. Verifica se uma tecla foi efetivamente premida
        if (key != KBD.NONE) {
            println("Tecla detetada: $key")
        } else {
            println("Nenhuma tecla premida nos ultimos 5 segundos...")
        }
    } while (key != '#')

    println("Terminado")
}