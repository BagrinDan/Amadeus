package com.amadeus.backend.controllers.Auth;


import com.amadeus.backend.dto.response.SignUpRequest;
import com.amadeus.backend.dto.response.SignUpResponse;
import com.amadeus.backend.services.interfaces.SignUpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class SignUpController {
    private final SignUpService service;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request){
        SignUpResponse response = new SignUpResponse();

        response.setMessage(service.registerUser(request));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
