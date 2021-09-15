package com.shortener.dto

import com.shortener.data.domain.UserPrincipal
import com.shortener.data.domain.UserRole
import org.springframework.security.core.GrantedAuthority

data class UserLoginResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    val token: String,
) {
    val tokenType: String = "Bearer"

    companion object {
        @JvmStatic
        fun fromTokenAndUserPrincipal(token: String, userPrincipal: UserPrincipal): UserLoginResponse {
            val role = userPrincipal.authorities.stream().findFirst().map { obj: GrantedAuthority -> obj.authority }
                .orElse(UserRole.ROLE_USER.name)

            return UserLoginResponse(
                userPrincipal.id!!,
                userPrincipal.username,
                userPrincipal.name,
                role,
                token
            )
        }
    }
}
