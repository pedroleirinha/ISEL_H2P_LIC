package org.example

import isel.leic.utils.Time
import org.example.KBD.NONE
import org.example.TicketDispenser.activatePrintingTicket
import org.example.TicketDispenser.isTicketCollected
import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import kotlin.math.roundToInt

data class Station(
    val code: Int,
    val name: String,
    val distance: Int,
    val price: Int
)

object TUI {
    var firstKey = true

    var beginSellingProcess = false

    val stations = mutableListOf<Station>()
    var stationCount = 0
    var originStation: Station? = null
    var destStation: Station? = null
    var roundTrip = true

    fun pickStation(key: Char) {
        if (key.isDigit()) {
            stationCount = key.digitToInt()
            if (originStation == null) {
                originStation = stations[key.digitToInt()]
                printStation()
            } else {
                destStation = stations[key.digitToInt()]
                printStation()
            }
        }
    }

    fun toggleRoundTrip() {
        roundTrip = !roundTrip
        printStation()
    }

    fun printStation() {
        val station = stations[stationCount]
        LCD.clear()

        val stationNumber = (station.code - 1).toString().padStart(2, '0')
        val tripIcon = "${0.toChar()}${if (roundTrip) 1.toChar() else ""}"

        var price = station.price.toDouble()

        showMessageCenterAlign(station.name)
        if (beginSellingProcess) {
            showMessageLeftAlign(tripIcon, 1)
            if (roundTrip) {
                price *= 2
            }

        } else {
            showMessageLeftAlign("$stationNumber$tripIcon", 1)
        }
        val priceText = (price / 100).toString().padEnd(4, '0')
        showMessageRightAlign("$priceText${3.toChar()}", 1)
    }

    fun readStations() {
        var stationCounter = 1
        BufferedReader(FileReader("stations.csv"))
            .forEachLine {
                val info = it.split(";")
                stations.add(Station(stationCounter++, info[2], info[1].toInt(), info[0].toInt()))
            }
        println(stations.toString())
    }

    fun askQuestion(message: String) {
        LCD.clear()
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


    fun sellTicket() {
        beginSellingProcess = true
        printStation()
    }

    fun showStation() {
        LCD.clear()
        showMessageLeftAlign(message = "Destino:")
        showMessageRightAlign(message = "A${0.toChar()} e B${1.toChar()}")
        stationCount %= stations.size
        showMessageCenterAlign(message = stations[stationCount].name, 1)
    }

    fun init() {
        HAL.init()
        KBD.init()
        TicketDispenser.init()

        startUpLcd()
        readStations()

        HAL.clrBits(0xF)
    }

    fun nextStation() {
        stationCount = ++stationCount % stations.size
        printStation()
    }

    fun previousStation() {
        stationCount = if (stationCount > 0) stationCount - 1 else stations.size - 1
        printStation()
    }

    fun startUpLcd() {
        LCD.init()
        showWelcomeMessage()
    }

    fun showWelcomeMessageV2() {
        showMessageCenterAlign(message = "Welcome to")
        showMessageCenterAlign(message = "Matosinhos ${2.toChar()}", line = 1)
    }

    fun getDateTime(): String {
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
        showMessageCenterAlign(message = "Ticket To Ride")
        showMessageCenterAlign(message = getDateTime(), line = 1)
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
        } while (key != '#')

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
        Time.sleep(1000)
        isTicketCollected()
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

    fun readKey(): Char {
        //println(Integer.toBinaryString(UsbPort.read()).padStart(8, '0'))
        val key = KBD.waitKey(timeout = 6000)
        if (key != NONE) {
            if (firstKey) {
                LCD.clear()
                firstKey = false
            }
        }

        return key
    }
}


