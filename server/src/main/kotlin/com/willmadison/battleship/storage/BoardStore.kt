package com.willmadison.battleship.storage

import com.willmadison.battleship.bo.Board

interface BoardStore {
    fun save(board: Board)
    fun fetch(id: String): Board?
}