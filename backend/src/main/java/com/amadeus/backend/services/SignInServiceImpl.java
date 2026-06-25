package com.amadeus.backend.services;

import com.amadeus.backend.dto.request.SignInRequest;
import com.amadeus.backend.security.jwt.JwtCore;
import com.amadeus.backend.services.interfaces.SignInService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SignInServiceImpl implements SignInService {
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;

    @Override
    public String authUser(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        return jwtCore.generateToken(authentication);
    }

}
