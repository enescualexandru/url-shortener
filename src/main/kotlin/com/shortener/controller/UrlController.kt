package com.shortener.controller

import com.shortener.dto.UrlShortenRequest
import com.shortener.service.UrlService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.net.URI


@Controller
class UrlController(private val urlService: UrlService) {

    @GetMapping
    fun getEncodeUrlPage(model: Model): String {
        model.addAttribute("urlShortenRequest", UrlShortenRequest())

        //TODO: remove it (displays the shortened url history)
        model["allEncodedUrls"] = urlService.getAllShortenedUrls()

        return "encode-url"
    }

    @PostMapping
    fun encodeUrl(urlShortenRequest: UrlShortenRequest, bindingResult: BindingResult, model: Model): String {
        if (bindingResult.hasErrors()) {
            return "encode-url"
        }
        model["urlShortenResponse"] = urlService.shortenUrl(urlShortenRequest)

        //TODO: remove it (displays the shortened url history)
        model["allEncodedUrls"] = urlService.getAllShortenedUrls()

        return "encode-url-result"
    }

    @GetMapping("/e/{encodedSequence}")
    fun decodeUrl(@PathVariable encodedSequence: String): ResponseEntity<Void?>? {
        val decodedUrl = urlService.decodeUrl(encodedSequence)
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(decodedUrl))
            .build()
    }

}