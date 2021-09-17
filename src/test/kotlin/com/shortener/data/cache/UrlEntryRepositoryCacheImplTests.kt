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
        urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence("a1b1"))
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Bad arguments, no entry will be cached`() {
        urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence("a1b1"))
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Entries are cached on retrieval`() {
        val urlEntry = TestUtils.createUrlEntry()
        val persistedEntry = urlEntryRepositoryCacheImpl.save(urlEntry)
        val encodedSequence = persistedEntry.encodedSequence!!.sequence
        urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence(encodedSequence))
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
            urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence(encodedSequence))!!
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)

        urlEntryRepositoryCacheImpl.delete(persistedEntry)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Entries are evicted from cache on saving`() {
        val urlEntry = TestUtils.createUrlEntry()
        var persistedEntry = urlEntryRepositoryCacheImpl.save(urlEntry)
        val encodedSequence = persistedEntry.encodedSequence!!.sequence
        persistedEntry =
            urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence(encodedSequence))!!
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)

        urlEntryRepositoryCacheImpl.save(persistedEntry)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    private fun getAllKeysFromCache(): Set<ByteArray> =
        redisTemplate.connectionFactory!!.connection.keys("*".toByteArray()) as Set<ByteArray>

    private fun getValueFromCache(key: ByteArray) = redisTemplate.connectionFactory!!.connection.get(key)

    private fun isSequenceCached(sequence: String): Boolean =
        getAllKeysFromCache().map { deserializeKey(it) }.any { it.contains(sequence) }

    private fun deserializeKey(key: ByteArray): String = redisTemplate.keySerializer.deserialize(key).toString()

    private fun deserializeValue(value: ByteArray?): UrlEntry =
        redisTemplate.valueSerializer.deserialize(value) as UrlEntry

    private fun getUrlEntryFromCache(sequence: String): UrlEntry? {
        if (isSequenceCached(sequence)) {
            val key = getAllKeysFromCache().map { deserializeKey(it) }.first { it.endsWith(sequence) }
            return deserializeValue(getValueFromCache(key.toByteArray()))
        }

        return null
    }
}
