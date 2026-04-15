package org.example

import isel.leic.UsbPort
import isel.leic.utils.Time
import org.example.KBD.NONE
import org.example.TicketDispenser.activatePrintingTicket
import java.io.BufferedReader
import java.io.FileReader
import kotlin.math.roundToInt

data class Station(
    val code: Int,
    val name: String,
    val price: Int = 0
)

object TUI {
    var firstKey = true
    val stations = mutableListOf<Station>()
    var stationCount = 0
    var originStation: Station? = null
    var destStation: Station? = null
    var roundTrip = false

    fun readStations() {
        BufferedReader(FileReader("stations.csv")).forEachLine {
            val info = it.split(";")
            stations.add(Station(info[0].toInt(), info[1]))
        }
    }

    fun askQuestion(message: String) {
        LCD.clear()
        showMessageLeftAlign(message = "${message}?")
    }

    fun yesOrNoAnwser(): Boolean {
        var key: Char?
        do {
            key = KBD.waitKey(timeout = 6000)
        } while (key != '*' && key != '#')

        return key == '*'
    }

    fun sellTicket() {
        askQuestion("Ida e Volta")
        showMessageLeftAlign("S - * | N - #", 1)
        roundTrip = yesOrNoAnwser()

        pickFromStationsList()
    }

    fun showStation() {
        LCD.clear()
        showMessageLeftAlign(message = "Destino: A${0.toChar()} e B${1.toChar()}")
        stationCount %= stations.size
        showMessageCenterAlign(message = stations[stationCount].name, 1)
    }

    fun init() {
        HAL.init()
        KBD.init()
        TicketDispenser.init()

        startUpLcd()
        readStations()
    }

    fun nextStation() {
        stationCount++
        showStation()
    }

    fun previousStation() {
        stationCount = if (stationCount > 0) stationCount - 1 else stations.size - 1
        showStation()
    }

    fun startUpLcd() {
        LCD.init()
        showWelcomeMessage()
    }

    fun showWelcomeMessage() {
        showMessageCenterAlign(message = "Welcome to")
        showMessageCenterAlign(message = "Matosinhos ${2.toChar()}", line = 1)
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

    fun pickFromStationsList() {
        LCD.clear()
        showMessageLeftAlign(message = "Destino:")
        showStation()
        var key: Char?
        do {
            key = KBD.waitKey(timeout = 6000)

            when (key) {
                'A' -> nextStation()
                'B' -> previousStation()
            }

        } while (key != '*')

        destStation = stations[stationCount]
        LCD.clear()
        showMessageLeftAlign(message = "Escolheu:")
        showMessageLeftAlign(message = "${destStation?.name}", 1)
        Time.sleep(2000)
        LCD.clear()
        HAL.clrBits(0b00010000)
        showMessageLeftAlign(message = "Imprimir Ticket")
        submitTicket()
        LCD.clear()
        showMessageLeftAlign(message = "Retire o bilhete!")
        while (!HAL.isBit(0b00010000)){
            Time.sleep(1000)
        }
        Time.sleep(1000)
        LCD.clear()
        showMessageLeftAlign(message = "Coletado")
        Time.sleep(2000)
        LCD.clear()
    }

    fun submitTicket() {
        activatePrintingTicket(
            roundTrip = true,
            origin = originStation?.code ?: 0,
            destination = destStation?.code ?: 0
        )
    }

    fun readKey() {
        println(Integer.toBinaryString(UsbPort.read()).padStart(8, '0'))
        val key = KBD.waitKey(timeout = 6000)
        if (key != NONE) {
            if (firstKey) {
                LCD.clear()
                firstKey = false
            }

            when (key) {
                '#' -> sellTicket()
                else -> LCD.write(c = key)
            }
        }
    }
}


