package org.example

import isel.leic.utils.Time

// Escreve no LCD usando a interface a 8 bits.
object LCD {

    const val sdxBits = 0b00000001
    const val sCLKBits = 0b00000010
    const val lcdSSBit = 0b00000100
    const val lcdSerialBits = 0b00000111
    const val lcdCommandBits = 0b00110000
    const val lcdFunctionSetBits = 0b00111000
    const val lcdDisplaySetBits = 0b00001100
    const val lcdEntryModeSetBits = 0b00000110
    const val lcdClearSetBits = 0b00000001
    const val lcdHomeSetBits = 0b00000010

    // Dimensão do display.
    const val LINES = 2
    const val COLS = 16

    // Escreve um byte de comando/dados no LCD em série
    private fun writeByteSerial(rs: Boolean, data: Int) {
        val enableMask = 0b1000000000
        val RSMask = 0b0000000001

        val shiftedData = data shl 1
        val dataWithRSMask = if (rs) shiftedData or RSMask else shiftedData
        val dataWithEnableMask = dataWithRSMask or enableMask

        SerialEmitter.send(addr = SerialEmitter.Peripheral.LCD, dataWithEnableMask)
        SerialEmitter.send(addr = SerialEmitter.Peripheral.LCD, dataWithRSMask)
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
            val lastBit = 0b10000000

            val columnBits = column % COLS
            val cursorCommand = lastBit or (line shl 6) or columnBits

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

    fun drawHourGlass() {
        val data = intArrayOf(0x1F, 0x11, 0x0A, 0x04, 0x0A, 0x11, 0x1F, 0x00)
        writeCMD(0x60)
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
        drawHourGlass()
    }

    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear() {
        writeCMD(data = lcdClearSetBits)  // Clears Display
        writeCMD(data = lcdHomeSetBits)  // Return Home
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
