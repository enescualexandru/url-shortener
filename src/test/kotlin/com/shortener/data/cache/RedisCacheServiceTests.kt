package com.shortener.data.cache

import com.shortener.GOOD_URL
import com.shortener.TestUtils
import com.shortener.data.domain.EncodedSequence
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
    @Autowired val redisTemplate: RedisTemplate<String, String>,
    @Autowired val cacheService: CacheService<String, String>
) {

    @BeforeEach
    fun clearCache() {
        redisTemplate.connectionFactory!!.connection.keys("*".toByteArray())?.forEach {
            redisTemplate.delete(deserializeKey(it))
        }
    }

    @Test
    fun `Save to cache and verify the result`() {
        val urlEntry = TestUtils.createUrlEntry()
        val sequence = urlEntry.encodedSequence!!.sequence
        cacheService.save(sequence, urlEntry.longUrl)

        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        Assertions.assertThat(cacheService.get(sequence)).isEqualTo(urlEntry.longUrl)
    }

    @Test
    fun `Delete entry from cache`() {
        val urlEntry = TestUtils.createUrlEntry()
        val sequence = urlEntry.encodedSequence!!.sequence
        cacheService.save(sequence, urlEntry.longUrl)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(1)
        cacheService.delete(sequence)
        Assertions.assertThat(getAllKeysFromCache().size).isEqualTo(0)
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
        Assertions.assertThat(cacheService.get(sequence)).isEqualTo(persistedEntry?.longUrl)
    }

    private fun getAllKeysFromCache(): Set<ByteArray> =
        redisTemplate.connectionFactory!!.connection.keys("*".toByteArray()) as Set<ByteArray>

    private fun deserializeKey(key: ByteArray): String = redisTemplate.keySerializer.deserialize(key).toString()

}
