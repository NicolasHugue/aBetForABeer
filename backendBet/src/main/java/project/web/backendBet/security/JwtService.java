package project.web.backendBet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import project.web.backendBet.model.Role;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.exp-minutes:120}") long expMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expMinutes = expMinutes;
    }

    public String generateToken(String username, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("role", role.name()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public Role extractRole(String token) {
        String r = (String) parse(token).getBody().get("role");
        return Role.valueOf(r);
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            var claims = parse(token).getBody();
            return user.getUsername().equals(claims.getSubject()) &&
                    claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
}