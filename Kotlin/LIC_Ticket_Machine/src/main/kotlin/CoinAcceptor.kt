package org.example

import isel.leic.utils.Time

object CoinAcceptor {
    val coins = arrayOf<Int>(5, 10, 20, 50, 100, 200)

    fun totalAddedCoinsValue(): Int = coinsAdded.fold(0) { acc, coin -> acc + coin }

    var coinRead = false
    var safeDeposit = mutableListOf<Int>()
    var coinsAdded = mutableListOf<Int>()

    fun init() {
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

        if (coinBits in 0..coins.size) {
            return coins[coinBits]
        }
        return -1
    }

    fun ejectCoinsAndCleanDeposit() {
        ejectCoins()
        coinsAdded = mutableListOf()
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

    fun coinHandshake() {
        collectCoin()
        coinRead = false
    }

    fun transferTicketCoinsToSafe() {
        safeDeposit.addAll(coinsAdded)
        coinsAdded = mutableListOf()
    }

    fun isCoinCollectionDone(): Boolean {
        return !checkForCoin() && coinRead
    }

    fun readCoin() {
        val coinValue = readCoinBits()
        coinsAdded.add(coinValue)
    }

    fun isBusy(): Boolean {
        return checkForCoin() || isCoinCollectionDone()
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
            }

            println("Valor total no cofre ${CoinAcceptor.safeDeposit.sum() / 100} Euro(s)")
        }

        Time.sleep(100)
    }
}