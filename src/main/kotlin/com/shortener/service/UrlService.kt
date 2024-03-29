package com.shortener.service

import com.shortener.dto.UrlShortenRequest
import com.shortener.dto.UrlShortenResponse

interface UrlService {
    fun shortenUrl(request: UrlShortenRequest): UrlShortenResponse
    fun decodeSequence(encodedSequence: String): String
    fun getAllShortenedUrls(): List<UrlShortenResponse>
}
