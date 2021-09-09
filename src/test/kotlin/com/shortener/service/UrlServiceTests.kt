package com.shortener.service

import com.shortener.*
import com.shortener.data.repository.UrlEntryRepository
import com.shortener.dto.UrlShortenRequest
import com.shortener.exception.InvalidInputUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class UrlServiceTests(
    @Autowired val urlEntryRepository: UrlEntryRepository,
    @Autowired val urlService: UrlService
) {

    @Test
    fun `Valid url is converted to a shorter one`() {
        val request = UrlShortenRequest(GOOD_URL)
        val response = urlService.shortenUrl(request)
        assertThat(response).isNotNull
        assertThat(response.shortenedUrl).isNotNull
        assertThat(response.shortenedUrl).isNotEqualTo(GOOD_URL)
    }

    @Test
    fun `Two requests for shortening the same url does not produce identical shortened urls`() {
        val request1 = UrlShortenRequest(GOOD_URL)
        val response1 = urlService.shortenUrl(request1)

        val request2 = UrlShortenRequest(GOOD_URL)
        val response2 = urlService.shortenUrl(request2)

        assertThat(response1.shortenedUrl).isNotEqualTo(response2.shortenedUrl)
    }

    @Test
    fun `Input url with no schema can still be converted`() {
        val request = UrlShortenRequest(GOOD_URL_NO_SCHEMA)
        val response = urlService.shortenUrl(request)
        assertThat(response).isNotNull
        assertThat(response.shortenedUrl).isNotNull
        assertThat(response.shortenedUrl).isNotEqualTo(GOOD_URL_NO_SCHEMA)
    }

    @Test
    fun `Invalid url cannot be converted, and exception is thrown`() {
        val request = UrlShortenRequest(BAD_URL)
        val exception = assertThrows<InvalidInputUrl> { urlService.shortenUrl(request) }

        assertInvalidInputUrlMessage(exception)
    }

    @Test
    fun `Invalid url, lacking schema, cannot be converted, and exception is thrown`() {
        val request = UrlShortenRequest(BAD_URL_NO_SCHEMA)
        val exception = assertThrows<InvalidInputUrl> { urlService.shortenUrl(request) }
        assertInvalidInputUrlMessage(exception)
    }

    @Test
    fun `A shortened url can be reversed to the initial input url`() {
        val encodedSequence = "a1b2"
        val entry = TestUtils.createUrlEntry(GOOD_URL, encodedSequence, false)
        urlEntryRepository.save(entry)

        val response = urlService.decodeSequence(encodedSequence)
        assertThat(response).isNotNull
        assertThat(response).isEqualTo(GOOD_URL)
    }

    @Test
    fun `A shortened url which has expired cannot be converted, and exception is thrown`() {
        val encodedSequence = "a1b2"
        val entry = TestUtils.createUrlEntry(GOOD_URL, encodedSequence, true)
        urlEntryRepository.save(entry)

        val exception = assertThrows<InvalidInputUrl> { urlService.decodeSequence(encodedSequence) }
        assertInvalidShortenedInputUrlMessage(exception)
    }

    @Test
    fun `All the shortened entries(history) can be returned`() {
        val entry1 = TestUtils.createUrlEntry(GOOD_URL, "a1b2", false)
        val entry2 = TestUtils.createUrlEntry(GOOD_URL_NO_SCHEMA, "a1b3", false)
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

}
