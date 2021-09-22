package com.shortener.data.repository

import com.shortener.data.domain.EncodedSequence
import com.shortener.data.domain.UrlEntry

interface UrlEntryRepositoryBase {
    fun save(entry: UrlEntry): UrlEntry
    fun delete(entry: UrlEntry)
    fun findByEncodedSequence(encodedSequence: EncodedSequence): UrlEntry?
}
