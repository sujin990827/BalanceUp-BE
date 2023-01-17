package com.balanceup.keum.config.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.repository.RedisRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

	@Value("${jwt.access-token-expired-time-ms}")
	private Long accessTokenExpiredTimeMs;

	@Value("${jwt.refresh-token-expired-time-ms}")
	private Long refreshTokenExpiredTimeMs;

	@Value("${jwt.secret-key}")
	private String key;


	private final RedisRepository redisRepository;


	public String getUserNameByToken(String token) {
		return extractClaims(token).get("username", String.class);
	}

	private boolean isExpired(String token) {
		Date expiredDate = extractClaims(token).getExpiration();
		return expiredDate.before(new Date());
	}

	private Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getKey())
			.build().parseClaimsJws(token).getBody();
	}

	public TokenDto generateToken(String username) {
		Claims claims = Jwts.claims();
		claims.put("username", username);
		String accessToken = getToken(claims, accessTokenExpiredTimeMs);
		String refreshToken = getToken(claims, refreshTokenExpiredTimeMs);
		redisRepository.setValues(username,refreshToken, Duration.ofMillis(refreshTokenExpiredTimeMs));
		return new TokenDto(accessToken, refreshToken);
	}

	private String getToken(Claims claims, Long tokenExpiredTimeMs) {
		Date now = new Date();
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenExpiredTimeMs))
			.signWith(getKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUserNameByToken(token);
		return (username.equals(userDetails.getUsername())) && !isExpired(token);
	}

	private Key getKey() {
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
