package com.koroliuk.bankrestapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.koroliuk.bankrestapi.controller.request.UserDto
import com.koroliuk.bankrestapi.model.User
import com.koroliuk.bankrestapi.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun createUserShouldReturnCreatedUser() {
        val userDto = UserDto(name = "John Doe1", balance = 100)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value(userDto.name))
            .andExpect(jsonPath("$.balance").value(userDto.balance))
    }

    @Test
    fun getUserShouldReturnUser() {
        val user = userRepository.save(User(name = "John Doe2", balance = 100))

        mockMvc.perform(get("/api/users/${user.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.id))
            .andExpect(jsonPath("$.name").value(user.name))
            .andExpect(jsonPath("$.balance").value(user.balance))
    }

    @Test
    fun deleteUserShouldReturnNoContent() {
        val user = userRepository.save(User(name = "John Doe3", balance = 100))

        mockMvc.perform(delete("/api/users/${user.id}"))
            .andExpect(status().isNoContent)

        val foundUser = userRepository.findById(user.id!!)
        assertEquals(false, foundUser.isPresent)
    }

    @Test
    fun getUsersShouldReturnUsers() {
        val user1 = userRepository.save(User(name = "John Doe3", balance = 100))
        val user2 = userRepository.save(User(name = "John Doe4", balance = 100))

        mockMvc.perform(get("/api/users?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(user1.id))
            .andExpect(jsonPath("$.content[0].name").value(user1.name))
            .andExpect(jsonPath("$.content[0].balance").value(user1.balance))
            .andExpect(jsonPath("$.content[1].id").value(user2.id))
            .andExpect(jsonPath("$.content[1].name").value(user2.name))
            .andExpect(jsonPath("$.content[1].balance").value(user2.balance))
    }

    @Test
    fun updateBalances() {
        val user = userRepository.save(User(name = "John Doe5", balance = 100))
        val balances = mapOf(user.id!! to 200)

        mockMvc.perform(
            post("/api/users/set-users-balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balances))
        )
            .andExpect(status().isNoContent)

        val updatedUser = userRepository.findById(user.id!!).get()
        assertEquals(200, updatedUser.balance)
    }

    @Test
    fun updateBalancesShouldReturnNotFound() {
        val id = 999
        userRepository.deleteById(id)
        val balances = mapOf(id.toString() to 200)

        mockMvc.perform(
            post("/api/users/set-users-balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balances))
        )
            .andExpect(status().isNotFound)
    }
}
