package net.alex9849.cocktailpi.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.repository.OptionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final String secretKeyOptionsKey = "secretKey";
    private String secretKey;

    @Autowired
    private OptionsRepository optionsRepository;

    @Value("${alex9849.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private String getSecretKey() {
        if(secretKey == null) {
            secretKey = optionsRepository.getOption(secretKeyOptionsKey).orElse(null);
            if(secretKey == null) {
                secretKey = UUID.randomUUID().toString();
                secretKey += "-" + UUID.randomUUID().toString();
                optionsRepository.setOption(secretKeyOptionsKey, secretKey);
            }
        }
        return secretKey;
    }

    public String generateJwtToken(Authentication authentication, boolean remember) {
        User principal = (User) authentication.getPrincipal();
        Date expirationDate;
        if(remember) {
            long tenYears = 10L * 365 * 24 * 60 * 60 * 1000;
            expirationDate = new Date(new Date().getTime() + tenYears);
        } else {
            expirationDate = new Date((new Date()).getTime() + jwtExpirationMs);
        }

        return Jwts.builder()
                .setSubject(String.valueOf(principal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .claim("remember", remember)
                .signWith(Keys.hmacShaKeyFor(getSecretKey().toString().getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    public long getUserIdFromJwtToken(String token) {
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(getSecretKey().toString().getBytes()).build().parseClaimsJws(token).getBody().getSubject());
    }

    public Date getExpirationDateFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey().toString().getBytes()).build().parseClaimsJws(token).getBody().getExpiration();
    }

    public boolean isRemember(String token) {
        return (boolean) Jwts.parserBuilder().setSigningKey(getSecretKey().toString().getBytes()).build().parseClaimsJws(token).getBody().get("remember");
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey().toString().getBytes()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("Untrusted JWT token: {}", e.getMessage());
        }

        return false;
    }

    public String parseJwt(@Nullable String authHeaderContent) {
        if (StringUtils.hasText(authHeaderContent) && authHeaderContent.startsWith("Bearer ")) {
            return authHeaderContent.substring(7);
        }
        return null;
    }
}
