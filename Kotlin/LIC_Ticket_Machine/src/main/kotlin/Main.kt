package org.example

import isel.leic.*

enum class ENVIROMENT {
    TEST,
    REAL
}

object FakeUsbPort {
    var portValue: Int = 0

    fun read(): Int {
        return portValue
    }

    fun write(value: Int) {
        portValue = value
    }
}

fun main() {
    HAL.init(ENVIROMENT.REAL)
    KBD.init()
    LCD.init()

    UsbPort.read()

    while (true) {


        println("1 - read key")
        println("2 - Write Bits")
        println("3 - Set Bits")
        println("4 - Clear Bits")
        val option = readln().toInt()

        when (option) {
            1 -> {
                val key = KBD.getKey()
                println("\n\nTecla Pressionada: $key\n\n")
            }

            2 -> HAL.writeBits(0b10101010, 0b11111111)
            3 -> HAL.setBits(0b00001111)
            4 -> HAL.clrBits(0b00001111)
        }
    }
}