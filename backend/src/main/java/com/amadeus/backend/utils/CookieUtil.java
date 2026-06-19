package com.amadeus.backend.utils;

import com.amadeus.backend.security.jwt.JwtCore;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CookieUtil {

    private final JwtCore jwtCore;

    public Cookie createJwtCookie(String token){
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtCore.getLifeTime());

        return cookie;
    }

    public Cookie deleteJwtCookie(){
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return cookie;
    }
}
