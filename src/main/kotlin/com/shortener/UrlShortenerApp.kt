package com.shortener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class UrlShortenerApp

fun main(args: Array<String>) {
    runApplication<UrlShortenerApp>(*args)
//    val timer = Timer()
//    timer.log()
}
