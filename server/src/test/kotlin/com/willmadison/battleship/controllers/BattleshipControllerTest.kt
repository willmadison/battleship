package com.willmadison.battleship.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.willmadison.battleship.bo.Board
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@ActiveProfiles(profiles = ["dev"])
@SpringBootTest
class BattleshipControllerTest {

    @Autowired
    private lateinit var ctx: WebApplicationContext

    private lateinit var mvc: MockMvc

    private val mapper = ObjectMapper()

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build()
    }

    @Test
    fun createBoard() {
        val result = mvc.perform(post("/battleship/boards").param("width", "4"))
                .andExpect(status().isCreated)
                .andReturn()

        assertThat(result.response.contentAsString).isNotNull()

        val board = mapper.readValue(result.response.contentAsString, Board::class.java)

        assertThat(board).isNotNull
        assertThat(board.id).isNotNull()
        assertThat(board.width).isEqualTo(4)
        assertThat(board.shotsByLocation.isEmpty()).isTrue()
    }

    @Test
    fun shouldSupportAttackSequences() {
        var result = mvc.perform(post("/battleship/boards").param("width", "10"))
                .andExpect(status().isCreated)
                .andReturn()

        val board = mapper.readValue(result.response.contentAsString, Board::class.java)

        assertThat(board.id).isNotNull()

        mvc.perform(post("/battleship/boards/${board.id}/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cruiserRequest("A1:A3")))
                .andExpect(status().isOk)

        result = mvc.perform(post("/battleship/boards/${board.id}/attack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attackLocation("A1")))
                .andExpect(status().isOk)
                .andReturn()

        var response = mapper.readValue(result.response.contentAsString, AttackResponse::class.java)

        assertThat(response.shotResult).isEqualTo("Hit. Cruiser.")
        assertThat(response.board.shotsByLocation["A1"]).isEqualTo(Board.ShotResult.HIT)

        result = mvc.perform(post("/battleship/boards/${board.id}/attack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attackLocation("B1")))
                .andExpect(status().isOk)
                .andReturn()

        response = mapper.readValue(result.response.contentAsString, AttackResponse::class.java)

        assertThat(response.shotResult).isEqualTo("Miss!")
        assertThat(response.board.shotsByLocation["B1"]).isEqualTo(Board.ShotResult.MISS)

        for (location in arrayOf("A2", "A3")) {
            result = mvc.perform(post("/battleship/boards/${board.id}/attack")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(attackLocation(location)))
                    .andExpect(status().isOk)
                    .andReturn()
        }

        response = mapper.readValue(result.response.contentAsString, AttackResponse::class.java)

        assertThat(response.shotResult).isEqualTo("Sunk Cruiser!")
    }

    @Test
    fun shouldDisallowInvalidBoardLocation() {
        val result = mvc.perform(post("/battleship/boards").param("width", "10"))
                .andExpect(status().isCreated)
                .andReturn()

        val board = mapper.readValue(result.response.contentAsString, Board::class.java)

        assertThat(board.id).isNotNull()

        mvc.perform(post("/battleship/boards/${board.id}/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cruiserRequest("A9:A11")))
                .andExpect(status().is4xxClientError)

        mvc.perform(post("/battleship/boards/${board.id}/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cruiserRequest("X9:Z9")))
                .andExpect(status().is4xxClientError)

        mvc.perform(post("/battleship/boards/${board.id}/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cruiserRequest("A1:C3")))
                .andExpect(status().is4xxClientError)
    }

    @Test
    fun shouldDisallowNonExistentBoardMoves() {
        mvc.perform(post("/battleship/boards/foo/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cruiserRequest("A9:A11")))
                .andExpect(status().is4xxClientError)

        mvc.perform(post("/battleship/boards/foo/attack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attackLocation("A1")))
                .andExpect(status().is4xxClientError)
                .andReturn()
    }

    private fun attackLocation(location: String): String {
        return """
            {
                "location": "$location"
            }
        """.trimIndent()
    }

    private fun cruiserRequest(range: String): String {
       return """
           {
                "shipClassification": "Cruiser",
                "range": "$range"
           }
"""".trimIndent()
    }
}
