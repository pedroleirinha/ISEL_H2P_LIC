package org.example

import isel.leic.UsbPort

object HAL {
    var lastValue = 0

    // Inicia o objeto
    fun init() {
        lastValue = 0
        clrBits(0b11111111)
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
}