package org.example

import isel.leic.utils.Time
import java.util.*
import kotlin.math.roundToInt

enum class ICONS(val code: Char) {
    ARROW_UP(0.toChar()),
    ARROW_DOWN(1.toChar()),
    EURO(3.toChar()),
    SMILE(2.toChar()),
    HOUR_GLASS(4.toChar()),
}

object TUI {
    var bufferLine1 = StringBuilder(" ".repeat(LCD.COLS))
    var bufferLine2 = StringBuilder(" ".repeat(LCD.COLS))

    fun init() {
        KBD.init()
        startUpLcd()
    }

    fun checkIfSameTextOnDisplay(message: String, line: Int, pos: Int): Boolean {
        val targetBuffer = if (line == 0) bufferLine1 else bufferLine2
        for (i in message.indices) {
            val bufferIndex = pos + i
            if (bufferIndex < LCD.COLS && targetBuffer[bufferIndex] != message[i]) {
                return false
            }
        }
        return true
    }

    fun updateDisplay(message: String, line: Int, pos: Int) {
        if (!checkIfSameTextOnDisplay(message, line, pos)) {
            updateDisplayPartially(line, column = pos, text = message)
        }
    }

    fun updateDisplayPartially(line: Int, column: Int, text: String) {
        val targetBuffer = if (line == 0) bufferLine1 else bufferLine2

        for (i in text.indices) {
            val bufferIndex = column + i
            if (bufferIndex < 16) {
                targetBuffer.setCharAt(bufferIndex, text[i])
            }
        }

        LCD.cursor(line, column)
        LCD.write(text)
    }

    fun askQuestion(message: String) {
        showMessageLeftAlign(message = "${message}?")
    }

    fun yesOrNoAnwser(): Boolean {
        // "*" for YES and "#" for NO
        var key: Char?
        do {
            key = KBD.waitKey(timeout = 6000)
        } while (key != '*' && key != '#')

        return key == '*'
    }

    fun startUpLcd() {
        LCD.init()
        showWelcomeMessage()
    }

    fun showWelcomeMessageV2() {
        showMessageCenterAlign(message = "Welcome to")
        showMessageCenterAlign(message = "Matosinhos ${ICONS.SMILE.code}", line = 1)
    }

    fun getCurrentDateTimeString(): String {
        val cal = Calendar.getInstance()

        val day = "${cal.get(Calendar.DAY_OF_MONTH)}".padStart(2, '0')
        val month = "${cal.get(Calendar.MONTH) + 1}".padStart(2, '0')
        val year = "${cal.get(Calendar.YEAR)}".padStart(2, '0')

        val hour = "${cal.get(Calendar.HOUR_OF_DAY)}".padStart(2, '0')
        val minute = "${cal.get(Calendar.MINUTE)}".padStart(2, '0')

        val date = "${day}/${month}/${year}"
        val time = "${hour}:${minute}"

        return "$date $time"
    }

    fun showWelcomeMessage() {
        showMessageCenterAlignPartial(message = "Ticket To Ride", clearLine = true)
        showMessageCenterAlignPartial(message = getCurrentDateTimeString(), line = 1, clearLine = true)
    }

    fun showMessageRightAlignPartial(message: String, line: Int = 0, clearLine: Boolean = false) {
        val startPos = LCD.COLS - message.length

        if (clearLine) {
            val displayMessage = message.padStart(startPos + message.length, ' ')
            LCD.cursor(line, 0)
            updateDisplay(displayMessage, line, 0)
        } else {
            LCD.cursor(line, startPos)
            updateDisplay(message, line, startPos)
        }
    }

    fun showMessageLeftAlignPartial(message: String, line: Int = 0, clearLine: Boolean = false) {
        val displayMessage = if (clearLine) message.padEnd(LCD.COLS, ' ') else message
        LCD.cursor(line, 0)
        updateDisplay(displayMessage, line, 0)
    }

    fun showMessageCenterAlignPartial(message: String, line: Int = 0, clearLine: Boolean = false) {
        val halfMessage = message.length / 2.0
        val startPos = (LCD.COLS / 2) - (halfMessage.roundToInt())

        if (clearLine) {
            val displayMessage = message.padStart(startPos + message.length, ' ').padEnd(LCD.COLS, ' ')
            LCD.cursor(line, 0)
            updateDisplay(displayMessage, line, 0)
        } else {
            LCD.cursor(line, startPos)
            updateDisplay(message, line, startPos)
        }

    }

    fun showMessageRightAlign(message: String, line: Int = 0) {
        val startPos = LCD.COLS - message.length
        LCD.cursor(line, startPos)
        LCD.write(text = message)
    }

    fun showMessageLeftAlign(message: String, line: Int = 0) {
        LCD.cursor(line, 0)
        LCD.write(text = message)
    }

    fun showMessageCenterAlign(message: String, line: Int = 0) {
        val halfMessage = message.length / 2.0
        val startPos = (LCD.COLS / 2) - (halfMessage.roundToInt())
        LCD.cursor(line, startPos)
        LCD.write(text = message)
    }

    fun clearScreen(){
        LCD.clear()
        bufferLine1 = StringBuilder(" ".repeat(LCD.COLS))
        bufferLine2 = StringBuilder(" ".repeat(LCD.COLS))
    }
}


fun main() {
    println(" <- TUI Test (Partial Updates) -> ")
    TUI.init() // Inicializa o hardware e os buffers de software internos

    println("A verificar ecrã de boas-vindas...")
    TUI.showWelcomeMessage() // Desenho inicial completo
    Time.sleep(3000)

    // Os métodos showMessage devem agora usar LCD.cursor(line, 0) internamente.
    println("Demo: Alinhamentos (Mantendo o ecrã ativo)")
    TUI.showMessageLeftAlign("Esquerda", 0)   // Atualiza apenas a Linha 0
    TUI.showMessageCenterAlign("Centro", 1)    // Atualiza apenas a Linha 1
    Time.sleep(2000)

    // a linha 1 ("Centro") mantém-se visível até ser sobrescrita pelo preço.
    TUI.showMessageRightAlign("Direita", 0)

    Time.sleep(3000)

    println("Responda no teclado: Pagar Bilhete? (* para Sim, # para Não)")
    TUI.askQuestion("Pagar Bilhete") // Escreve a pergunta sem limpar o preço se não necessário
    val resposta = TUI.yesOrNoAnwser()

    // posicionamos o cursor na linha de ação (ex: Linha 1).
    if (resposta) {
        TUI.showMessageCenterAlign("A processar...", 1)
        Time.sleep(1000)
        TUI.showWelcomeMessageV2() // Atualiza apenas os campos que mudaram na V2
    } else {
        TUI.showMessageCenterAlign("Cancelado", 1)
    }

    Time.sleep(3000)
    TUI.showWelcomeMessage() // Regressa ao estado IDLE de forma fluida
    println("Teste do TUI concluído.")
}
