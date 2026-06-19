package com.amadeus.backend.controllers;

import com.amadeus.backend.dto.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthResponse> handleBadCredentials(BadCredentialsException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Error: Login or Password are incorrect"));
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<AuthResponse> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new AuthResponse(e.getMessage()));
    }
}

