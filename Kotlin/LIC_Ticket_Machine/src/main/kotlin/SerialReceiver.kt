package org.example

import isel.leic.utils.Time

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialReceiver {
    private const val keyDecodeMask = 0b1100001
    private const val keyDecodeExpectedValue = 0b1000001

    var busy = false

    // Inicia a classe
    fun init() {
        busy = false
    }

    fun receiveKeyInSerie(bitsToReceive: Int): Int {

        //Receive and concatenate all bits
        val bits = receiveInSerie(bitsToReceive)

        //return Integer.toBinaryString(bits).padStart(4, '0').toInt(2)

        //Validate sequence of bits according to the mask
        val isValid = validateSequence(bits, keyDecodeMask, keyDecodeExpectedValue)

        if (isValid) {
            //Retrieve the key
            return Integer.toBinaryString(bits).slice(2..5).toInt(2)
        }
        return -1
    }

    fun receiveInSerie(bitsToReceive: Int): Int {
        if (isBusy()) return 0

        busy = true
        var bits = ""
        println("A RECEBER $bitsToReceive BITS")
        for (i in 0 until bitsToReceive) {
            bits += "${if (HAL.isBit(0b10000000)) '1' else '0'}"

            /*HAL.setBits(mask = 0b00000010)
            Time.sleep(10)
            HAL.clrBits(mask = 0b00000010)*/

            HAL.setBits(mask = 0b10000000)
            Time.sleep(1000)
            HAL.clrBits(mask = 0b10000000)

            println("current bits: $bits")
            /*
            while (!HAL.isBit(0b00100000)) {
            }
            */

            Time.sleep(10)
        }

        busy = false
        return bits.reversed().toInt(2)
    }

    fun validateSequence(bits: Int, mask: Int, valueRef: Int): Boolean {
        //Dos 9 bits Queremos validar os bits (1)-01DDDD0-(1) em que o '1' da ponta é o estado de repouso

        //000 0110 01 - ERRADO

        //101 0110 01 - BATE CERTO

        //101 0000 01 - MASK

        //bits => 111011001

        //bits => 111011001


        return (bits and mask) == valueRef
    }

    // Retorna informação se o periférico está ocupado
    fun isBusy(): Boolean {
        return busy
    }
}