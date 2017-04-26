package com.willmadison.battleship

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class BattleshipApplication

fun main(args: Array<String>) {
    SpringApplication.run(BattleshipApplication::class.java, *args)
}