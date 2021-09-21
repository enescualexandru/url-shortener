package com.shortener.data.cache

interface CacheService<K : Any, V : Any> {
    fun save(key: K, value: V)
    fun delete(key: K)
    fun get(key: K): V?
}
