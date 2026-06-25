package com.amadeus.backend.services.interfaces;

import com.amadeus.backend.dto.request.SignInRequest;

public interface SignInService {
    String authUser(SignInRequest request);
}
