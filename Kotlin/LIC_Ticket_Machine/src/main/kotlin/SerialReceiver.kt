package org.example

import isel.leic.utils.Time

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialReceiver {

    // Inicia a classe
    fun init() {
    }

    fun receiveKeyInSerie(bitsToReceive: Int): Int {

        if (!checkFirstTransmissionBit()) return -1

        emitTxClkCycle()

        //Receive and concatenate all bits
        val bits = receiveInSerie(bitsToReceive)

        //return Integer.toBinaryString(bits).padStart(4, '0').toInt(2)

        println("\n" + Integer.toBinaryString(bits).padStart(4, '0').toInt(2) + "\n")
        if (!checkLastTransmissionBit()) return -1

        while (!retrieveTxD()) {
            emitTxClkUp()
            emitTxClkDown()

        }

        return Integer.toBinaryString(bits).toInt(2)
    }

    fun emitTxClkUp() {
        HAL.setBits(mask = 0b10000000)
        Time.sleep(10)
    }

    fun emitTxClkDown() {
        HAL.clrBits(mask = 0b10000000)
        Time.sleep(10)
    }

    fun emitTxClkCycle(){
        emitTxClkUp()
        emitTxClkDown()
    }

    fun retrieveTxD(): Boolean {
        val bit = HAL.isBit(0b10000000)
        print("${if (bit) 1 else 0}")
        return bit
    }

    fun checkFirstTransmissionBit(): Boolean {
        emitTxClkUp()
        return retrieveTxD()
    }

    fun checkLastTransmissionBit(): Boolean {
        emitTxClkUp()
        return !retrieveTxD()
    }

    fun receiveInSerie(bitsToReceive: Int): Int {
        var bits = ""
        //println("A RECEBER $bitsToReceive BITS")

        for (i in 0 until bitsToReceive) {
            emitTxClkUp()
            val txD = retrieveTxD()
            bits += "${if (txD) '1' else '0'}"

            emitTxClkDown()
        }

        return bits.reversed().toInt(2)
    }

    fun validateSequence(bits: Int, mask: Int, valueRef: Int): Boolean {
        return (bits and mask) == valueRef
    }

    // Retorna informação se o periférico está ocupado
    // É suposto indicar se a emissão foi concluida verificando o bit final no inputport
    fun isBusy(): Boolean {
        return !HAL.isBit(0b10000000)
    }
}