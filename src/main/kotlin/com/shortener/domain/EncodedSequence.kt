package com.shortener.domain

import org.hibernate.annotations.NaturalId
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

@Table(name = "ENCODED_SEQUENCE_ENTRIES")
@Entity
class EncodedSequence : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long? = null

    @NaturalId
    @Column(name = "sequence", unique = true)
    @Size(max = 15)
    var sequence: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncodedSequence

        if (id != other.id) return false
        if (sequence != other.sequence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + sequence.hashCode()
        return result
    }

}
