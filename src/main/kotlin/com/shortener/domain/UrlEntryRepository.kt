package com.shortener.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlEntryRepository : JpaRepository<UrlEntry, Long> {
    fun findAllByOrderByIdDesc(): List<UrlEntry>
}