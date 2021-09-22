package com.shortener.data.cache

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@TestConfiguration
class TestCacheConfig(redisProperties: RedisProperties) {
    private val redisServer: RedisServer = RedisServer(redisProperties.port)

    @PostConstruct
    fun startRedisServer() {
        redisServer.start()
    }

    @PreDestroy
    fun stopRedisServer() {
        redisServer.stop()
    }

}
