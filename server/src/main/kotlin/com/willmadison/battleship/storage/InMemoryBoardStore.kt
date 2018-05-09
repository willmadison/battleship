package com.willmadison.battleship.storage

import com.willmadison.battleship.bo.Board
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class InMemoryBoardStore: BoardStore {

    private val boardsById: MutableMap<String, Board> = mutableMapOf()

    override fun save(board: Board) {
        boardsById[board.id] = board
    }

    override fun fetch(id: String): Board? {
        return boardsById[id]
    }
}