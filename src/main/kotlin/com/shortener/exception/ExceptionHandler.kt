package com.shortener.exception

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.http.HttpServletRequest

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class, InvalidDataException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(req: HttpServletRequest, ex: Exception): ErrorMessage {
       return ErrorMessage(ex.message)
    }
}
