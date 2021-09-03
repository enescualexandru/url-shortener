package com.shortener.cache

import com.shortener.domain.UrlEntry
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@EnableCaching
@Configuration
class RedisCacheConfiguration {

    @Bean
    fun redisTemplateUrlEntry(connectionFactory: RedisConnectionFactory?): RedisTemplate<Long?, UrlEntry?> {
        val template: RedisTemplate<Long?, UrlEntry?> = RedisTemplate()
        template.setConnectionFactory(connectionFactory!!)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()

        return template
    }
}
