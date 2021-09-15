package com.shortener.controller

import com.shortener.dto.UrlEntryHistoryResponse
import com.shortener.dto.UrlShortenRequest
import com.shortener.dto.UrlShortenResponse
import com.shortener.service.UrlService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

@RestController
class UrlController(private val urlService: UrlService) {

    @PostMapping
    fun encodeUrl(@RequestBody @Valid urlShortenRequest: UrlShortenRequest): ResponseEntity<UrlShortenResponse> {
       return ResponseEntity.status(HttpStatus.OK).body(urlService.shortenUrl(urlShortenRequest))
    }

    @GetMapping("/e/{encodedSequence}")
    fun decodeSequence(@PathVariable encodedSequence: String): ResponseEntity<Void?> {
        val decodedUrl = urlService.decodeSequence(encodedSequence)
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(decodedUrl))
            .build()
    }

    @GetMapping("/history")
    fun getHistory(): ResponseEntity<List<UrlEntryHistoryResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(urlService.getUrlHistoryForCurrentUser())
    }
}
