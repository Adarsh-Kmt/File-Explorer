package kamathadarsh.FileExplorer.security.utils;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import kamathadarsh.FileExplorer.security.DTO.CustomUserDetails;
import kamathadarsh.FileExplorer.security.utils.KeyUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
@AllArgsConstructor
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);

    private final KeyUtils keyUtils;

    public String generateJwtToken(Authentication authentication) throws Exception {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        PrivateKey privateKey = keyUtils.loadPrivateKey();


        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                //.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(privateKey)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) throws Exception {

        PublicKey publicKey = keyUtils.loadPublicKey();
        return Jwts.parserBuilder().setSigningKey(publicKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            PublicKey publicKey = keyUtils.loadPublicKey();

            Jwts.parserBuilder().setSigningKey(publicKey).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("error in getting public key: {}", e.getMessage());
        }

        return false;
    }
}