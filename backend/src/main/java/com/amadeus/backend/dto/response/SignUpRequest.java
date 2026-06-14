package com.amadeus.backend.dto.response;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
    private String confirmPassword;
}
