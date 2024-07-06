package com.koroliuk.bankrestapi.service.impl

import com.koroliuk.bankrestapi.controller.request.UserDto
import com.koroliuk.bankrestapi.model.User
import com.koroliuk.bankrestapi.repository.UserRepository
import com.koroliuk.bankrestapi.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    @Autowired val userRepository: UserRepository
) : UserService {

    companion object {
        const val BATCH_SIZE = 10000
    }

    @Transactional
    override fun updateBalances(balances: Map<Int, Int>) {
        val balanceEntries = balances.entries.toList()
        for (i in balanceEntries.indices step BATCH_SIZE) {
            val batch = balanceEntries.subList(i, minOf(i + BATCH_SIZE, balanceEntries.size))
            processBatch(batch)
        }
    }

    private fun processBatch(batch: List<Map.Entry<Int, Int>>) {
        val userIds = batch.map { it.key }
        val users = userRepository.findAllById(userIds)
        val existingUserIds = users.map { it.id }.toSet()

        val invalidUserIds = userIds - existingUserIds
        if (invalidUserIds.isNotEmpty()) {
            throw IllegalArgumentException("Invalid user IDs: $invalidUserIds")
        }

        users.forEach { user ->
            user.balance = batch.first { it.key == user.id }.value
        }
        userRepository.saveAll(users)
    }

    override fun createUser(userDto: UserDto): User {
        val user = User(
            name = userDto.name,
            balance = userDto.balance
        )
        return userRepository.save(user)
    }

    override fun getUser(id: Int): User {
        return userRepository.findById(id).orElseThrow { IllegalArgumentException("User with ID $id not found") }
    }

    override fun getUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    override fun deleteUser(id: Int) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User with ID $id does not exist")
        }
        userRepository.deleteById(id)
    }

}
