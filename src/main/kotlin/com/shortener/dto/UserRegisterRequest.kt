package com.shortener.dto

import com.shortener.data.domain.UserRole
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRegisterRequest(
    @NotBlank
    val name: String,
    @Email
    @NotBlank
    val email: String,
    @NotBlank
    val password: String
) {

    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.ROLE_USER
}
