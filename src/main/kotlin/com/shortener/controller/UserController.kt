package com.shortener.controller

import com.shortener.dto.UserLoginRequest
import com.shortener.dto.UserLoginResponse
import com.shortener.dto.UserRegisterRequest
import com.shortener.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid userRegisterRequest: UserRegisterRequest): ResponseEntity<*> {
        userService.registerUser(userRegisterRequest)
        return ResponseEntity.ok().build<Any>()
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody @Valid userLoginRequest: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(userService.loginUser(userLoginRequest))
    }

}
