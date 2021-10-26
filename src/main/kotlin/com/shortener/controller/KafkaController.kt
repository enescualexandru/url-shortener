package com.shortener.controller

import com.shortener.kafka.KafKaProducerService
import com.shortener.kafka.Timer
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = ["/kafka"])
class KafkaController(private val producerService: KafKaProducerService, private val timer: Timer) {

    @PostMapping(value = ["/publish"])
    fun sendMessageToKafkaTopic(@RequestParam("message") message: String?) {
        producerService.sendMessage(message)
    }

    @GetMapping(value = ["/trigger"])
    fun trigger() {
        timer.log()
    }
}
