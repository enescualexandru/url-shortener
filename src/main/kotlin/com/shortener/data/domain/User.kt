package com.shortener.data.domain

import org.hibernate.annotations.NaturalId
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Table(
    name = "USERS", uniqueConstraints = [
        UniqueConstraint(columnNames = ["email"])
    ]
)
@Entity
class User(
    @Column(name = "email")
    @NaturalId
    @Email
    @NotBlank
    val email: String,
    @Column(name = "password")
    @NotBlank
    val password: String,
    @Column(name = "name")
    @NotBlank
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @NotNull
    val role: UserRole

) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long? = null
}
