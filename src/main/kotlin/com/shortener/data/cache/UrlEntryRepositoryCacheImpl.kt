package com.shortener.data.cache

import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry
import com.shortener.data.repository.UrlEntryRepositoryBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

const val CACHE_NAME_URL_ENTRY = "url-entry-cache"

@Component
class UrlEntryRepositoryCacheImpl : UrlEntryRepositoryBase {

    @Autowired
    @Qualifier("urlEntryRepository")
    private lateinit var urlEntryRepository: UrlEntryRepositoryBase

    @CacheEvict(
        beforeInvocation = true,
        value = [CACHE_NAME_URL_ENTRY],
        key = "#entry.encodedSequence.sequence",
        condition = "#entry.encodedSequence != null"
    )
    override fun save(entry: UrlEntry): UrlEntry = urlEntryRepository.save(entry)

    @CacheEvict(
        beforeInvocation = true,
        value = [CACHE_NAME_URL_ENTRY],
        key = "#entry.encodedSequence.sequence",
        condition = "#entry.encodedSequence != null"
    )
    override fun delete(entry: UrlEntry) = urlEntryRepository.delete(entry)

    @Cacheable(
        value = [CACHE_NAME_URL_ENTRY],
        key = "#encodedSequence.sequence",
        unless = "#encodedSequence.sequence == '' || #result == null"
    )
    override fun findByEncodedSequence(encodedSequence: EncodedSequence): UrlEntry? =
        urlEntryRepository.findByEncodedSequence(encodedSequence)

}
