package org.example

data class Station(
    val code: Int,
    val name: String,
    val ticketsSold: Int,
    val price: Int
)

fun Station.toText() = "${price};${ticketsSold};${name}"

object Stations {

    val stationsList = mutableListOf<Station>()
    var stationCount = 0
    var originStation: Station? = null
    var destStation: Station? = null

    fun init() {
        loadStations()

    }

    fun getCurrentStation(): Station {
        return stationsList[stationCount]
    }

    fun setDestinationStation(index: Int) {
        destStation = stationsList[index]
    }

    fun setOriginStation(index: Int) {
        originStation = stationsList[index]
    }

    fun decrementStationsCount() {
        stationCount = if (stationCount > 0) stationCount - 1 else stationsList.size - 1
        if (getCurrentStation().code == originStation?.code) {
            stationCount = if (stationCount > 0) stationCount - 1 else stationsList.size - 1
        }
    }

    fun incrementStationsCount() {
        stationCount = ++stationCount % stationsList.size
        if (getCurrentStation().code == originStation?.code) {
            stationCount = ++stationCount % stationsList.size
        }
    }

    fun incrementDestinationStationSoldTickets() {
        val station = stationsList[stationCount]
        stationsList[stationCount] = station.copy(ticketsSold = station.ticketsSold + 1)

        stationCount = 0
        destStation = null
    }

    fun resetStationsTicketCounters() {
        for (i in stationsList.indices) {
            stationsList[i] = stationsList[i].copy(ticketsSold = 0)
        }
    }

    fun loadStations() {
        var stationCounter = 0
        val list = FileAccess.readStationsFromFile()

        val allStations = list.split("\n")
        for (line in allStations) {
            if (line.isEmpty()) break
            val info = line.split(";")
            val price = info[0].toInt()

            stationsList.add(
                Station(stationCounter++, info[2], info[1].toInt(), price)
            )
        }

        originStation = stationsList.find { it.price == 0 }
    }

    fun saveStations() {
        var text = ""
        stationsList.forEach {
            text += "${it.toText()}\n"
        }

        FileAccess.writeStationsToFile(text)
    }
}


fun main() {

    println("--- Teste do Módulo Stations (Ticket Machine) ---")

    // 1. Preparar ficheiro fictício para o teste
    //val csvContent = "120;5;Alverca\n0;0;Lisboa-Santa Apolonia\n250;2;Sintra\n180;10;Cascais"
    //FileAccess.writeStationsToFile(csvContent)
    println("Ficheiro 'stations.csv' criado para teste.\n")

    // 2. Inicializar o objeto Stations
    // O init() carrega as estações e define a origem
    Stations.init()
    println("Sistema inicializado.")
    println("Estação de Origem: ${Stations.originStation?.name}")
    println("Total de estações carregadas: ${Stations.stationsList.size}")

    println("\n--- Navegação ---")
    // Simula premir a tecla 'A'
    Stations.incrementStationsCount()
    println("Navegou para: ${Stations.getCurrentStation().name}")

    Stations.incrementStationsCount()
    println("Navegou para: ${Stations.getCurrentStation().name}")

    // Simula premir a tecla 'B'
    Stations.decrementStationsCount()
    println("Navegou para: ${Stations.getCurrentStation().name}")

    println("\n--- Teste de Seleção e Venda (Tecla #) ---")
    // Define o destino para a estação atual
    val currentIndex = Stations.stationCount
    Stations.setDestinationStation(currentIndex)
    println("Destino selecionado: ${Stations.destStation?.name}")

    // Simula a conclusão da venda (incrementa bilhetes vendidos e faz reset)
    val soldBefore = Stations.stationsList[currentIndex].ticketsSold
    Stations.incrementDestinationStationSoldTickets()
    println("Venda realizada para index $currentIndex.")

    // Verifica se o contador aumentou e se o sistema resetou para a estação inicial (index 0)
    println("Bilhetes vendidos anteriormente: $soldBefore")
    println("Bilhetes vendidos agora: ${Stations.stationsList[currentIndex].ticketsSold}")
    println("Estação após venda (reset): ${Stations.getCurrentStation().name}")

    println("\n--- Teste de Persistência ---")
    Stations.saveStations()
    println("Dados guardados no ficheiro.")

    // 4. Verificação final da string formatada
    val finalData = FileAccess.readStationsFromFile()
    println("Conteúdo final do ficheiro:\n$finalData")
}