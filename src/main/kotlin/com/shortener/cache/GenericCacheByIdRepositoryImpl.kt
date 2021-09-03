package com.shortener.cache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class GenericCacheByIdRepositoryImpl<T, ID> : GenericCachedRepository<T, ID> {
    @Autowired
    lateinit var repository: JpaRepository<T, ID>

    @CacheEvict(beforeInvocation = true, value = ["cachedById"], key = "#entry.id", condition = "#entry.id != null")
    override fun save(entry: T): T {
        return repository.save(entry!!)

    }

    @Cacheable(value = ["cachedById"], key = "#id", unless = "#result == null")
    override fun findById(id: ID): Optional<T> {
        return repository.findById(id!!)
    }

    @CacheEvict(beforeInvocation = true, value = ["cachedById"], key = "#entry.id", condition = "#entry.id != null")
    override fun delete(entry: T) {
        repository.delete(entry!!)
    }
}
