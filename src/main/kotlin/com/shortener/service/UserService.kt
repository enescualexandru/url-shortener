package com.shortener.service

import com.shortener.dto.UserLoginRequest
import com.shortener.dto.UserLoginResponse
import com.shortener.dto.UserRegisterRequest

interface UserService {
    fun registerUser(request: UserRegisterRequest)
    fun loginUser(request: UserLoginRequest): UserLoginResponse
}
