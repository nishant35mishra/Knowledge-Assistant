package com.project.knowledgeassistant.utilities;

import com.project.knowledgeassistant.enums.UserRoles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

@Component
public class JWTUtility {

    @Value("${secret}")
    private String secretKey ;

    public SecretKey getKey () {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String username, String email , String role) {

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .signWith(getKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*2))
                .compact();

    }

    public String extractEmail  (String token) {

        return
        Jwts.parser()
                .verifyWith( getKey() )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();


    }

    public Date getExpiry  (String token) {
        return  Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getPayload()
                .getExpiration();
    }

    public boolean checkExpiry ( String token) {
        return  getExpiry  (token).before(new Date(System.currentTimeMillis()));
    }

    public boolean checkTokenValidation(String token , String email) {

        return !checkExpiry(token) && extractEmail(token).equals(email) ;

    }




}
