package org.example

import isel.leic.utils.Time
import org.example.KBD.keyBitsSize

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialReceiver {
    const val numberBitsForKeyTransmission = 7

    // Inicia a classe
    fun init() {
    }

    fun realignTransmission() {
        var count = 0
        while (count < numberBitsForKeyTransmission) {
            emitTxClkUp()
            val txD = retrieveTxD()
            emitTxClkDown()

            if (txD) {
                count++
            }
        }
    }

    fun getData(): Int {
        if (isBusy()) {
            val keyBits = receiveKeyInSerie(keyBitsSize)
            return keyBits

        }
        return -1
    }

    fun receiveKeyInSerie(bitsToReceive: Int): Int {

        emitTxClkCycle()

        // Se o txD não estvier a '1' está desalinhado
        if (retrieveTxD()) {
            //Receive and concatenate all bits
            val bits = receiveInSerie(bitsToReceive)

            emitTxClkCycle()
            if (checkLastTransmissionBit()) {
                emitTxClkCycle() // Ultimo ciclo para repor o '1' no TxD
                return bits.numToBinString().toInt(2)
            }
        }

        // Tenta realinhar a trama garantido que encontra o txD a '1' (repouso) 7 vezes consecutivas
        realignTransmission()

        return -1
    }

    fun emitTxClkUp() {
        HAL.setTxCLK()
    }

    fun emitTxClkDown() {
        HAL.clearTxCLK()
    }

    fun emitTxClkCycle() {
        emitTxClkUp()
        emitTxClkDown()
    }

    fun retrieveTxD(): Boolean {
        val bit = HAL.getTxDBit()
        return bit == 1
    }

    fun checkLastTransmissionBit(): Boolean {
        return !retrieveTxD()
    }

    fun receiveInSerie(bitsToReceive: Int): Int {
        var bits = ""
        //println("A RECEBER $bitsToReceive BITS")

        for (i in 0 until bitsToReceive) {
            emitTxClkUp()
            val txD = HAL.getTxDBit()
            bits += txD

            emitTxClkDown()
        }

        return bits.reversed().toInt(2)
    }


    // Retorna informação se o periférico está ocupado
    // É suposto indicar se a emissão foi concluida verificando o bit final no inputport
    fun isBusy(): Boolean {
        return !HAL.isTxDBitOn()
    }
}

fun main() {
    HAL.init()
    SerialReceiver.init()

    println(" <- SerialReceiver -> ")
    println("Prima teclas no keypad para ver a transmissão série.")

    while (true) {
        SerialReceiver.emitTxClkDown()

        // Detetar o Start Bit. Espera que TXD baixe para '0' enquanto TXclk = 0
        if (!SerialReceiver.retrieveTxD()) {
            println("\nInício de transmissao detetado!")

            // Depois de detetar a tecla, lêmos os 4 bits de dados referentes ao código da tecla
            val keyCode = SerialReceiver.receiveKeyInSerie(4)

            if (keyCode != -1) {
                println("Tecla recebida com sucesso! Codigo: ${keyCode.numToBinStringPadded(keyBitsSize)}")
            } else {
                println("Desalinhamento detetado. A realinhar...")
            }

            Time.sleep(200)
        }
    }
}
