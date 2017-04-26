package com.willmadison.battleship.bo

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class LocationRangeTests {

    @Test(expected = InvalidLocationException::class)
    fun locationValidationRules() {
        val startTooShort = LocationRange("A", "A4")
        val startHasBadColumn = LocationRange("AA", "A1")
    }

    @Test(expected = InvalidLocationRangeException::class)
    fun rangeValidationRules() {
        val startAfterEnd = LocationRange("A3", "A1")
        val diagonalRange = LocationRange("A1", "C4")
    }

    @Test
    fun rangeLength() {
        assertEquals(3, LocationRange("A1", "A3").length)
        assertEquals(3, LocationRange("A1", "C1").length)
        assertEquals(12, LocationRange("A1", "A12").length)
    }

    @Test
    fun rangesContainIntermediates() {
        val range = LocationRange("A1", "A10")

        for (col in 1..10) {
            assertTrue(range.locations.contains(Location('A', col)))
        }

        assertEquals(range.length, range.locations.size)
    }
}