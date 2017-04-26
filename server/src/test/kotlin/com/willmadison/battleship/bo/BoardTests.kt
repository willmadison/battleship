package com.willmadison.battleship.bo

import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test

class BoardTests {

    private lateinit var board: Board
    private lateinit var ship: Ship

    @Before
    fun setUp() {
        board = Board(4)
        ship = Cruiser()
    }

    @Test(expected = InvalidLocationRangeException::class)
    fun shipPlacementInvalidLocation_throws() {
        val range = LocationRange("A4", "A1")
        board.place(ship, range)
    }

    @Test
    fun shipPlacementValidLocationRange() {
        val range = LocationRange("A1", "A3")
        board.place(ship, range)
    }

    @Test
    fun occupancy() {
        val range = LocationRange("A1", "A3")
        board.place(ship, range)

        for (location in listOf(Location('A', 1), Location('A', 2), Location('A', 3))) {
            assertTrue(board.isOccupied(location))
        }

        for (location in listOf(Location('B', 1), Location('B', 2), Location('B', 3))) {
            assertFalse(board.isOccupied(location))
        }
    }

    @Test(expected = InvalidShipPlacementException::class)
    fun shipPlacementOverlappingShips() {
        var range = LocationRange("A1", "A3")
        board.place(ship, range)


        range = LocationRange("A2", "C2")
        board.place(Cruiser(), range)
    }

    @Test
    fun shipPlacementNonOverlappingShips() {
        var range = LocationRange("A1", "A3")
        board.place(ship, range)

        range = LocationRange("A4", "C4")
        board.place(Cruiser(), range)

        for (location in listOf(Location('A', 4), Location('B', 4), Location('C', 4))) {
            assertTrue(board.isOccupied(location))
        }
    }

    @Test
    fun boardDisplay() {
        assertEquals("""===========
. 1 2 3 4
A
B
C
D
===========""", Board(4).display())

        assertEquals("""==================
. 1 2 3 4 5 6 7 8
A
B
C
D
E
F
G
H
==================""", Board(8).display())

      assertEquals("""=============================
. 1 2 3 4 5 6 7 8 9 10 11 12
A
B
C
D
E
F
G
H
I
J
K
L
=============================""", Board(12).display())
    }

    @Test
    fun attackSequence() {
        val range = LocationRange("A1", "A3")
        board.place(ship, range)

        var result = board.attack(Location('A', 2))
        assertEquals("Hit. Cruiser.", result)

        board.attack(Location('A', 1))

        result = board.attack(Location('B', 17))
        assertEquals("Invalid Location B17. Please select another location.", result)

        result = board.attack(Location('B', 1))
        assertEquals("Miss!", result)

        result = board.attack(Location('A', 3))
        assertEquals("Sunk Cruiser of length 3!", result)

        assertEquals("""===========
. 1 2 3 4
A H H H
B M
C
D
===========""", board.display())
    }
}