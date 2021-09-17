package com.shortener.data.cache

import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry
import com.shortener.data.repository.UrlEntryRepositoryBase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
@CacheConfig(cacheNames = ["url-entry-cache"])
class UrlEntryRepositoryCacheImpl(@Qualifier("urlEntryRepository") private val urlEntryRepository: UrlEntryRepositoryBase) :
    UrlEntryRepositoryBase {

    @CacheEvict(
        beforeInvocation = true,
        key = "#entry.encodedSequence.sequence",
        condition = "#entry.encodedSequence != null"
    )
    override fun save(entry: UrlEntry): UrlEntry = urlEntryRepository.save(entry)

    @CacheEvict(
        beforeInvocation = true,
        key = "#entry.encodedSequence.sequence",
        condition = "#entry.encodedSequence != null"
    )
    override fun delete(entry: UrlEntry) = urlEntryRepository.delete(entry)

    @Cacheable(
        key = "#encodedSequence.sequence",
        unless = "#encodedSequence.sequence == '' || #result == null"
    )
    override fun findByEncodedSequence(encodedSequence: EncodedSequence): UrlEntry? =
        urlEntryRepository.findByEncodedSequence(encodedSequence)

}
