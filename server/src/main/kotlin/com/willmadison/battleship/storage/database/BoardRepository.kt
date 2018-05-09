package com.willmadison.battleship.storage.database

import org.springframework.data.repository.CrudRepository

interface BoardRepository: CrudRepository<BoardEntity, String>