package com.willmadison.battleship.controllers

import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@ActiveProfiles(profiles = arrayOf("test"))
@SpringBootTest
class BattleshipControllerTest {

    @Autowired
    lateinit private var ctx: WebApplicationContext

    lateinit private var mvc: MockMvc

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build()
    }

    @Test
    fun createBoard() {
        val result = mvc.perform(post("/battleship/boards").param("width", "4"))
                .andExpect(status().isCreated)
                .andReturn()

        assertNotNull(result.response.contentAsString)
    }
}