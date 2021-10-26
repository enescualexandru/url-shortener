package com.shortener.kafka

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class KafKaProducerService(private val kafkaTemplate: KafkaTemplate<String, String?>) {

    fun sendMessage(message: String?) {
        logger.info(String.format("Message sent -> %s", message))
        kafkaTemplate.send("test1", message)
    }

    //    companion object {
//        private val logger: Logger = LoggerFactory.getLogger(KafKaProducerService::class.java)
//    }
    companion object {
        //private val logger: Logger = LoggerFactory.getLogger(Timer::class.java)
        private val logger: Logger = LogManager.getLogger(Timer::class.java)
    }

    fun trigger() {
        val timer:Timer = Timer()
        timer.log()
    }
}
