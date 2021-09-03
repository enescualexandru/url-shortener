package com.shortener.cache

import java.util.*

interface GenericCachedRepository<T, ID> {
    fun save(entry: T): T
    fun findById(id: ID): Optional<T>
    fun delete(entry: T)
}
