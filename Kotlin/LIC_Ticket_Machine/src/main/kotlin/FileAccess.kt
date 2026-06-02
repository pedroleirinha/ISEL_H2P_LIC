package org.example

import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

object FileAccess {
    const val stationsFileName = "stations.csv"
    const val coinsFileName = "coins.csv"

    fun readCoinsFromFile(): String {
        return readFromFile(coinsFileName)
    }

    fun readStationsFromFile(): String {
        return readFromFile(stationsFileName)
    }

    fun writeCoinsToFile(info: String) {
        writeToFile(coinsFileName, info)
    }

    fun writeStationsToFile(info: String) {
        writeToFile(stationsFileName, info)
    }

    fun readFromFile(fileName: String): String {
        var lines = ""
        BufferedReader(FileReader(fileName)).forEachLine {
            lines += "$it\n"
        }

        return lines
    }

    fun writeToFile(fileName: String, info: String) {
        val pw = PrintWriter(fileName)

        val allLines = info.split("\n")
        allLines.forEach {
            pw.write("$it\n")
        }
        pw.close()
    }
}

fun main() {
    println("--- Teste de Acesso a Ficheiros (Ticket Machine) ---")

    val sampleCoins = "200;10\n100;20\n50;50\n20;100"
    val sampleStations = "1.25;0;Alverca\n2.10;5;Rossio\n3.40;2;Sintra"

    try {
        // 2. Teste de Escrita
        println("A escrever dados nos ficheiros...")
        FileAccess.writeCoinsToFile(sampleCoins)
        FileAccess.writeStationsToFile(sampleStations)
        println("Escrita concluída com sucesso.")

        println("-------------------------------------------")

        // 3. Teste de Leitura
        println("A ler dados do ficheiro de moedas:")
        val readCoins = FileAccess.readCoinsFromFile()
        println(readCoins)

        println("A ler dados do ficheiro de estações:")
        val readStations = FileAccess.readStationsFromFile()
        println(readStations)

        if (readCoins.trim() == sampleCoins.trim()) {
            println("Sucesso: Os dados de moedas coincidem!")
        } else {
            println("Aviso: Existem diferenças nos dados de moedas.")
        }

    } catch (e: Exception) {
        println("Erro durante o teste: ${e.message}")
    }
}
