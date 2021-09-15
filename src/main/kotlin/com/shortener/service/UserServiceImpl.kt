package com.shortener.service

import com.shortener.data.domain.User
import com.shortener.data.repository.UserRepository
import com.shortener.dto.UserLoginRequest
import com.shortener.dto.UserLoginResponse
import com.shortener.dto.UserRegisterRequest
import com.shortener.exception.InvalidDataException
import com.shortener.security.jwt.JwtUtils
import com.shortener.utils.AuthUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils,
    private val authUtils: AuthUtils
) : UserService {

    @Transactional
    override fun registerUser(request: UserRegisterRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw InvalidDataException("Email already used")
        }

        userRepository.save(
            User(
                request.email,
                passwordEncoder.encode(request.password),
                request.name,
                request.role
            )
        )
    }

    override fun loginUser(request: UserLoginRequest): UserLoginResponse {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        return UserLoginResponse.fromTokenAndUserPrincipal(
            jwtUtils.generateJwtToken(authUtils.getAuthentication()),
            authUtils.getUserPrincipalFromAuth()!!
        )
    }
}
