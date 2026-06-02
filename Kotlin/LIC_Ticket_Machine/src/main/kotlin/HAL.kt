package org.example

import isel.leic.UsbPort

object HAL {
    const val txCLKBit = 0b10000000
    const val txDBit = 0b10000000
    const val coinBit = 0b00001000
    const val acceptCoinBit = 0b00010000
    const val collectCoinBit = 0b01000000
    const val ejectCoinBit = 0b01000000
    const val coinsBits = 0b00000111

    const val maintenanceBit = 0b01000000

    const val sdxBits = 0b00000001
    const val sCLKBits = 0b00000010
    const val ticketCollectedBit = 0b00010000
    const val ticketSerialBits = 0b00001111
    const val tdSSBit = 0b00001000

    const val lcdSSBit = 0b00000100
    const val lcdSerialBits = 0b00000111
    const val lcdCommandBits = 0b00110000
    const val lcdFunctionSetBits = 0b00111000
    const val lcdDisplaySetBits = 0b00001111
    const val lcdEntryModeSetBits = 0b00000110
    const val lcdClearSetBits = 0b00000001
    const val lcdHomeSetBits = 0b00000010


    var lastValue = 0

    // Inicia o objeto
    fun init() {
        lastValue = 0
    }

    // Retorna 'true' se o bit definido pela mask está com o valor lógico '1' no UsbPort
    fun isBit(mask: Int, value: Int? = null): Boolean {
        //O operador "and" faz uma comparação AND bit a bit entre os dois valores
        return ((value ?: UsbPort.read()) and mask) == mask
    }

    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int, value: Int? = null): Int {
        return (value ?: UsbPort.read()) and mask
    }

    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask: Int, value: Int) {
        val allBits = mask and value
        val newValue = allBits or lastValue

        writeHAL(newValue)
    }

    // Coloca os bits representados por mask no valor lógico '1'
    fun setBits(mask: Int) {
        val newValue = lastValue or mask

        writeHAL(newValue)
    }

    // Coloca os bits representados por mask no valor lógico '0'
    fun clrBits(mask: Int) {
        val newValue = lastValue and mask.inv()
        writeHAL(newValue)
    }

    fun writeHAL(newValue: Int) {
        lastValue = newValue
        UsbPort.write(newValue)
    }


    /*
    * AUX FUNCTIONS
    *
    * */


    /* SERIAL RECEIVER */
    fun isTxDBitOn(): Boolean {
        return isBit(txDBit)
    }

    fun getTxDBit(): Int {
        return if (isBit(txDBit)) 1 else 0
    }

    fun setTxCLK() {
        setBits(mask = txCLKBit)
    }

    fun clearTxCLK() {
        clrBits(mask = txCLKBit)
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

    /* TICKET DISPENSER */

    fun isTicketCollectedBitOn(): Boolean {
        return isBit(ticketCollectedBit)
    }

    fun clearTDSerialBits() {
        clrBits(ticketSerialBits) // LIMPA OS 3 BITS QUE VAO SER USADOS
    }


    /* LCD */
    fun clearLCDSerialBits() {
        clrBits(lcdSerialBits)
    }

    fun setSDXBit() {
        setBits(mask = sdxBits) //Fica o ultimo bit ON
    }

    fun clearSDXBit() {
        clrBits(mask = sdxBits) //Fica o ultimo bit OFF
    }

    fun setSCKLBit() {
        setBits(mask = sCLKBits) //Fica o ultimo bit ON
    }

    fun clearSCKLBit() {
        clrBits(mask = sCLKBits) //Fica o ultimo bit OFF
    }

    fun turnOffTdSS() {
        setBits(mask = tdSSBit) //Fica o ultimo bit OFF
    }

    fun turnOffLcdSS() {
        setBits(mask = lcdSSBit) //Fica o ultimo bit OFF
    }

    fun isMaintenanceMode(): Boolean = isBit(maintenanceBit)

}
