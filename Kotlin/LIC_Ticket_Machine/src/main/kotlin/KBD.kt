package org.example

import isel.leic.UsbPort
import isel.leic.utils.Time.getTimeInMillis
import org.example.SerialReceiver.receiveKeyInSerie


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

        val keyBits = receiveKeyInSerie(4)
        if (keyBits == -1) return NONE

        val key = Integer.toBinaryString(keyBits).padStart(4, '0')

        val row = key.slice(2..3).toInt(2)
        val col = key.slice(0..1).toInt(2)

        println("Coluna: $col; Linha: $row")
        return teclas[row][col]
    }

    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos),
    // ou NONE caso contrário.
    fun waitKey(timeout: Long): Char {
        val time = getTimeInMillis() + timeout
        while (getTimeInMillis() < time) {
            //println(Integer.toBinaryString(UsbPort.read()).padStart(8, '0'))
            if (SerialReceiver.isBusy()) {
                val key = getKey()
                println("KEY: $key pressed")
                return key
            }
            if (CoinAcceptor.checkForCoin()) return NONE
        }
        println("NO KEY PRESS")
        return NONE
    }
}