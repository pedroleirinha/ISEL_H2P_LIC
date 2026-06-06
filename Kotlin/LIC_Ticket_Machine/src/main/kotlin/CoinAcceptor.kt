package org.example

import isel.leic.utils.Time

data class Coin(val faceValue: Int = 0, val count: Int = 0)

object CoinAcceptor {
    val coins = Array(6) { Coin() }
    var coinRead = false

    var currentPaymentCoins = mutableMapOf<Int, Int>()
    var coinCounter = 0

    fun init() {
        loadCoins()
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
        return HAL.isCoinBitOn()
    }

    fun acceptCoin() {
        HAL.setAcceptCoinBit()
        HAL.clearAcceptCoinBit()
    }

    fun collectCoin() {
        HAL.setCollectCoinBit()
        HAL.clearCollectCoinBit()
    }

    fun ejectCoins() {
        HAL.setEjectCoinBit()
        HAL.clearEjectCoinBit()
    }

    fun readCoinBits(): Int {
        val coinBits = HAL.getCoinsBits()

        if (coinBits in 0 until coins.size) {
            return coinBits
        }
        return -1
    }

    fun isPaymentCompleted(ticketPrice: Int): Boolean {
        return totalAddedCoinsValue() >= ticketPrice
    }

    fun isPaymentProcessedCompleted(ticketPrice: Int): Boolean {
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
        val list = FileAccess.readCoinsFromFile()

        val allCoins = list.split("\n")

        var index = 0
        for (coinInfo in allCoins) {
            if (coinInfo.isEmpty()) break
            val info = coinInfo.split(";")
            coins[index++] = Coin(info[0].toInt(), info[1].toInt())

        }
    }

    fun saveCoins() {
        var text = ""
        coins.forEach {
            text += "${it.faceValue};${it.count}\n"
        }

        FileAccess.writeCoinsToFile(text)
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