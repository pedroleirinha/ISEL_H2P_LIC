package org.example

import java.sql.Connection
import java.sql.DriverManager

class DatabaseAccess : DateAccessInterface {
    var connection: Connection? = null

    init {
        connect()
    }

    fun connect() {
        val url = "jdbc:postgresql://localhost:5432/ticketmachine"
        val user = "postgres"
        val password = "password"

        connection = DriverManager.getConnection(url, user, password)
    }

    override fun loadStations(): List<Station> {
        if (connection == null) return emptyList()

        val statement = connection!!.createStatement()
        val queryResult = statement.executeQuery("SELECT * FROM stations")
        val stationsList = mutableListOf<Station>()

        while (queryResult.next()) {
            val id = queryResult.getInt("id")
            val title = queryResult.getString("title")
            val ticketsSold = queryResult.getInt("tickets_sold")
            val price = queryResult.getDouble("price")

            stationsList.add(
                Station(id - 1, title, ticketsSold, price.toInt())
            )
        }

        return stationsList
    }

    override fun saveStations(stations: List<Station>) {
        if (connection == null) return

        // SQL para atualizar apenas o número de bilhetes vendidos para cada estação
        val sql = "UPDATE stations SET tickets_sold = ? WHERE id = ?"
        val preparedStatement = connection!!.prepareStatement(sql)

        stations.forEach { station ->
            preparedStatement.setInt(1, station.ticketsSold)
            preparedStatement.setInt(2, station.code)
            preparedStatement.executeUpdate()
        }
    }

    override fun saveCoins(coins: Array<Coin>) {
        if (connection == null) return

        // SQL para atualizar a contagem de moedas no inventário
        val sql = "UPDATE coins SET count = ? WHERE id = ?"
        val preparedStatement = connection!!.prepareStatement(sql)

        for (id in coins.indices) {
            val coin = coins[id]
            preparedStatement.setDouble(1, coin.count.toDouble())
            preparedStatement.setInt(2, id)
            preparedStatement.executeUpdate()
        }
    }

    override fun loadCoins(): Array<Coin> {
        if (connection == null) return emptyArray()

        val statement = connection!!.createStatement()
        val queryResult = statement.executeQuery("SELECT * FROM coins")

        val coins = Array(6) { Coin() }

        while (queryResult.next()) {
            val id = queryResult.getInt("id")
            val faceValue = queryResult.getInt("face_value")
            val count = queryResult.getDouble("count")

            coins[id - 1] = Coin(faceValue, count.toInt())
        }

        return coins
    }
}