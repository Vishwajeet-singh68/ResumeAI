package org.app.resumeaigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String SECRET = "kljaiohrenvjie5480reionrc94w08q034uv9340kjlo390wqioeru349qoj=";

    public Key getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    // ✅ Common method (single parsing point 🔥)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Validate token
    public boolean validateToken(String token) {
        try {
            getClaims(token); // parsing itself validates
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // ✅ Extract username (subject)
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Extract userId (custom claim)
    public String extractUserId(String token) {
        return getClaims(token).get("userId", String.class);
    }

    // ✅ Extract role (optional 🔥)
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }
}