package com.amadeus.backend.services.interfaces;

import com.amadeus.backend.dto.request.SignUpRequest;

public interface SignUpService {
    String registerUser(SignUpRequest request);
}
