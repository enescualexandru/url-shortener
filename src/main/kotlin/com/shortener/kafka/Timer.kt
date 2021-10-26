package com.shortener.kafka

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime

import java.time.format.DateTimeFormatter

@Component
class Timer {
//    @Value("\${spring.kafka.bootstrap-servers}")
//    var springBootStrapServers1: String = "none"
//
//    @Value("\${SPRING_KAFKA_BOOTSTRAP-SERVERS}")
//    var springBootStrapServers2: String = "none"
//
//    @Value("\${kafka.server}")
//    var springBootStrapServers3: String = "none"
//
//    @Value("\${KAFKA_BOOTSTRAP_SERVER}")
//    var springBootStrapServers4: String = "none"

    @Throws(InterruptedException::class)
    @Async
    fun log() {
//        logger.info("springBootStrapServers1: $springBootStrapServers1")
//        logger.info("springBootStrapServers2: $springBootStrapServers2")
//        logger.info("springBootStrapServers3: $springBootStrapServers3")
//        logger.info("springBootStrapServers4: $springBootStrapServers4")
//        val env = System.getenv();
//        for (envName in  env.keys) {
//            logger.info("%s=%s%n", envName, env[envName])
//        }
        while (true) {
            logger.error(
                "Inside scheduleTask - Sending logs to Kafka at " + DateTimeFormatter.ofPattern("HH:mm:ss")
                    .format(LocalDateTime.now())
            )
            Thread.sleep(3000)
        }
    }

    companion object {
        //private val logger = LoggerFactory.getLogger(Timer::class.java)
        private val logger: Logger = LogManager.getLogger(Timer::class.java)
    }
}
