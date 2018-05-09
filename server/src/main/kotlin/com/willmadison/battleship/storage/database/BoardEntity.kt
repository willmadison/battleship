package com.willmadison.battleship.storage.database

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "BOARDS")
class BoardEntity(@Id val id: String, @Column(name = "width") val width: Int, @Column(name = "SHOT_INFORMATION") val shotInfo: String) {
    constructor() : this(UUID.randomUUID().toString(), 0, "")
}