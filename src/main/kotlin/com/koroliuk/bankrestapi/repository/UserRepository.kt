package com.koroliuk.bankrestapi.repository

import com.koroliuk.bankrestapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int>
