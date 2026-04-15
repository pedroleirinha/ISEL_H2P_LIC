package org.example

// Envia tramas para os diferentes módulos Serial Receiver .
object SerialReceiver {
    var busy = false

    // Inicia a classe
    fun init() {
        busy = false
    }

    fun receiveInSerie(bitsToReceive: Int): Int {
        var bits = ""
        /*
       * O BIT (0) VAI SER USADO PARA RECEBER O BIT (TxD)
       *
       * */
        println("A RECEBER $bitsToReceive BITS")
        for (i in 0..bitsToReceive) {
            bits += "${if (HAL.isBit(0b00000001)) '1' else '0'}"

            HAL.setBits(mask = 0b00000010)
            HAL.clrBits(mask = 0b00000010)
        }

        return bits.toInt(2)
    }


    // Retorna informação se o periférico está ocupado
    fun isBusy(): Boolean {
        return busy
    }
}