package com.shortener.data.domain

import org.hibernate.annotations.NaturalId
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

@Table(name = "ENCODED_SEQUENCE_ENTRIES")
@Entity
class EncodedSequence(
    @NaturalId
    @Column(name = "sequence", length = 15, unique = true)
    @Size(max = 15)
    var sequence: String
) : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncodedSequence

        if (sequence != other.sequence) return false

        return true
    }

    override fun hashCode(): Int {
        return sequence.hashCode()
    }

}
