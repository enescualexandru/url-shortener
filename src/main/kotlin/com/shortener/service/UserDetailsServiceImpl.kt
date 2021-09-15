package com.shortener.service

import com.shortener.data.domain.User
import com.shortener.data.domain.UserPrincipal
import com.shortener.data.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(email: String): UserPrincipal {
        val user: User = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User with the email: $email not found")
        return UserPrincipal.build(user)
    }

}
