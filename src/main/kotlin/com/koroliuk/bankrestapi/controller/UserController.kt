package com.koroliuk.bankrestapi.controller

import com.koroliuk.bankrestapi.controller.request.UserDto
import com.koroliuk.bankrestapi.model.User
import com.koroliuk.bankrestapi.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
class UserController(
    @Autowired val userService: UserService
) {

    @PostMapping("/set-users-balance")
    fun setUserBalances(@RequestBody balances: Map<Int, Int>): ResponseEntity<Any> {
        return try {
            userService.updateBalances(balances)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message, e);
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: UserDto): ResponseEntity<User> {
        val createdUser = userService.createUser(user)
        return ResponseEntity(createdUser, HttpStatus.CREATED)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Int): ResponseEntity<String> {
        return try {
            userService.deleteUser(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e);
        }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val user = userService.getUser(id)
            ResponseEntity(user, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message, e);
        }
    }

    @GetMapping
    fun getUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<User>> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by("id"))
        val usersPage = userService.getUsers(pageable)
        return ResponseEntity(usersPage, HttpStatus.OK)
    }

}
