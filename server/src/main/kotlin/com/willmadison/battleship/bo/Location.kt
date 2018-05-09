package com.willmadison.battleship.bo

data class Location(val row: Char, val column: Int) {
    fun isBefore(other: Location) = this.row < other.row || this.column < other.column
    fun isDiagonalTo(other: Location) = this.row != other.row && this.column != other.column
    fun inSameRow(other: Location) = this.row == other.row
    fun inSameColumn(other: Location) = this.column == other.column

    override fun toString(): String {
        return "$row$column".toUpperCase()
    }
}

fun String.toLocation(): Location {
    if (this.length < 2) {
        throw InvalidLocationException("location code must be at least 2 characters long")
    }

    val row = this[0]

    if (!row.isLetter()) {
        throw InvalidLocationException("location code must begin with an alphabetic character")
    }

    val column = this.slice(1 until this.length).toInt()

    return Location(row, column)
}