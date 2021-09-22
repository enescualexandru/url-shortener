package com.shortener.data.cache

import org.springframework.stereotype.Component

/**
 *  Lazy caching: cache the entries on retrieval, evict them on save/delete
 */
@Component
class CacheWithRepositoryFallback<K : Any, V : Any, R : Any>(private val cacheService: CacheService<K, V>) {

    fun getOrDefault(key: K, fallback: () -> V?): V? {
        var entry = cacheService.get(key)
        if (entry == null) {
            entry = fallback()
            if (entry != null) {
                cacheService.save(key, entry)
            }
        }

        return entry
    }

    fun delete(key: K?, action: () -> Nothing) {
        if (key != null) {
            cacheService.delete(key)
        }

        action()
    }

    fun save(key: K?, action: () -> R): R {
        if (key != null) {
            cacheService.delete(key)
        }

        return action()
    }

}
