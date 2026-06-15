package org.example

import isel.leic.utils.Time
import org.example.HAL.clrBits
import org.example.HAL.isBit
import org.example.HAL.readBits
import org.example.HAL.setBits
import org.example.TicketMachine.getTotalTicketPrice

data class Coin(val faceValue: Int = 0, val count: Int = 0)

object CoinAcceptor {
    var coins = Array(0) { Coin() }
    var coinRead = false
    const val coinBit = 0b00001000
    const val acceptCoinBit = 0b00010000
    const val collectCoinBit = 0b01000000
    const val ejectCoinBit = 0b01000000
    const val coinsBits = 0b00000111

    var currentPaymentCoins = mutableMapOf<Int, Int>()
    var coinCounter = 0

    fun init() {
        loadCoins()
    }

    /* COIN ACCEPTOR */

    fun isCoinBitOn(): Boolean {
        return isBit(coinBit)
    }

    fun setAcceptCoinBit() {
        setBits(acceptCoinBit)
    }

    fun clearAcceptCoinBit() {
        clrBits(acceptCoinBit)
    }

    fun setCollectCoinBit() {
        setBits(collectCoinBit)
    }

    fun clearCollectCoinBit() {
        clrBits(collectCoinBit)
    }

    fun setEjectCoinBit() {
        setBits(ejectCoinBit)
    }

    fun clearEjectCoinBit() {
        clrBits(ejectCoinBit)
    }

    fun getCoinsBits(): Int {
        return readBits(coinsBits)
    }

    fun decrementCoinsCount() {
        coinCounter = if (coinCounter > 0) coinCounter - 1 else coins.size - 1
    }

    fun incrementCoinsCount() {
        coinCounter = ++coinCounter % coins.size
    }

    fun getCurrentCoin() = coins[coinCounter]

    fun totalAddedCoinsValue(): Int {
        var sum = 0
        currentPaymentCoins.forEach { (coinValue, count) ->
            sum += coinValue * count
        }
        return sum
    }

    fun resetCoinCounters() {
        for (i in coins.indices) {
            coins[i] = coins[i].copy(count = 0)
        }
    }

    fun checkForNewCoin(): Boolean {
        return checkForCoin() && !coinRead
    }

    fun checkForCoin(): Boolean {
        return isCoinBitOn()
    }

    fun acceptCoin() {
        setAcceptCoinBit()
        clearAcceptCoinBit()
    }

    fun collectCoin() {
        setCollectCoinBit()
        clearCollectCoinBit()
    }

    fun ejectCoins() {
        setEjectCoinBit()
        clearEjectCoinBit()
    }

    fun readCoinBits(): Int {
        val coinBits = getCoinsBits()

        if (coinBits in 0 until coins.size) {
            return coinBits
        }
        return -1
    }

    fun isPaymentCompleted(ticketPrice: Int): Boolean {
        return totalAddedCoinsValue() >= ticketPrice
    }

    fun isPaymentProcessedCompleted(ticketPrice: Int = getTotalTicketPrice()): Boolean {
        return isPaymentCompleted(ticketPrice) && !checkForCoin()
    }

    fun ejectCoinsAndCleanDeposit() {
        ejectCoins()
        currentPaymentCoins = mutableMapOf()
    }

    fun activateCollectCoins() {
        collectCoin()
        currentPaymentCoins = mutableMapOf()
    }

    fun readAndAcceptCoin() {
        readCoin()
        coinRead = true
        acceptCoin()
    }

    fun coinHandshake() {
        collectCoin()
        coinRead = false
    }

    fun transferTicketCoinsToSafe() {
        for (i in coins.indices) {
            coins[i] = coins[i].copy(count = (currentPaymentCoins[coins[i].faceValue] ?: 0) + coins[i].count)
        }
        currentPaymentCoins = mutableMapOf()
    }

    fun isCoinCollectionDone(): Boolean {
        return !checkForCoin() && coinRead
    }

    fun readCoin() {
        val coinBits = readCoinBits()

        if (coinBits == -1) return
        val coin = coins[coinBits]

        val currentCount = currentPaymentCoins.getOrDefault(coin.faceValue, 0)
        currentPaymentCoins[coin.faceValue] = currentCount + 1
    }

    fun isBusy(): Boolean {
        return checkForNewCoin() || isCoinCollectionDone()
    }

    fun loadCoins() {
        coins = DatabaseAccess().loadCoins()
    }

    fun saveCoins() {
        DatabaseAccess().saveCoins(coins)
    }
}

fun main() {
    HAL.init()
    CoinAcceptor.init()

    println(" <- CoinAcceptor -> ")
    println("Teste do Moedeiro iniciado. Insira moedas para testar.")
    println("O sistema utiliza moedas de: 5, 10, 20, 50, 100 e 200")

    while (true) {
        if (CoinAcceptor.checkForCoin()) {
            println("Moeda detetada no moedeiro!")

            CoinAcceptor.readAndAcceptCoin()

            while (!CoinAcceptor.isCoinCollectionDone()) {
                Time.sleep(100) // Aguarda por moeda recolhida
            }

            val total = CoinAcceptor.totalAddedCoinsValue()
            println("Moeda registada com sucesso!")
            println("Valor total acumulado: ${total / 100.0} Euro(s)")

            // Simulação de transição para o cofre seguro se atingir um valor (ex: 2 Euros)
            if (total >= 200) {
                println("Limite atingido. Recolhendo moedas para o cofre...")
                // Ativa o sinal 'collect' e limpa a lista de moedas atuais
                CoinAcceptor.transferTicketCoinsToSafe()
                CoinAcceptor.activateCollectCoins()
                println("Moedas recolhidas. Saldo resetado.")


                CoinAcceptor.saveCoins()
            }
        }

        Time.sleep(100)
    }
}