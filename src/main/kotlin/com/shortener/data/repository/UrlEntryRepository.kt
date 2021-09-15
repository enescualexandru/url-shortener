package com.shortener.data.repository

import com.shortener.data.domain.UrlEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlEntryRepository : JpaRepository<UrlEntry, Long>, UrlEntryRepositoryBase {
    fun findAllByUserIdOrderByIdDesc(id: Long): List<UrlEntry>
}
