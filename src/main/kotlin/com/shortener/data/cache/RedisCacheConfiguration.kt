package com.shortener.data.cache

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@EnableCaching
@Configuration
class RedisCacheConfiguration {

    @Bean
    fun <K, V> redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<K, V> {
        val template = RedisTemplate<K, V>()
        template.setConnectionFactory(connectionFactory)
        template.keySerializer = StringRedisSerializer()
        return template
    }

}
