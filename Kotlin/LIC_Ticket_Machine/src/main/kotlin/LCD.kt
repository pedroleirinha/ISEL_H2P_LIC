package org.example

import isel.leic.simul.module.LCD
import isel.leic.simul.panel.CharLCD

// Escreve no LCD usando a interface a 8 bits.
object LCD {

    // Dimensão do display.
    const val LINES = 2
    const val COLS = 16

    var ecra: LCD = LCD("LCD");

    // Escreve um byte de comando/dados no LCD em série
    private fun writeByteSerial(rs: Boolean, data: Int) {

    }

    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int) {}

    // Escreve um comando no LCD
    private fun writeCMD(data: Int) {}

    // Escreve um dado no LCD
    private fun writeDATA(data: Int) {}

    // Envia a sequência de iniciação para comunicação a 8 bits.
    fun init() {

    }

    // Escreve um caracter na posição corrente.
    fun write(c: Char) {}

    // Escreve uma string na posição corrente.
    fun write(text: String) {}

    // Envia comando para posicionar cursor ('line': 0..LINES-1, 'column': 0..COLS-1)
    fun cursor(line: Int, column: Int) {

    }

    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear() {
        ecra.process8Bits()
    }
}