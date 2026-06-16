package com.amadeus.backend.controllers.Auth;


import com.amadeus.backend.dto.request.SignInRequest;
import com.amadeus.backend.dto.response.AuthResponse;
import com.amadeus.backend.services.interfaces.SignInService;
import com.amadeus.backend.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class SignInController {

    private final SignInService service;
    private final CookieUtil util;

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody SignInRequest request,
                                               HttpServletResponse response) {

        String token = service.authUser(request);

        response.addCookie(util.createJwtCookie(token));

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new AuthResponse("Lab member created"));
    }

    @GetMapping("/logOut")
    public ResponseEntity<String> logOut(HttpServletResponse response){
        response.addCookie(util.deleteJwtCookie());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Logout completed");
    }

}