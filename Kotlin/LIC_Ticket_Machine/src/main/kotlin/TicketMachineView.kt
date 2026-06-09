package org.example

import isel.leic.utils.Time
import org.example.CoinAcceptor.totalAddedCoinsValue
import org.example.TUI.clearScreen
import org.example.TUI.showMessageCenterAlign
import org.example.TUI.showMessageCenterAlignPartial
import org.example.TUI.showMessageLeftAlign
import org.example.TUI.showMessageLeftAlignPartial
import org.example.TUI.showMessageRightAlign
import org.example.TicketMachine.getTotalTicketPrice
import kotlin.math.max


object TicketMachineView {


    fun updateTicketPrice(priceText: String) {
        LCD.cursor(1, LCD.COLS - priceText.length)
        //updateDisplay(priceText, 1, LCD.COLS - priceText.length)
        showMessageRightAlign(priceText, 1)
    }

    fun printStation() {
        val station = Stations.getCurrentStation()
        clearScreen()
        updateStationName(station.name)
        showTicketStationNumber(station)
        showTicketPrice(getTotalTicketPrice().toDouble())
    }

    fun showTicketStationNumber(station: Station) {
        val stationNumber = (station.code).toString().padStart(2, '0')
        updateStationNumber("$stationNumber${ICONS.ARROW_UP.code}${ICONS.ARROW_DOWN.code}")
    }

    fun showTicketRoundTripInformation(roundTrip: Boolean) {
        val tripIcon = "${ICONS.ARROW_UP.code}${if (roundTrip) ICONS.ARROW_DOWN.code else ""}"
        updateStationNumber(tripIcon)
    }

    fun updateTicketCount(ticketCountText: String) {
        LCD.cursor(1, LCD.COLS - ticketCountText.length)
        //updateDisplay(ticketCountText, 1, LCD.COLS - ticketCountText.length)
        showMessageRightAlign(ticketCountText, 1)
    }

    fun updateCoinsCount(coinCountText: String) {
        LCD.cursor(1, LCD.COLS - coinCountText.length)
        showMessageRightAlign(coinCountText, 1)
        //updateDisplay(coinCountText, 1, LCD.COLS - coinCountText.length)
    }

    fun printStationTicketsSold() {
        val station = Stations.getCurrentStation()
        clearScreen()

        updateStationName(station.name)
        showTicketStationNumber(station)
        updateTicketCount("${station.ticketsSold}")
    }

    fun printCoinsCount() {
        val coin = CoinAcceptor.getCurrentCoin()

        clearScreen()

        val newPrice: Double = (coin.faceValue.toDouble() / 100)
        val priceText = (newPrice).toString().padEnd(4, '0')

        showMessageCenterAlign("$priceText${ICONS.EURO.code}")
        showCoinCountNumber()
        updateCoinsCount("${coin.count}")

    }

    fun showCoinCountNumber() {
        val stationNumber = CoinAcceptor.coinCounter.toString().padStart(2, '0')
        showMessageLeftAlignPartial("$stationNumber${ICONS.ARROW_UP.code}${ICONS.ARROW_DOWN.code}", 1)
    }

    fun finishCollectTicketMessage(){
        showMessageCenterAlign("Thank You!", 0)
        showMessageCenterAlign("Have a nice Trip", 1)
    }

    fun showPrintingMessage() {
        showMessageLeftAlign(message = "A Imprimir.. ${ICONS.HOUR_GLASS.code}".padEnd(LCD.COLS, ' '), 1)
    }

    fun showShuttingDownMessage() {
        showMessageCenterAlign(message = "A DESLIGAR..", line = 0)
    }

    fun showAbortVendingMessage() {
        clearScreen()
        showMessageCenterAlign("Vending Aborted!")
        showMessageCenterAlign(" ", line = 1)
        Time.sleep(500)
    }

    fun printMaintenanceOption(option: Maintenance.MAINTENANCEOPTIONS) {
        clearScreen()
        showMessageCenterAlign("Maintenance")
        showMessageLeftAlign("${option.key}-${option.title}", line = 1)
    }

    fun printMaintenanceCollectPrint(){
        updateStationName(Stations.getCurrentStation().name)
        showMessageCenterAlign("Collect Ticket", 1)
    }

    fun askConfirmationShutDown() {
        clearScreen()
        showMessageCenterAlign("Shutdown", 0)
        showMessageCenterAlign("*-YES other-NO", 1)
    }

    fun askConfirmationResetCoins() {
        showMessageCenterAlignPartial("Reset? Press *", 1, clearLine = true)
    }

    fun updateStationNumber(priceText: String) {
        LCD.cursor(1, 0)
        //updateDisplay(priceText.padEnd(4, ' '), 1, 0)
        showMessageLeftAlign(priceText.padEnd(4, ' '), 1)
    }

    fun updateStationName(stationName: String) {
        showMessageCenterAlignPartial(stationName, line = 0, clearLine = true)
    }

    fun askConfirmationToPrintTicket() {
        showMessageCenterAlignPartial("${ICONS.ARROW_UP.code} *- to Print", 1, clearLine = true)
    }

    fun showTicketPrice(price: Double) {
        val newPrice = max((price - totalAddedCoinsValue()) / 100, 0.0)
        val priceText = (newPrice).toString().padEnd(4, '0')

        updateTicketPrice("$priceText${ICONS.EURO.code}")
    }
}
