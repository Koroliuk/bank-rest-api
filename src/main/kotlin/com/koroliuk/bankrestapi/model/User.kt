package com.koroliuk.bankrestapi.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null,
    var name: String,
    var balance: Int
)

