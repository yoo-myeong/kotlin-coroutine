package com.kotlin.coroutine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@SpringBootApplication
@EnableR2dbcAuditing
class CoroutineApplication

fun main(args: Array<String>) {
    runApplication<CoroutineApplication>(*args)
}
