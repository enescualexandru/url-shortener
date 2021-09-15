package com.shortener.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserLoginRequest(
    @Email
    @NotBlank
    val email: String,
    @NotBlank
    val password: String
)
