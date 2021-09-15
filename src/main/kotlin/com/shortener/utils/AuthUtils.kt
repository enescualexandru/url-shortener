package com.shortener.utils

import com.shortener.data.domain.UserPrincipal
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtils {

    fun getUserPrincipalFromAuth(): UserPrincipal? {
        if (getAuthentication().principal is UserPrincipal) {
            return getAuthentication().principal as UserPrincipal
        }

        return null
    }

    fun getAuthentication(): Authentication =  SecurityContextHolder.getContext().authentication
}
