package org.example

import isel.leic.utils.Time
import org.example.CoinAcceptor.totalAddedCoinsValue
import org.example.TicketMachine.TicketMachineState
import org.example.TicketMachine.getTotalTicketPrice
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

enum class ICONS(val code: Char) {
    ARROW_UP(0.toChar()),
    ARROW_DOWN(1.toChar()),
    EURO(3.toChar()),
    SMILE(2.toChar()),
}

object TUI {

    fun init() {
        HAL.init()
        KBD.init()
        startUpLcd()
    }

    fun printStation(roundTrip: Boolean = false) {
        val station = Stations.getCurrentStation()
        LCD.clear()

        showMessageCenterAlign(station.name)

        when (TicketMachine.state) {
            TicketMachineState.PICK_STATION -> showTicketStationNumber(station)
            TicketMachineState.PAYMENT -> showTicketRoundTripInformation(roundTrip)
            else -> 1
        }

        showTicketPrice(getTotalTicketPrice().toDouble())
    }

    fun printStationTicketsSold() {
        val station = Stations.getCurrentStation()
        LCD.clear()

        showMessageCenterAlign(station.name)
        showTicketStationNumber(station)
        showMessageRightAlign("${station.ticketsSold}", 1)
    }

    fun printCoinsCount() {
        val coin = CoinAcceptor.getCurrentCoin()
        LCD.clear()

        val newPrice: Double = (coin.faceValue.toDouble() / 100)
        val priceText = (newPrice).toString().padEnd(4, '0')
        showMessageCenterAlign("$priceText${ICONS.EURO.code}")
        showCoinCountNumber()
        showMessageRightAlign("${coin.count}", 1)
    }

    fun showTicketStationNumber(station: Station) {
        val stationNumber = (station.code - 1).toString().padStart(2, '0')
        showMessageLeftAlign("$stationNumber${ICONS.ARROW_UP.code}${ICONS.ARROW_DOWN.code}", 1)
    }


    fun showCoinCountNumber() {
        val stationNumber = CoinAcceptor.coinCounter.toString().padStart(2, '0')
        showMessageLeftAlign("$stationNumber${ICONS.ARROW_UP.code}${ICONS.ARROW_DOWN.code}", 1)
    }

    fun printMaintenanceOption(option: Maintenance.MAINTENANCEOPTIONS) {
        LCD.clear()
        showMessageCenterAlign("Maintenance")
        showMessageLeftAlign("${option.key}-${option.string}", 1)
    }

    fun showPrintingMessage() {
        LCD.clear()
        showMessageLeftAlign(message = "Imprimir Ticket")
    }

    fun showShuttingDownMessage() {
        LCD.clear()
        showMessageCenterAlign(message = "A DESLIGAR..")
    }

    fun showAbortVendingMessage() {
        LCD.clear()
        showMessageCenterAlign("Vending Aborted!")
        Time.sleep(1000)

        showWelcomeMessage()
    }

    fun showTicketRoundTripInformation(roundTrip: Boolean) {
        val tripIcon = "${ICONS.ARROW_UP.code}${if (roundTrip) ICONS.ARROW_DOWN.code else ""}"
        showMessageLeftAlign(tripIcon, 1)
    }

    fun showTicketPrice(price: Double) {
        val newPrice = max((price - totalAddedCoinsValue()) / 100, 0.0)
        val priceText = (newPrice).toString().padEnd(4, '0')
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

    fun showStation() {
        LCD.clear()
        showMessageLeftAlign(message = "Destino:")
        showMessageRightAlign(message = "A${ICONS.ARROW_UP} e B${ICONS.ARROW_DOWN}")
        showMessageCenterAlign(message = Stations.getCurrentStation().name, 1)
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
        LCD.clear()
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

}


fun main() {
    println(" <- TUI -> ")
    TUI.init()

    println("A verificar ecrã de boas-vindas...")
    Time.sleep(3000)

    LCD.clear()
    TUI.showMessageLeftAlign("Esquerda", 0)
    TUI.showMessageCenterAlign("Centro", 1)
    Time.sleep(2000)

    LCD.clear()
    TUI.showMessageRightAlign("Direita", 0)
    TUI.showTicketPrice(150.0)
    Time.sleep(3000)

    println("Responda no teclado: Pagar Bilhete? (* para Sim, # para Não)")
    TUI.askQuestion("Pagar Bilhete")
    val resposta = TUI.yesOrNoAnwser()

    LCD.clear()
    if (resposta) {
        TUI.showMessageCenterAlign("A processar...", 0)
        TUI.showWelcomeMessageV2()
    } else {
        TUI.showMessageCenterAlign("Cancelado", 0)
    }

    Time.sleep(3000)
    TUI.showWelcomeMessage() // Volta ao estado inicial
    println("Teste do TUI concluído.")
}
