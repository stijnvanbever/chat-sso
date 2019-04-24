package be.spider.chat.server.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
    private static final SecretKey SECRETKEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public String createToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .signWith(SECRETKEY)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(SECRETKEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
