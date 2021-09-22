package com.shortener

import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry
import java.time.LocalDateTime

const val GOOD_URL = "http://www.google.com"
const val GOOD_URL_NO_SCHEMA = "google.com"
const val BAD_URL = "http://$#%oole.com"
const val BAD_URL_NO_SCHEMA = "$#%oole.com"
const val DEFAULT_SEQUENCE = "a1b2"
const val DEFAULT_EXPIRED = false

object TestUtils {

    fun createUrlEntry(
        longUrl: String = GOOD_URL,
        encodedSequenceStr: String = DEFAULT_SEQUENCE,
        expired: Boolean = DEFAULT_EXPIRED
    ): UrlEntry =
        UrlEntry(longUrl).apply {
            encodedSequence = EncodedSequence(encodedSequenceStr)
            createdAt = LocalDateTime.now().minusDays(3)
            expiresAt = (if (expired) LocalDateTime.now().minusDays(1) else LocalDateTime.now().plusDays(4))
        }

}
