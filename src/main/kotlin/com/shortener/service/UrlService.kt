package com.shortener.service

import com.shortener.dto.UrlEntryHistoryResponse
import com.shortener.dto.UrlShortenRequest
import com.shortener.dto.UrlShortenResponse

interface UrlService {
    fun shortenUrl(request: UrlShortenRequest): UrlShortenResponse
    fun decodeSequence(encodedSequence: String): String
    fun getUrlHistoryForCurrentUser(): List<UrlEntryHistoryResponse>
}
