package com.shortener.data.cache

import com.shortener.TestUtils
import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry
import com.shortener.data.repository.UrlEntryRepositoryBase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test-cache")
@SpringBootTest(classes = [TestCacheConfig::class])
@Transactional
class UrlEntryRepositoryCacheImplTests(
    @Autowired val urlEntryRepositoryCacheImpl: UrlEntryRepositoryBase,
    @Autowired val redisTemplate: RedisTemplate<String, UrlEntry>,
    @Autowired val cacheManager: CacheManager
) {

    @BeforeEach
    fun clearCache() {
        cacheManager.cacheNames.forEach { cacheManager.getCache(it)!!.clear() }
    }

    @Test
    fun `Entries not cached upon saving`() {
        val urlEntry = TestUtils.createUrlEntry()
        urlEntryRepositoryCacheImpl.save(urlEntry)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Non existent entries are not cached`() {
        urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence().apply { sequence = "a1b1" })
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Bad arguments, no entry will be cached`() {
        urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence().apply { sequence = "" })
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Entries are cached on retrieval`() {
        val urlEntry = TestUtils.createUrlEntry()
        val persistedEntry = urlEntryRepositoryCacheImpl.save(urlEntry)
        val encodedSequence = persistedEntry.encodedSequence!!.sequence
        urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence().apply { sequence = encodedSequence })
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        Assertions.assertThat(isSequenceCached(encodedSequence)).isTrue
        Assertions.assertThat(persistedEntry).isEqualTo(getUrlEntryFromCache(encodedSequence))
    }

    @Test
    fun `Entries are evicted from cache on deletion`() {
        val urlEntry = TestUtils.createUrlEntry()
        var persistedEntry = urlEntryRepositoryCacheImpl.save(urlEntry)
        val encodedSequence = persistedEntry.encodedSequence!!.sequence
        persistedEntry =
            urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence().apply { sequence = encodedSequence })!!
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)

        urlEntryRepositoryCacheImpl.delete(persistedEntry)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    fun getAllKeysFromCache(): Set<ByteArray> =
        redisTemplate.connectionFactory!!.connection.keys("*".toByteArray()) as Set<ByteArray>

    fun isSequenceCached(sequence: String): Boolean =
        getAllKeysFromCache().map { getKeyFromByteArray(it) }.any { it.contains(sequence) }

    fun getKeyFromByteArray(key: ByteArray): String = redisTemplate.keySerializer.deserialize(key).toString()

    fun getValueFromByteArray(value: ByteArray): UrlEntry? =
        redisTemplate.valueSerializer.deserialize(value) as UrlEntry

    fun getUrlEntryFromCache(sequence: String?): UrlEntry? =
        getAllKeysFromCache().map { getValueFromByteArray((redisTemplate.connectionFactory!!.connection.get(it)!!)) }
            .first()
}
