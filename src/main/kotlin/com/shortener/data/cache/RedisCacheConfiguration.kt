package com.shortener.data.cache

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@EnableCaching
@Configuration
class RedisCacheConfiguration {

    @Bean
    fun <K, V> redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<K, V> {
        val template = RedisTemplate<K, V>()
        template.setConnectionFactory(connectionFactory)
        template.keySerializer = GenericJackson2JsonRedisSerializer(jsonObjectMapper)
        template.valueSerializer = GenericJackson2JsonRedisSerializer(jsonObjectMapper)
        return template
    }

    private val jsonObjectMapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}
