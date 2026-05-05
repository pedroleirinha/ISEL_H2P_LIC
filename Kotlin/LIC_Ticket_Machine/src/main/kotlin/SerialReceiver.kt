package org.example

import isel.leic.utils.Time

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialReceiver {
    private const val keyDecodeMask = 0b1100001
    private const val keyDecodeExpectedValue = 0b1000000

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
        HAL.setBits(mask = 0b10000000)
        Time.sleep(10)
        HAL.clrBits(mask = 0b10000000)

        var bits = ""
        println("A RECEBER $bitsToReceive BITS")
        var firstBit = true
        var bitCount = 1

        while (firstBit || bitCount < bitsToReceive) {

            val bit = HAL.isBit(0b10000000)
            if (firstBit) {
                if (bit) {
                    bits += "1"
                    firstBit = false
                }
            } else {
                bits += "${if (bit) '1' else '0'}"
                bitCount++
            }

            /*HAL.setBits(mask = 0b00000010)
            Time.sleep(10)
            HAL.clrBits(mask = 0b00000010)*/

            HAL.setBits(mask = 0b10000000)
            Time.sleep(500)
            HAL.clrBits(mask = 0b10000000)

            println("current bits: $bits")
            /*
            while (!HAL.isBit(0b00100000)) {
            }
            */

            Time.sleep(10)
        }

        busy = false
        return bits.toInt(2)
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