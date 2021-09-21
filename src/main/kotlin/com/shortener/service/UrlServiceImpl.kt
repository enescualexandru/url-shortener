package com.shortener.service

import com.shortener.data.cache.CacheService
import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry
import com.shortener.data.repository.UrlEntryRepository
import com.shortener.dto.UrlShortenRequest
import com.shortener.dto.UrlShortenResponse
import com.shortener.exception.InvalidInputUrl
import com.shortener.utils.IdEncoder
import com.shortener.utils.UrlValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.LocalDateTime
import java.util.*

private const val HTTP_SCHEMA = "http"
private const val DEFAULT_HTTPS_SCHEMA = "https"
private const val SCHEMA_SEPARATOR = "://"
private const val URL_EXPIRES_DAYS_NO_USER = 7L
private val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()

@Service
class UrlServiceImpl(
    private val urlEntryRepository: UrlEntryRepository,
    private val idEncoder: IdEncoder,
    private val urlValidator: UrlValidator,
    private val cacheService: CacheService<String, UrlEntry>
) : UrlService {

    @Value("\${spring.cache.type}")
    private var cacheType: String = "none"

    @Transactional(rollbackFor = [Exception::class])
    override fun shortenUrl(request: UrlShortenRequest): UrlShortenResponse {
        val fixedRequestUrl = addDefaultSchemaIfMissing(request.longUrl)

        if (!urlValidator.isValidUrl(fixedRequestUrl)) {
            throw InvalidInputUrl("The URL you provided is invalid")
        }

        val urlEntry = UrlEntry(fixedRequestUrl).apply {
            expiresAt = createdAt.plusHours(getNoUserUrlExpiresDays())
        }

        val persistedUrlEntry = urlEntryRepository.save(urlEntry)
        val encodedId = idEncoder.encode(persistedUrlEntry.id!!)
        persistedUrlEntry.encodedSequence = EncodedSequence(encodedId)
        urlEntryRepository.save(persistedUrlEntry)
        if (isCachingEnabled()) {
            cacheService.delete(encodedId)
        }

        val url = addBaseUrlToEncodedSequence(encodedId)
        return UrlShortenResponse(url)
    }

    override fun decodeSequence(encodedSequence: String): String {
        var urlEntry:UrlEntry? = null

        if (isCachingEnabled()) {
            urlEntry = cacheService.get(encodedSequence)
        }

        if (urlEntry == null) {
            urlEntry = urlEntryRepository.findByEncodedSequence(EncodedSequence(encodedSequence)) ?: throwInvalidInputUrl()
            if (isCachingEnabled()) {
                cacheService.save(encodedSequence, urlEntry)
            }
        }

        if (isUrlEntryExpired(urlEntry)) {
            throwInvalidInputUrl()
        }

        return urlEntry.longUrl
    }

    override fun getAllShortenedUrls(): List<UrlShortenResponse> = urlEntryRepository.findAllByOrderByIdDesc().map {
        val encodedId = idEncoder.encode(it.id!!)
        val shortenedUrl = addBaseUrlToEncodedSequence(encodedId)
        UrlShortenResponse(shortenedUrl)
    }

    private fun getNoUserUrlExpiresDays(): Long = URL_EXPIRES_DAYS_NO_USER

    private fun throwInvalidInputUrl(): Nothing = throw InvalidInputUrl("The shortened URL is not valid")

    private fun isUrlEntryExpired(urlEntry: UrlEntry): Boolean = urlEntry.expiresAt.isBefore(LocalDateTime.now())

    private fun addDefaultSchemaIfMissing(requestUrl: String): String {
        if (requestUrl.lowercase(Locale.getDefault()).startsWith(HTTP_SCHEMA) ||
            requestUrl.lowercase(Locale.getDefault()).startsWith(DEFAULT_HTTPS_SCHEMA)
        ) {
            return requestUrl
        }

        return HTTP_SCHEMA + SCHEMA_SEPARATOR + requestUrl
    }

    private fun addBaseUrlToEncodedSequence(sequence: String): String = "$baseUrl/e/$sequence"

    fun isCachingEnabled() = cacheType != "none"

}
