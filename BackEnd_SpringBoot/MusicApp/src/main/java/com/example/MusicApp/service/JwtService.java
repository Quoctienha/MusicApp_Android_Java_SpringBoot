package com.example.MusicApp.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Access Token: 5 phút
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60;

    // Refresh Token: 7 ngày60 *24 * 7
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 2;

    public String generateAccessToken(String username) {
        return buildToken(username, ACCESS_TOKEN_EXPIRATION, null);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, REFRESH_TOKEN_EXPIRATION, "refresh");
    }

    private String buildToken(String username, long expirationMillis, String tokenType) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SECRET_KEY);

        if (tokenType != null) {
            builder.claim("tokenType", tokenType);
        }

        return builder.compact();
    }


    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractUsernameIgnoreExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().getSubject(); // Token hết hạn vẫn lấy được username
        } catch (JwtException e) {
            return null; // Token không hợp lệ
        }
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, false);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, true);
    }

    private boolean validateToken(String token, boolean shouldBeRefreshToken) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("tokenType", String.class);

            if (shouldBeRefreshToken) {
                return "refresh".equals(tokenType);
            } else {
                return tokenType == null; // Access token không có tokenType hoặc tokenType != "refresh"
            }
        } catch (ExpiredJwtException e) {
            // Lỗi khi token hết hạn
            return false;
        } catch (JwtException e) {
            // Lỗi khác (token không hợp lệ)
            return false;
        }
    }

    // Kiểm tra xem tên người dùng trong token có khớp với người dùng
    // đã tải từ UserDetailsService hay không
    public boolean validateTokenForUser(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && validateAccessToken(token));
    }

}
