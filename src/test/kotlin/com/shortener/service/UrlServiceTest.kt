package com.shortener.service

import com.shortener.domain.UrlEntry
import com.shortener.domain.UrlEntryRepository
import com.shortener.dto.UrlShortenRequest
import com.shortener.exception.InvalidInputUrl
import com.shortener.utils.IdEncoder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import java.time.LocalDateTime
import javax.transaction.Transactional


@ExtendWith(MockitoExtension::class)
@SpringBootTest
@Transactional
class UrlServiceTest(
    @Autowired val urlEntryRepository: UrlEntryRepository,
    @Autowired val urlService: UrlService
) {
    @SpyBean
    lateinit var idEncoder: IdEncoder

    val goodUrl = "http://www.google.com"
    val goodUrlNoSchema = "google.com"
    val badUrl = "http://$#%oole.com"
    val badUrlNoSchema = "$#%oole.com"

    /**
     * Test that a valid url is converted to a shorter one
     */
    @Test
    fun shortenUrlSuccessTest() {
        val request = UrlShortenRequest(goodUrl)
        val response = urlService.shortenUrl(request)
        assertThat(response).isNotNull
        assertThat(response.shortenedUrl).isNotNull
        assertThat(response.shortenedUrl).isNotEqualTo(goodUrl)
    }

    /**
     * Verifies that two requests for shortening the same url does not produce identical shortened urls
     */
    @Test
    fun shortenSameUrlSuccessTest() {
        val request1 = UrlShortenRequest(goodUrl)
        val response1 = urlService.shortenUrl(request1)

        val request2 = UrlShortenRequest(goodUrl)
        val response2 = urlService.shortenUrl(request2)

        assertThat(response1.shortenedUrl).isNotEqualTo(response2.shortenedUrl)
    }

    /**
     * Verifies that an input url with no schema can still be converted
     */
    @Test
    fun shortenUrlWithNoSchemaSuccessTest() {
        val request = UrlShortenRequest(goodUrlNoSchema)
        val response = urlService.shortenUrl(request)
        assertThat(response).isNotNull
        assertThat(response.shortenedUrl).isNotNull
        assertThat(response.shortenedUrl).isNotEqualTo(goodUrlNoSchema)
    }

    /**
     * Tests that an invalid url cannot be converted, and exception is thrown
     */
    @Test
    fun shortenUrlBadUrlFailedTest() {
        val request = UrlShortenRequest(badUrl)
        val exception = assertThrows<InvalidInputUrl> { urlService.shortenUrl(request) }

        assertInvalidInputUrlMessage(exception)
    }

    /**
     * Tests that an invalid url, lacking schema, cannot be converted, and exception is thrown
     */
    @Test
    fun shortenUrlBadUrlNoSchemaFailedTest() {
        val request = UrlShortenRequest(badUrlNoSchema)
        val exception = assertThrows<InvalidInputUrl> { urlService.shortenUrl(request) }
        assertInvalidInputUrlMessage(exception)
    }

    /**
     * Tests that a shortened url can be reversed to the initial input url
     */
    @Test
    fun decodeUrlSuccessTest() {
        val encodedSequence = "a1b2"
        var entry = createUrlEntry(goodUrl, false)
        entry = urlEntryRepository.save(entry)
        val decodedEntryId = entry.getId()

        Mockito.`when`(idEncoder.decode(encodedSequence)).thenReturn(longArrayOf(decodedEntryId!!))

        val response = urlService.decodeUrl(encodedSequence)
        assertThat(response).isNotNull
        assertThat(response).isEqualTo(goodUrl)
    }

    /**
     * Verifies that a nonexistent shortened url cannot be converted, and exception is thrown
     */
    @Test
    fun decodeUrlInvalidSequenceTest() {
        val encodedSequence = "a1b2"
        Mockito.`when`(idEncoder.decode(encodedSequence)).thenReturn(longArrayOf())

        val exception = assertThrows<InvalidInputUrl> { urlService.decodeUrl(encodedSequence) }
        assertInvalidShortenedInputUrlMessage(exception)
    }

    /**
     * Verifies that a shortened url which has expired cannot be converted, and exception is thrown
     */
    @Test
    fun decodeUrlExpiredUrlTest() {
        val encodedSequence = "a1b2"
        var entry = createUrlEntry(goodUrl, true)
        entry = urlEntryRepository.save(entry)
        val decodedEntryId = entry.getId()

        Mockito.`when`(idEncoder.decode(encodedSequence)).thenReturn(longArrayOf(decodedEntryId!!))

        val exception = assertThrows<InvalidInputUrl> { urlService.decodeUrl(encodedSequence) }
        assertInvalidShortenedInputUrlMessage(exception)
    }

    /**
     * Verifies that all the shortened entries(history) can be returned
     */
    @Test
    fun getAllShortenedUrlsTest() {
        val entry1 = createUrlEntry(goodUrl, false)
        val entry2 = createUrlEntry(goodUrlNoSchema, false)
        urlEntryRepository.saveAll(arrayListOf(entry1, entry2))
        val response = urlService.getAllShortenedUrls()
        assertThat(response).isNotNull
        assertThat(response.size).isGreaterThan(1)
    }

    private fun assertInvalidInputUrlMessage(exception: Exception) {
        assertExceptionContainsMessage(exception, "The URL you provided is invalid")
    }

    private fun assertInvalidShortenedInputUrlMessage(exception: Exception) {
        assertExceptionContainsMessage(exception, "The shortened URL is not valid")
    }

    private fun assertExceptionContainsMessage(exception: Exception, expectedMessage: String) {
        val actualMessage = exception.message
        assertThat(actualMessage).contains(expectedMessage)
    }

    private fun createUrlEntry(url: String, expired: Boolean): UrlEntry {
        val entry = UrlEntry()
        entry.setLongUrl(url)
        entry.setCreatedAt(LocalDateTime.now().minusDays(3))
        entry.setExpiresAt(if (expired) LocalDateTime.now().minusDays(1) else LocalDateTime.now().plusDays(4))

        return entry
    }

}