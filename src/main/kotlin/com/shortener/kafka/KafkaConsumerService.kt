package com.shortener.kafka

//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class KafKaConsumerService {
    //private val logger = LoggerFactory.getLogger(KafKaConsumerService::class.java)

    //@KafkaListener(topics = ["test1"], groupId = "group-id")
    //fun consume(message: String) {
        //logger.error(String.format("Message received -> %s", message))
   // }

//    companion object {
//        private val logger: Logger = LoggerFactory.getLogger(KafKaProducerService::class.java)
//    }

    companion object {
        //private val logger: Logger = LoggerFactory.getLogger(Timer::class.java)
        private val logger: Logger = LogManager.getLogger(Timer::class.java)
    }
}
