package org.example

import isel.leic.simul.module.LCD
import isel.leic.utils.Time

// Escreve no LCD usando a interface a 8 bits.
object LCD {

    // Dimensão do display.
    const val LINES = 2
    const val COLS = 16

    // Escreve um byte de comando/dados no LCD em série
    private fun writeByteSerial(rs: Boolean, data: Int) {
        val rsBit = if (rs) 1 else 0

        val extendedData = Integer.toBinaryString(data).padStart(8, '0')
        var dataFullEnabled = "1${extendedData}${rsBit}".toInt(2)
        SerialEmitter.send(addr = SerialEmitter.Peripheral.LCD, dataFullEnabled)

        dataFullEnabled = "0${extendedData}${rsBit}".toInt(2)

        SerialEmitter.send(addr = SerialEmitter.Peripheral.LCD, dataFullEnabled)
    }

    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int) {

    }

    // Escreve um comando no LCD
    private fun writeCMD(data: Int) {
        writeByteSerial(rs = false, data)
    }

    // Escreve um dado no LCD
    private fun writeDATA(data: Int) {
        writeByteSerial(rs = true, data)
    }

    // Envia a sequência de iniciação para comunicação a 8 bits.
    fun init() {
        SerialEmitter.init()
        println("INICIALIZACAO DO LCD\n\n")
        Time.sleep(15)
        writeCMD(0b00110000)
        Time.sleep(5)
        writeCMD(0b00110000)
        Time.sleep(1)
        writeCMD(0b00110000)
        Time.sleep(10)
        println("FIM DA INICIALIZACAO DO LCD\n\n")
        println("CONFIGS DO LCD\n\n")
        writeCMD(0b00111000) //FUNCTION SET
        writeCMD(0b00001111) //DISPLAY OFF
        writeCMD(0b00000110) //Define o ENTRY MODE para incrementar automaticamente.
        Time.sleep(100)
        clear()
        println("FIM DAS CONFIGS DO LCD\n\n")

        write("Welcome!")
    }

    // Escreve um caracter na posição corrente.
    fun write(c: Char) {
        writeDATA(c.code)
    }

    // Escreve uma string na posição corrente.
    fun write(text: String) {
        text.map { write(c = it) }
    }

    // Envia comando para posicionar cursor ('line': 0..LINES-1, 'column': 0..COLS-1)
    fun cursor(line: Int, column: Int) {
        if (line in 0..<LINES && column in 0..<COLS) {
            val lineBits = Integer.toBinaryString(line).padStart(2, '0')
            val columnBits = Integer.toBinaryString(column).padStart(4, '0')

            val cursorCommand = "1$lineBits$columnBits".toInt(2) //USES DDRAM

            writeCMD(data = cursorCommand)
        }
    }

    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear() {
        writeCMD(data = 0b00000001)  // Clears Display
        Time.sleep(2)
        writeCMD(data = 0b00000010)  // Return Home
        Time.sleep(2)
    }
}