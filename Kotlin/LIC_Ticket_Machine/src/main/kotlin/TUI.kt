package org.example

import isel.leic.utils.Time
import org.example.CoinAcceptor.totalAddedCoinsValue
import org.example.TicketDispenser.activatePrintingTicket
import org.example.TicketDispenser.isTicketCollected
import java.util.*
import kotlin.math.roundToInt

enum class TicketMachineState {
    PICK_STATION,
    PAYMENT,
    TICKET
}

enum class ICONS(val code: Char) {
    ARROW_UP(0.toChar()),
    ARROW_DOWN(1.toChar()),
    EURO(3.toChar()),
    SMILE(2.toChar()),
}

object TUI {
    var state: TicketMachineState = TicketMachineState.PICK_STATION
    var roundTrip = false

    fun init() {
        HAL.init()
        KBD.init()
        TicketDispenser.init()
        startUpLcd()
        Stations.init()
    }

    fun pickStation(key: Char) {
        if (key.isDigit()) {
            val keyNumber = key.digitToInt()
            Stations.stationCount = keyNumber
            Stations.setDestinationStation(keyNumber)
            println("Destination set to ${Stations.getCurrentStation().name}")
            printStation()
        }
    }

    fun toggleRoundTrip() {
        roundTrip = !roundTrip
        printStation()
    }

    fun printStation() {
        val station = Stations.getCurrentStation()
        LCD.clear()

        var price = station.price.toDouble()

        showMessageCenterAlign(station.name)

        when (state) {
            TicketMachineState.PICK_STATION -> showTicketStationNumber(station)
            TicketMachineState.PAYMENT -> {
                showTicketRoundTripInformation()
                price *= if (roundTrip) 2 else 1
            } else -> 1
        }

        showTicketPrice(price)
    }

    fun showTicketStationNumber(station: Station) {
        val stationNumber = (station.code - 1).toString().padStart(2, '0')
        showMessageLeftAlign("$stationNumber${ICONS.ARROW_UP.code}${ICONS.ARROW_DOWN.code}", 1)
    }

    fun showTicketRoundTripInformation() {
        val tripIcon = "${ICONS.ARROW_UP.code}${if (roundTrip) ICONS.ARROW_DOWN.code else ""}"
        showMessageLeftAlign(tripIcon, 1)
    }

    fun showTicketPrice(price: Double){
        val priceText = ((price - totalAddedCoinsValue()) / 100).toString().padEnd(4, '0')
        showMessageRightAlign("$priceText${ICONS.EURO.code}", 1)
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
        state = TicketMachineState.PAYMENT
        printStation()
    }

    fun showStation() {
        LCD.clear()
        showMessageLeftAlign(message = "Destino:")
        showMessageRightAlign(message = "A${ICONS.ARROW_UP} e B${ICONS.ARROW_DOWN}")
        showMessageCenterAlign(message = Stations.getCurrentStation().name, 1)
    }


    fun nextStation() {
        Stations.incrementStationsCount()
        printStation()
    }

    fun previousStation() {
        Stations.decrementStationsCount()
        printStation()
    }

    fun startUpLcd() {
        LCD.init()
        showWelcomeMessage()
    }

    fun showWelcomeMessageV2() {
        showMessageCenterAlign(message = "Welcome to")
        showMessageCenterAlign(message = "Matosinhos ${ICONS.SMILE}", line = 1)
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
        showMessageCenterAlign(message = "Ticket To Ride")
        showMessageCenterAlign(message = getCurrentDateTimeString(), line = 1)
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

        Stations.setDestinationStation(Stations.stationCount)

        LCD.clear()
        showMessageLeftAlign(message = "Escolheu:")
        showMessageLeftAlign(message = "${Stations.destStation?.name}", 1)
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
            roundTrip = roundTrip,
            origin = Stations.originStation?.code ?: 0,
            destination = Stations.destStation?.code ?: 0
        )
        LCD.clear()
        showMessageLeftAlign(message = "Imprimir Ticket")

    }

    fun readKey(): Char {
        return KBD.waitKey(timeout = 6000)
    }
}


