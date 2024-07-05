package com.koroliuk.bankrestapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankRestApiApplication

fun main(args: Array<String>) {
    runApplication<BankRestApiApplication>(*args)
}
