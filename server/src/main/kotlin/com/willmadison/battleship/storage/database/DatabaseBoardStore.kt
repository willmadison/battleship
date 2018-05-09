package com.willmadison.battleship.storage.database

import com.willmadison.battleship.bo.Board
import com.willmadison.battleship.storage.BoardStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class DatabaseBoardStore @Autowired constructor(private val repository: BoardRepository) : BoardStore {

    override fun save(board: Board) {
        val entity = board.toEntity()
        repository.save(entity)
    }

    override fun fetch(id: String): Board? {
        val optionalEntity = repository.findById(id)

        return when {
            optionalEntity.isPresent -> optionalEntity.get().toBoard()
            else -> null
        }
    }
}

private fun BoardEntity.toBoard(): Board {
    val board = Board(this.width)
    val shotsByLocation = mutableMapOf<String, Board.ShotResult>()
    board.shotsByLocation = shotsByLocation

    return board
}

private fun Board.toEntity(): BoardEntity {
    val serializedShotInfo = ""
    return BoardEntity(this.id, this.width, this.shotsByLocation.serialize())
}

private fun MutableMap<String, Board.ShotResult>.serialize(): String {

}
