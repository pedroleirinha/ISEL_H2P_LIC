package org.example

import isel.leic.utils.Time
import org.example.HAL.lcdCommandBits
import org.example.HAL.lcdDisplaySetBits
import org.example.HAL.lcdEntryModeSetBits
import org.example.HAL.lcdFunctionSetBits

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
        Time.sleep(15)
        writeCMD(lcdCommandBits)
        Time.sleep(5)
        writeCMD(lcdCommandBits)
        Time.sleep(1)
        writeCMD(lcdCommandBits)
        Time.sleep(10)
        writeCMD(lcdFunctionSetBits) //FUNCTION SET
        writeCMD(lcdDisplaySetBits) //DISPLAY OFF
        writeCMD(lcdEntryModeSetBits) //Define o ENTRY MODE para incrementar automaticamente.
        Time.sleep(100)
        clear()
        drawCustomIcons()
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
            val lineBits = Integer.toBinaryString(line)

            val columnBits = Integer.toBinaryString(column % COLS)
                .padStart(4, '0')
            val cursorCommand = "1${lineBits}00${columnBits}".toInt(2) //USES DDRAM

            writeCMD(data = cursorCommand)
        }
    }

    fun drawArrowUp() {
        val data = arrayOf(4, 14, 21, 4, 4, 4, 4, 0)
        writeCMD(0x40)
        data.forEach {
            writeDATA(it)
        }
    }

    fun drawArrowDown() {
        val data = arrayOf(0, 4, 4, 4, 4, 21, 14, 4)
        writeCMD(0x48)
        data.forEach {
            writeDATA(it)
        }
    }

    fun drawSmile() {
        val data = arrayOf(0, 10, 10, 0, 17, 14, 0, 0)
        writeCMD(0x50)
        data.forEach {
            writeDATA(it)
        }
    }

    fun drawEuro() {
        val data = arrayOf(6, 9, 30, 8, 30, 9, 6, 0)
        writeCMD(0x58)
        data.forEach {
            writeDATA(it)
        }
    }

    fun drawCustomIcons() {
        drawArrowUp()
        drawArrowDown()
        drawSmile()
        drawEuro()
    }

    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear() {
        writeCMD(data = HAL.lcdClearSetBits)  // Clears Display
        Time.sleep(1)
        writeCMD(data = HAL.lcdHomeSetBits)  // Return Home
    }
}

fun main() {
    HAL.init()
    LCD.init()

    println("<- LCD ->")

    LCD.cursor(0, 0)
    LCD.write("ISEL - LIC 25/26")

    LCD.cursor(1, 0)
    LCD.write("Ticket Machine")

    Time.sleep(2000)

    LCD.clear()
    LCD.cursor(0, 0)
    LCD.write("Icons: ")
    LCD.write(ICONS.ARROW_UP.code) // Seta Cima
    LCD.write(ICONS.ARROW_DOWN.code) // Seta Baixo
    LCD.write(ICONS.SMILE.code) // Smile

    LCD.cursor(1, 0)
    LCD.write("Preco: 1.50")
    LCD.write(ICONS.EURO.code) // Euro

    Time.sleep(3000)

    // 4. Teste de Limpeza e Persistência
    LCD.clear()
    LCD.cursor(0, 4)
    LCD.write("Fim do Teste")
    LCD.cursor(1, 7)
    LCD.write(ICONS.SMILE.code) // Smile

    println("Concluído")
}
