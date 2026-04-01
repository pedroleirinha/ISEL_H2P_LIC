package org.example

import isel.leic.UsbPort
import org.example.KBD.NONE

object TUI {
    var firstKey = true

    fun init() {
        HAL.init()
        KBD.init()
        LCD.init()
    }


    fun readKey() {
        println(Integer.toBinaryString(UsbPort.read()).padStart(8, '0'))
        val key = KBD.waitKey(timeout = 6000)
        if (key != NONE) {
            if (firstKey) {
                LCD.clear()
                firstKey = false
            }
            LCD.write(c = key)
        }
    }
}