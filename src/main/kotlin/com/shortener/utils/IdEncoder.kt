package com.shortener.utils

import org.hashids.Hashids
import org.springframework.stereotype.Component

private const val SALT = "no salt when high blood pressure"
private const val MIN_HASH_LENGTH = 4

@Component
class IdEncoder(val hashids: Hashids = Hashids(SALT, MIN_HASH_LENGTH)) {

    fun encode(number: Long): String {
        return hashids.encode(number)
    }

    fun decode(sequence: String): LongArray {
        return hashids.decode(sequence)
    }
}
