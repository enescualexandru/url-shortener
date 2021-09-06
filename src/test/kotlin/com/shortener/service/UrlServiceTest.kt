package com.shortener.service

import com.shortener.domain.EncodedSequence
import com.shortener.domain.UrlEntry
import com.shortener.domain.UrlEntryRepository
import com.shortener.dto.UrlShortenRequest
import com.shortener.exception.InvalidInputUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class UrlServiceTest(
    @Autowired val urlEntryRepository: UrlEntryRepository,
    @Autowired val urlService: UrlService
) {
    val goodUrl = "http://www.google.com"
    val goodUrlNoSchema = "google.com"
    val badUrl = "http://$#%oole.com"
    val badUrlNoSchema = "$#%oole.com"

    @Test
    fun `Valid url is converted to a shorter one`() {
        val request = UrlShortenRequest(goodUrl)
        val response = urlService.shortenUrl(request)
        assertThat(response).isNotNull
        assertThat(response.shortenedUrl).isNotNull
        assertThat(response.shortenedUrl).isNotEqualTo(goodUrl)
    }

    @Test
    fun `Two requests for shortening the same url does not produce identical shortened urls`() {
        val request1 = UrlShortenRequest(goodUrl)
        val response1 = urlService.shortenUrl(request1)

        val request2 = UrlShortenRequest(goodUrl)
        val response2 = urlService.shortenUrl(request2)

        assertThat(response1.shortenedUrl).isNotEqualTo(response2.shortenedUrl)
    }

    @Test
    fun `Input url with no schema can still be converted`() {
        val request = UrlShortenRequest(goodUrlNoSchema)
        val response = urlService.shortenUrl(request)
        assertThat(response).isNotNull
        assertThat(response.shortenedUrl).isNotNull
        assertThat(response.shortenedUrl).isNotEqualTo(goodUrlNoSchema)
    }

    @Test
    fun `Invalid url cannot be converted, and exception is thrown`() {
        val request = UrlShortenRequest(badUrl)
        val exception = assertThrows<InvalidInputUrl> { urlService.shortenUrl(request) }

        assertInvalidInputUrlMessage(exception)
    }

    @Test
    fun `Invalid url, lacking schema, cannot be converted, and exception is thrown`() {
        val request = UrlShortenRequest(badUrlNoSchema)
        val exception = assertThrows<InvalidInputUrl> { urlService.shortenUrl(request) }
        assertInvalidInputUrlMessage(exception)
    }

    @Test
    fun `A shortened url can be reversed to the initial input url`() {
        val encodedSequence = "a1b2"
        val entry = createUrlEntry(goodUrl, encodedSequence, false)
        urlEntryRepository.save(entry)

        val response = urlService.decodeSequence(encodedSequence)
        assertThat(response).isNotNull
        assertThat(response).isEqualTo(goodUrl)
    }

    @Test
    fun `A shortened url which has expired cannot be converted, and exception is thrown`() {
        val encodedSequence = "a1b2"
        val entry = createUrlEntry(goodUrl, encodedSequence, true)
        urlEntryRepository.save(entry)

        val exception = assertThrows<InvalidInputUrl> { urlService.decodeSequence(encodedSequence) }
        assertInvalidShortenedInputUrlMessage(exception)
    }

    @Test
    fun `All the shortened entries(history) can be returned`() {
        val entry1 = createUrlEntry(goodUrl, "a1b2", false)
        val entry2 = createUrlEntry(goodUrlNoSchema, "a1b3", false)
        urlEntryRepository.saveAll(listOf(entry1, entry2))
        val response = urlService.getAllShortenedUrls()
        assertThat(response).isNotNull
        assertThat(response.size).isGreaterThan(1)
    }

    private fun assertInvalidInputUrlMessage(exception: Exception) =
        assertExceptionContainsMessage(exception, "The URL you provided is invalid")

    private fun assertInvalidShortenedInputUrlMessage(exception: Exception) =
        assertExceptionContainsMessage(exception, "The shortened URL is not valid")

    private fun assertExceptionContainsMessage(exception: Exception, expectedMessage: String) {
        val actualMessage = exception.message
        assertThat(actualMessage).contains(expectedMessage)
    }

    private fun createUrlEntry(longUrl: String, encodedSequenceStr: String, expired: Boolean): UrlEntry =
        UrlEntry(longUrl).apply {
            encodedSequence = EncodedSequence().apply { sequence = encodedSequenceStr }
            createdAt = LocalDateTime.now().minusDays(3)
            expiresAt = (if (expired) LocalDateTime.now().minusDays(1) else LocalDateTime.now().plusDays(4))
        }

}
