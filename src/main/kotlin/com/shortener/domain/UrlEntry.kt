package com.shortener.domain

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity(name = "URL_ENTRIES")
class UrlEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private var id: Long? = null

    @Column(name = "long_url")
    @Size(max = 2048)
    @NotBlank
    private var longUrl: String? = null

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private var createdAt: LocalDateTime? = null

    @Column(name = "expires_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private var expiresAt: LocalDateTime? = null

    fun getId(): Long? {
        return id
    }

    fun getLongUrl(): String? {
        return longUrl
    }

    fun setLongUrl(newLongUrl: String?) {
        longUrl = newLongUrl
    }

    fun getCreatedAt(): LocalDateTime? {
        return createdAt
    }

    fun setCreatedAt(newCreatedAt: LocalDateTime?) {
        createdAt = newCreatedAt
    }

    fun getExpiresAt(): LocalDateTime? {
        return expiresAt
    }

    fun setExpiresAt(newExpiresAt: LocalDateTime?) {
        expiresAt = newExpiresAt
    }

}