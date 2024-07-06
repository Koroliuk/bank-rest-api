package com.koroliuk.bankrestapi.controller.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank


data class UserDto(
    @field:NotBlank(message = "Name is mandatory")
    val name: String,

    @field:Min(value = 0, message = "Balance must be non-negative")
    val balance: Int
)
