package com.willmadison.battleship.bo

data class LocationRange(private val start: String, private val end: String) {
    val length: Int
    val locations: List<Location>

    init {
        val startLocation = start.toLocation()
        val endLocation = end.toLocation()

        if (!startLocation.isBefore(endLocation)) {
            throw InvalidLocationRangeException("invalid range: [$start:$end]; start location must come before the end location")
        }

        if (startLocation.isDiagonalTo(endLocation)) {
            throw InvalidLocationRangeException("invalid range: [$start:$end]; start and end locations must not be diagonal to one another.")
        }

        length = when {
            startLocation.inSameColumn(endLocation) -> endLocation.row - startLocation.row + 1
            startLocation.inSameRow(endLocation) -> endLocation.column - startLocation.column + 1
            else -> 0
        }

        locations = locationsInRange(startLocation, endLocation)
    }

    private fun locationsInRange(start: Location, end: Location): List<Location> {
        val locations = mutableListOf<Location>()

        locations.add(start)

        if (start.inSameColumn(end)) {
            for (row in start.row+1 until end.row) {
                locations.add("$row${start.column}".toLocation())
            }
        } else if (start.inSameRow(end)) {
            for (col in start.column+1 until end.column) {
                locations.add("${start.row}$col".toLocation())
            }
        }

        locations.add(end)

        return locations
    }

    fun isValidFor(board: Board, ship: Ship) = this.length < board.width &&
            this.length == ship.length &&
            this.locations.none { it.column > board.width } &&
            this.locations.none {it.row > 'A'.plus(board.width)}
}
