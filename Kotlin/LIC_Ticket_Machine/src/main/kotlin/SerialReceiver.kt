package org.example

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialReceiver {

    // Inicia a classe
    fun init() {
    }

    fun realignTransmission() {
        var count = 0
        while (count < 7) {
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
        HAL.setBits(mask = 0b10000000)
    }

    fun emitTxClkDown() {
        HAL.clrBits(mask = 0b10000000)
    }

    fun emitTxClkCycle() {
        emitTxClkUp()
        //Time.sleep(100)
        emitTxClkDown()
        //Time.sleep(100)
    }

    fun retrieveTxD(): Boolean {
        val bit = HAL.isBit(0b10000000)
        print("${if (bit) 1 else 0}")
        return bit
    }

    fun checkLastTransmissionBit(): Boolean {
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


    // Retorna informação se o periférico está ocupado
// É suposto indicar se a emissão foi concluida verificando o bit final no inputport
    fun isBusy(): Boolean {
        return !HAL.isBit(0b10000000)
    }
}