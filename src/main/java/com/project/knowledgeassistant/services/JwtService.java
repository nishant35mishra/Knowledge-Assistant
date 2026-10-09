package com.project.knowledgeassistant.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;


    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }


    public String generateAccessToken(
            UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("type", "ACCESS")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessExpiration
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }


    public String generateRefreshToken(
            UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }


    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return resolver.apply(claims);
    }


    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    public String extractTokenType(String token) {

        return extractClaim(
                token,
                claims -> claims.get(
                        "type",
                        String.class
                )
        );
    }


    public boolean isTokenExpired(String token) {

        Date expiration =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return expiration.before(new Date());
    }


    public boolean isTokenValid(
            String token,
            UserDetails userDetails,
            String expectedType) {

        try {

            String username =
                    extractUsername(token);

            String type =
                    extractTokenType(token);

            return username.equals(
                    userDetails.getUsername()
            )
                    && expectedType.equals(type)
                    && !isTokenExpired(token);

        } catch (JwtException |
                 IllegalArgumentException e) {

            return false;
        }
    }
}
