package com.shortener.domain

import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Table(name = "URL_ENTRIES")
@Entity
class UrlEntry(
    @Column(name = "long_url", length = 2048)
    @Size(max = 2048)
    var longUrl: String,
): Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "sequence", referencedColumnName = "sequence")
    var encodedSequence: EncodedSequence? = null

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "expires_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    lateinit var expiresAt: LocalDateTime

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UrlEntry

        if (id != other.id) return false
        if (encodedSequence != other.encodedSequence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (encodedSequence?.hashCode() ?: 0)
        return result
    }
}
