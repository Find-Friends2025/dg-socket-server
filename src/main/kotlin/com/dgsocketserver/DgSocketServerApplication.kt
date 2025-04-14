package com.dgsocketserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DgSocketServerApplication

fun main(args: Array<String>) {
    runApplication<DgSocketServerApplication>(*args)
}
