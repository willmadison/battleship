package com.willmadison.battleship.bo

data class Board(val width: Int) {

    enum class ShotResult(val code: Char) {
        HIT('H'), MISS('M')
    }

    private val occupants: MutableMap<Location, Ship?> = mutableMapOf()

    private val shotsByLocation: MutableMap<Location, ShotResult> = mutableMapOf()

    init {
        val ranges = mutableListOf<LocationRange>()

        for (i in 1..width) {
            val rowName = "${'A'+i-1}"
            val start = "${rowName}1"
            val end = "$rowName$width"

            ranges.add(LocationRange(start, end))
        }

        for (range in ranges) {
            for (location in range.locations) {
                occupants[location] = null
            }
        }
    }

    fun place(ship: Ship, range: LocationRange) {
        if (!range.isValidFor(this, ship)) {
            throw InvalidShipPlacementException()
        }

        for (location in range.locations) {
            if (isOccupied(location)) {
                throw InvalidShipPlacementException("location ${location.row}:${location.column} is already occupied")
            }
        }

        for (location in range.locations) {
            occupants[location] = ship
        }
    }

    fun isOccupied(location: Location): Boolean {
        val occupant = occupants[location]
        return occupant != null
    }

    fun display(): String {
        val rows = mutableListOf<String>()

        for (row in 0..width + 2) {
            val isHeaderRow = row == 0 || row == width + 2

            if (isHeaderRow) {
                rows.add(getHeader())
                continue
            }

            val isColumnHeaderRow = row == 1

            if (isColumnHeaderRow) {
                rows.add(getColumnHeader())
                continue
            }

            val rowSb = StringBuilder()

            for (col in 0..width) {
                val isRowHeaderColumn = col == 0

                val rowName = "${'A' + row - 2}"

                if (isRowHeaderColumn) {
                    rowSb.append(rowName)
                } else {
                    val location = "$rowName$col".toLocation()

                    val result = shotsByLocation[location]

                    if (result != null) {
                        rowSb.append(result.code)
                    }
                }

                if (col != width) {
                    rowSb.append(" ")
                }
            }

            val derivedRow = rowSb.toString().trim()

            if (derivedRow.isNotEmpty()) {
                rows.add(derivedRow)
            }
        }

        return rows.joinToString("\n")
    }

    private fun getHeader() = when (width) {
        4 -> "==========="
        8 -> "=================="
        12 -> "============================="
        else -> ""
    }

    private fun getColumnHeader(): String {
        val headerSb = StringBuilder()

        for (i in 0..width) {
            if (i == 0) {
                headerSb.append(".")
            } else {
                headerSb.append(i)
            }

            if (i != width) {
                headerSb.append(" ")
            }
        }

        return headerSb.toString()
    }

    fun attack(location: Location): String {
        var result = ""

        if (!isValidFor(location)) {
            return "Invalid Location ${location.row}${location.column}. Please select another location."
        }

        if (isOccupied(location)) {
            val occupant = occupants[location]

            if (occupant != null) {
                occupant.onImpact()

                if (occupant.isAfloat()) {
                    result = "Hit. ${occupant.type}."
                } else {
                    result = "Sunk ${occupant.type} of length ${occupant.length}!"
                }
            }

            shotsByLocation[location] = ShotResult.HIT
        } else {
            result = "Miss!"
            shotsByLocation[location] = ShotResult.MISS
        }

        return result
    }

    private fun isValidFor(location: Location) = occupants.containsKey(location)
}