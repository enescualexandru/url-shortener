package com.shortener.data.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCacheService<K : Any, V : Any>(private val redisTemplate: RedisTemplate<K, V>) : CacheService<K, V> {

    @Value("\${spring.cache.type}")
    var cacheType: String = "none"

    override fun save(key: K, value: V) {
        if (isCachingEnabled()) {
            redisTemplate.boundValueOps(key).set(value)
        }
    }

    override fun delete(key: K) {
        if (isCachingEnabled()) {
            redisTemplate.opsForValue().operations.delete(key)
        }
    }

    override fun get(key: K): V? {
        if (isCachingEnabled()) {
            return redisTemplate.boundValueOps(key).get()
        }

        return null
    }

    fun isCachingEnabled() = cacheType != "none"

}
