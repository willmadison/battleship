package com.willmadison.battleship.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.willmadison.battleship.bo.Board

data class AttackResponse @JsonCreator constructor (@JsonProperty("shotResult") val shotResult: String,
                                                    @JsonProperty("board") val board: Board)
