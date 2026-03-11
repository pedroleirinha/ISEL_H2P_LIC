package org.example

import isel.leic.UsbPort

object HAL {
    var enviroment = ENVIROMENT.TEST
    var usbValue = 0

    // Inicia o objeto
    fun init(enviroment: ENVIROMENT) {
        this.enviroment = enviroment

        readValue()
    }

    fun readValue() {
        this.usbValue = if (enviroment == ENVIROMENT.REAL) UsbPort.read() else FakeUsbPort.read()
    }

    // Retorna 'true' se o bit definido pela mask está com o valor lógico '1' no UsbPort
    fun isBit(mask: Int): Boolean {
        readValue()
        //O operador "and" faz uma comparação AND bit a bit entre os dois valores
        return (usbValue and mask) == mask
    }

    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int): Int {
        readValue()
        return usbValue and mask
    }

    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask: Int, value: Int) {
        readValue()

        val allBits = mask and value
        val newValue = allBits or usbValue

        if (enviroment == ENVIROMENT.REAL) UsbPort.write(newValue) else FakeUsbPort.write(newValue)
    }

    // Coloca os bits representados por mask no valor lógico '1'
    fun setBits(mask: Int) {
        readValue()

        val newValue = usbValue or mask
        println(newValue)

        if (enviroment == ENVIROMENT.REAL) UsbPort.write(newValue) else FakeUsbPort.write(newValue)
    }

    // Coloca os bits representados por mask no valor lógico '0'
    fun clrBits(mask: Int) {
        readValue()

        val newValue = usbValue and mask.inv()
        println(newValue)

        if (enviroment == ENVIROMENT.REAL) UsbPort.write(newValue) else FakeUsbPort.write(newValue)
    }
}