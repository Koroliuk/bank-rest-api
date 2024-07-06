package com.koroliuk.bankrestapi.service

import com.koroliuk.bankrestapi.controller.request.UserDto
import com.koroliuk.bankrestapi.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun updateBalances(balances: Map<Int, Int>)

    fun createUser(userDto: UserDto): User

    fun getUser(id: Int): User

    fun getUsers(pageable: Pageable): Page<User>

    fun deleteUser(id: Int)

}
