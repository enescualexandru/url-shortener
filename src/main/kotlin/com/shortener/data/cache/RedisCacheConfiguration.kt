package com.shortener.data.cache

import com.shortener.data.domain.UrlEntry
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
    fun redisTemplateUrlEntry(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, UrlEntry> {
        val template: RedisTemplate<String, UrlEntry> = RedisTemplate()
        template.setConnectionFactory(connectionFactory!!)
        template.keySerializer = StringRedisSerializer()
        return template
    }

}
