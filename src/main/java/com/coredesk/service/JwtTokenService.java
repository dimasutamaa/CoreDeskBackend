package com.coredesk.service;

import com.coredesk.config.AppConfig;
import com.coredesk.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final AppConfig appConfig;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles", user.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + appConfig.getJwtTokenExpiration()))
                .signWith(getSigninKey())
                .compact();
    }

    public boolean isValidAccessToken(String token, String username) {
        String tokenEmail = getEmailFromToken(token);
        return (username.equals(tokenEmail) && !isTokenExpired(token));
    }

    public String getEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(appConfig.getJwtSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
