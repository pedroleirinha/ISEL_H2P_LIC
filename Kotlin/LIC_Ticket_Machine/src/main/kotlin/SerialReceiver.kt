package org.example

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

    fun receiveKeyInSerie(bitsToReceive: Int): Int {

        emitTxClkCycle()

        // Se o txD não estvier a '1' está desalinhado
        if (retrieveTxD()) {
            //Receive and concatenate all bits
            val bits = receiveInSerie(bitsToReceive)

            emitTxClkCycle()
            if (checkLastTransmissionBit()) {
                return Integer.toBinaryString(bits).toInt(2)
            }
        }

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
        print(bit)
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