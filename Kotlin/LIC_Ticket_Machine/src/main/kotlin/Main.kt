package org.example

import isel.leic.*

fun main() {
    UsbPort.read()
    KBD.init()
    LCD.init()


    while (true) {
        KBD.waitKey(timeout = 6000)
    }
}