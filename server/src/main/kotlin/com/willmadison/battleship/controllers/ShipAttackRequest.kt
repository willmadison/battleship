package com.willmadison.battleship.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ShipAttackRequest @JsonCreator constructor (@JsonProperty("location") val location: String)