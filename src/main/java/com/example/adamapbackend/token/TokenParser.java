package com.example.adamapbackend.token;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenParser {

    @Value("${secret.key.jwt}")
    private String SECRET_KEY;

    public String extractEmail(String token) {
        // Extrae el correo electrónico del token sin realizar ninguna validación adicional
        return extractClaim(token, "email", String.class);
    }

    private <T> T extractClaim(String token, String claimName, Class<T> requiredType) {
        // Utiliza la clave secreta para parsear y desencriptar el JWT y luego extrae el claim específico
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().get(claimName, requiredType);
    }
}
