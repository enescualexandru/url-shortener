package com.shortener.utils

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component

@Component
class UrlValidator {
    private val urlValidator = UrlValidator(UrlValidator.ALLOW_LOCAL_URLS)

    fun isValidUrl(url: String): Boolean {
        return urlValidator.isValid(url)
    }
}
