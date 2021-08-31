package com.shortener.service

import com.shortener.domain.UrlEntry
import com.shortener.domain.UrlEntryRepository
import com.shortener.dto.UrlShortenRequest
import com.shortener.dto.UrlShortenResponse
import com.shortener.exception.InvalidInputUrl
import com.shortener.utils.IdEncoder
import org.apache.commons.validator.routines.UrlValidator
import org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.LocalDateTime
import java.util.*

private const val HTTP_SCHEMA = "http"
private const val DEFAULT_HTTPS_SCHEMA = "https"
private const val SCHEMA_SEPARATOR = "://"
private const val URL_EXPIRES_DAYS_NO_USER = 7L

@Service
class UrlServiceImpl(private val urlEntryRepository: UrlEntryRepository, private val idEncoder: IdEncoder) :
    UrlService {

    override fun shortenUrl(request: UrlShortenRequest): UrlShortenResponse {
        val fixedRequestUrl = addDefaultSchemaIfMissing(request.longUrl)

        if (!isValidUrl(fixedRequestUrl)) {
            throw InvalidInputUrl("The URL you provided is invalid")
        }

        val urlEntry = UrlEntry()
        urlEntry.setLongUrl(fixedRequestUrl)
        urlEntry.setCreatedAt(LocalDateTime.now())
        urlEntry.setExpiresAt(urlEntry.getCreatedAt()?.plusHours(getNoUserUrlExpiresDays()))

        val persistedUrlEntryId = urlEntryRepository.save(urlEntry).getId()
        val encodedUrlEntryId = idEncoder.encode(persistedUrlEntryId!!)
        val url = addBaseUrlToEncodedSequence(encodedUrlEntryId)
        return UrlShortenResponse(url)
    }

    override fun decodeUrl(encodedSequence: String): String {
        val decodedEntriesId = idEncoder.decode(encodedSequence)
        if (decodedEntriesId.isEmpty()) {
            throwInvalidInputUrl()
        }

        val urlEntry = urlEntryRepository.getById(decodedEntriesId[0])
        if (!isShortenedUrlValid(urlEntry)) {
            throwInvalidInputUrl()
        }

        return urlEntry.getLongUrl().toString()
    }

    override fun getAllShortenedUrls(): List<UrlShortenResponse> {
        val allUrls = urlEntryRepository.findAllByOrderByIdDesc()
        val allShortenedUrls = arrayListOf<UrlShortenResponse>()
        for (u in allUrls) {
            val encodedId = idEncoder.encode(u.getId()!!)
            val shortenedUrl = addBaseUrlToEncodedSequence(encodedId)
            allShortenedUrls.add(UrlShortenResponse(shortenedUrl))
        }

        return allShortenedUrls
    }

    private fun getNoUserUrlExpiresDays(): Long {
        return URL_EXPIRES_DAYS_NO_USER
    }

    private fun throwInvalidInputUrl() {
        throw InvalidInputUrl("The shortened URL is not valid")
    }

    private fun isShortenedUrlValid(urlEntry: UrlEntry): Boolean {
        //TODO: find potential new scenarios when an url is invalid
        return urlEntry.getExpiresAt()!!.isAfter(LocalDateTime.now())
    }

    private fun addDefaultSchemaIfMissing(requestUrl: String): String {
        if (requestUrl.lowercase(Locale.getDefault()).startsWith(HTTP_SCHEMA) ||
            requestUrl.lowercase(Locale.getDefault()).startsWith(DEFAULT_HTTPS_SCHEMA)
        ) {
            return requestUrl
        }

        return HTTP_SCHEMA + SCHEMA_SEPARATOR + requestUrl
    }

    private fun addBaseUrlToEncodedSequence(sequence: String): String {
        val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        return "$baseUrl/e/$sequence"
    }

    private fun isValidUrl(requestUrl: String): Boolean {
        return UrlValidator(ALLOW_LOCAL_URLS).isValid(requestUrl)
    }

}
