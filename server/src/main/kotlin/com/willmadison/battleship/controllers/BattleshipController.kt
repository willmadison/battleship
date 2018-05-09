package com.willmadison.battleship.controllers

import com.willmadison.battleship.bo.*
import com.willmadison.battleship.storage.BoardStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/battleship")
class BattleshipController @Autowired constructor(private val boardStore: BoardStore){

    private val logger = LoggerFactory.getLogger(BattleshipController::class.java)

    @PostMapping("/boards")
    fun createBoard(@RequestParam width: Int): ResponseEntity<Board> {
        val board = Board(width)
        boardStore.save(board)
        return ResponseEntity(board, HttpStatus.CREATED)
    }

    @PostMapping("/boards/{id}/place")
    fun place(@PathVariable id: String, @RequestBody placementRequest: ShipPlacementRequest): ResponseEntity<Any>? {
        val ship = when (placementRequest.shipClassification) {
            "Cruiser" -> Cruiser()
            "Submarine" -> Submarine()
            else -> Destroyer()
        }

        val rangeLocations = placementRequest.range.split(":")

        val board = boardStore.fetch(id)

        if (board != null) {
            return try {
                board.place(ship, LocationRange(rangeLocations[0], rangeLocations[1]))
                ResponseEntity.ok().build()
            } catch (ispe: InvalidShipPlacementException) {
                logger.warn("encountered an invalid ship placement:", ispe)
                ResponseEntity.badRequest().build()
            } catch (ilre: InvalidLocationRangeException) {
                logger.warn("encountered a diagonally placed ship", ilre)
                ResponseEntity.badRequest().build()
            }
        }

        logger.warn("invalid board id: ({})", id)
        return ResponseEntity.badRequest().build()
    }

    @PostMapping("/boards/{id}/attack")
    fun attack(@PathVariable id: String, @RequestBody attackRequest: ShipAttackRequest): ResponseEntity<AttackResponse>? {
        val attackLocation = attackRequest.location.toLocation()

        val board = boardStore.fetch(id)

        if (board != null) {
            val result = board.attack(attackLocation)
            return ResponseEntity.ok(AttackResponse(result, board))
        }

        logger.warn("invalid board id: ({})", id)
        return ResponseEntity.badRequest().build()
    }


}