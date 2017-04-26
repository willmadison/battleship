package com.willmadison.battleship.bo

data class Ship(val type: String, val length: Int, private var strength: Int) {
    fun onImpact() {
        strength--
    }

    fun isAfloat() = strength > 0
}

fun Cruiser() = Ship("Cruiser", 3, 3)
fun Submarine() = Ship("Submarine", 3, 3)
fun Destroyer() = Ship("Destroyer", 2, 2)