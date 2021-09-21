package com.shortener.data.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCacheService<K : Any, V : Any>(private val redisTemplate: RedisTemplate<K, V>) : CacheService<K, V> {

    override fun save(key: K, value: V) {
        redisTemplate.boundValueOps(key).set(value)
    }

    override fun delete(key: K) {
        redisTemplate.opsForValue().operations.delete(key)
    }

    override fun get(key: K): V? {
        return redisTemplate.boundValueOps(key).get()
    }

}
