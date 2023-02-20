package com.personel.auth.server.security.jwt;

import com.personel.auth.server.exceptions.InvalidCredentialsException;
import com.personel.auth.server.security.WebSecurityConfig;

import com.personel.auth.server.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Value("${serverApi.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    public String generateJwtToken(UserDetailsImpl userDetails) {
        return generateJwtToken(new HashMap<>(), userDetails);
    }
    public String generateJwtToken(Map<String,Object> extraClaims, UserDetails userDetails) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(webSecurityConfig.jwtSecretKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(webSecurityConfig.jwtSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean validateJwtToken(String authToken) {

        try {
            getClaim(authToken, Claims::getExpiration).before(new Date());
            Jwts.parser().setSigningKey(webSecurityConfig.jwtSecretKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
