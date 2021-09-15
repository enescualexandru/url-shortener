package com.shortener.service

import com.shortener.data.domain.*
import com.shortener.data.repository.UrlEntryRepositoryBase
import com.shortener.data.repository.UrlEntryRepository
import com.shortener.data.repository.UserRepository
import com.shortener.dto.UrlEntryHistoryResponse
import com.shortener.dto.UrlShortenRequest
import com.shortener.dto.UrlShortenResponse
import com.shortener.exception.InvalidDataException
import com.shortener.utils.AuthUtils
import com.shortener.utils.IdEncoder
import com.shortener.utils.UrlValidator
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
    private val authUtils: AuthUtils,
    private val userRepository: UserRepository,
    private val urlEntryRepositoryCacheImpl: UrlEntryRepositoryBase
) : UrlService {

    @Transactional(rollbackFor = [Exception::class])
    override fun shortenUrl(request: UrlShortenRequest): UrlShortenResponse {
        val fixedRequestUrl = addDefaultSchemaIfMissing(request.longUrl)

        if (!urlValidator.isValidUrl(fixedRequestUrl)) {
            throw InvalidDataException("The URL you provided is invalid")
        }

        val userId = authUtils.getUserPrincipalFromAuth()?.id
        val user = if (userId != null) userRepository.findById(userId).get() else null
        val urlEntry = UrlEntry(fixedRequestUrl, user).apply {
            expiresAt = createdAt.plusHours(getNoUserUrlExpiresDays())
        }

        val persistedUrlEntry = urlEntryRepositoryCacheImpl.save(urlEntry)
        val encodedId = idEncoder.encode(persistedUrlEntry.id!!)
        persistedUrlEntry.encodedSequence = EncodedSequence().apply { sequence = encodedId }
        urlEntryRepositoryCacheImpl.save(persistedUrlEntry)

        val url = addBaseUrlToEncodedSequence(encodedId)
        return UrlShortenResponse(url)
    }

    override fun decodeSequence(encodedSequence: String): String {
        val urlEntry =
            urlEntryRepositoryCacheImpl.findByEncodedSequence(EncodedSequence().apply { sequence = encodedSequence })
                ?: throwInvalidInputUrl()

        if (isUrlEntryExpired(urlEntry)) {
            throwInvalidInputUrl()
        }

        return urlEntry.longUrl
    }

    override fun getUrlHistoryForCurrentUser(): List<UrlEntryHistoryResponse> {
        val userId = authUtils.getUserPrincipalFromAuth()!!.id
        val history = if (userId != null) urlEntryRepository.findAllByUserIdOrderByIdDesc(userId) else null

        if (history!!.isEmpty()) {
            return listOf()
        }

        val historyList = history.map {
            UrlEntryHistoryResponse(
                it.id!!,
                addBaseUrlToEncodedSequence(it.encodedSequence!!.sequence),
                it.longUrl
            )
        }.toList()

        return historyList
    }

    private fun getNoUserUrlExpiresDays(): Long = URL_EXPIRES_DAYS_NO_USER

    private fun throwInvalidInputUrl(): Nothing = throw InvalidDataException("The shortened URL is not valid")

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

}
