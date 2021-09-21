package com.shortener.data.cache

import com.shortener.GOOD_URL
import com.shortener.TestUtils
import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry
import com.shortener.data.repository.UrlEntryRepository
import com.shortener.dto.UrlShortenRequest
import com.shortener.exception.InvalidInputUrl
import com.shortener.service.UrlService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test-cache")
@SpringBootTest(classes = [TestCacheConfig::class])
@Transactional
class RedisCacheServiceTests(
    @Autowired val urlService: UrlService,
    @Autowired val urlEntryRepository: UrlEntryRepository,
    @Autowired val redisTemplate: RedisTemplate<String, UrlEntry>,
    @Autowired val cacheService: CacheService<String, UrlEntry>
) {

    @BeforeEach
    fun clearCache() {
        redisTemplate.connectionFactory!!.connection.keys("*".toByteArray())?.forEach {
            redisTemplate.delete(deserializeKey(it))
        }
    }

    @Test
    fun `Save entry to cache`() {
        val urlEntry = TestUtils.createUrlEntry()
        val sequence = urlEntry.encodedSequence!!.sequence
        cacheService.save(sequence, urlEntry)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        Assertions.assertThat(urlEntry).isEqualTo(getUrlEntryFromCache(sequence))
    }

    @Test
    fun `Delete entry from cache`() {
        val urlEntry = TestUtils.createUrlEntry()
        val sequence = urlEntry.encodedSequence!!.sequence
        cacheService.save(sequence, urlEntry)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        cacheService.delete(sequence)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Get entry from cache`() {
        val urlEntry = TestUtils.createUrlEntry()
        val sequence = urlEntry.encodedSequence!!.sequence
        cacheService.save(sequence, urlEntry)
        System.err.println("KEYS: ")
        getAllKeysFromCache().forEach { System.err.println(deserializeKey(it)) }

        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        Assertions.assertThat(cacheService.get(sequence)).isEqualTo(urlEntry)
    }

    @Test
    fun `Entries not cached upon saving`() {
        urlService.shortenUrl(UrlShortenRequest(GOOD_URL))
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Non existent entries are not cached`() {
        assertThrows(InvalidInputUrl::class.java) { urlService.decodeSequence("a1b1") }
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
    }

    @Test
    fun `Entries are cached on retrieval`() {
        val response = urlService.shortenUrl(UrlShortenRequest(GOOD_URL))
        val sequence = response.shortenedUrl.substringAfter("/e/")

        urlService.decodeSequence(sequence)
        val persistedEntry = urlEntryRepository.findByEncodedSequence(EncodedSequence(sequence))
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        Assertions.assertThat(persistedEntry).isEqualTo(getUrlEntryFromCache(sequence))
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
