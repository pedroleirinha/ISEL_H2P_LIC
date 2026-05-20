package org.example

import org.example.TUI.printStation

object CoinAcceptor {
    val coins = arrayOf<Int>(5, 10, 20, 50, 100, 200)

    fun totalAddedCoinsValue(): Int = coinsAdded.fold(0) { acc, coin -> acc + coin }
    var coinRead = false
    var safeDeposit = mutableListOf<Int>()
    var coinsAdded = mutableListOf<Int>()

    fun init() {

    }

    fun checkForCoin(): Boolean {
        return HAL.isBit(0b00001000)
    }


    fun acceptCoin() {
        HAL.setBits(0b00010000)
        HAL.clrBits(0b00010000)
    }

    fun collectCoin() {
        HAL.setBits(0b01000000)
        HAL.clrBits(0b01000000)
    }

    fun readCoinBits(): Int {
        val coinBits = HAL.readBits(0b00000111)

        if (coinBits in 0..coins.size) {
            return coins[coinBits]
        }
        return -1
    }

    fun activateCollectCoins() {
        collectCoin()
        coinsAdded = mutableListOf()
    }

    fun readAndAcceptCoin() {
        readCoin()
        coinRead = true
        acceptCoin()
    }

    fun coinHandshake(){
        collectCoin()
        coinRead = false
    }

    fun transferTicketCoinsToSafe() {
        safeDeposit.addAll(coinsAdded)
        coinsAdded = mutableListOf()
    }

    fun isHandshakeDone(): Boolean {
        return !checkForCoin() && coinRead
    }

    fun readCoin() {
        val coinValue = readCoinBits()

        coinsAdded.add(coinValue)
        println(coinValue)
        printStation()
    }

    fun isBusy(): Boolean {
        return checkForCoin() || isHandshakeDone()
    }
}