package com.amadeus.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*  Сервис который работает с jwt токенами.
 *  Тут собраны все операции которые данный сервис будет делать
 *  с полученым токеном, а так создать генерить новый */

@Component
public class JwtCore {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private int lifetime;

    /*
     *
     *    Создания токена
     *
     * */


    // Создается обычный токен
    public String generateToken(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .signWith(getSighInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Можно создать и так. В этом случае у нас несколько Claims'ов
    public String generateJwtToken(Map<String, Object> extractClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .signWith(getSighInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Тестовый
    @Deprecated
    public String geneToken(UserDetails userDetails){
        return generateJwtToken(new HashMap<>(), userDetails);
    }

    /*
     *
     * Проверка токена
     *
     * */

    // Проверка токена на валидность
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Проверка токена если он просрочен. Можно выбать новый через RefreshToken или просто попросить
    // пользователя залогиниться заного
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /*
     *
     *  Взять что-то ИЗ токена
     *
     *
     * */

    // Взять параметр который отвечает за время жизни токена
    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    // Взять параметр отвечающий за имя
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    // Взять всё. Разбивает токен на атрибуты, чтобы получить доступ к ним
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSighInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Через "extractAllClaims(String token)" позволяет получить определенный аьрибут
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /*
     *
     *   Доп. операции
     *
     *
     * */

    // Позволяет взять и проверить подписть jwt
    private Key getSighInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Взять имя юзера
    public String getNameFromJwt(String token){
        return extractClaims(token, Claims::getSubject);
    }

    // Вернуть время жизни токена
    public int getLifeTime(){
        return this.lifetime;
    }
}

