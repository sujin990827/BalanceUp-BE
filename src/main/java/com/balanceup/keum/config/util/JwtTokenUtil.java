package com.balanceup.keum.config.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtTokenUtil {

	public static String getUserName(String token, String key) {
		return extractClaims(token, key).get("username", String.class);
	}

	private static boolean isExpired(String token, String key) {
		Date expiredDate = extractClaims(token, key).getExpiration();
		return expiredDate.before(new Date());
	}

	private static Claims extractClaims(String token, String key) {
		return Jwts.parserBuilder().setSigningKey(getKey(key))
			.build().parseClaimsJws(token).getBody();
	}

	public static String generateToken(String username, String key, long expiredTimeMs) {
		Claims claims = Jwts.claims();
		claims.put("username", username);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
			.signWith(getKey(key), SignatureAlgorithm.HS256)
			.compact();
	}

	public static boolean validateToken(String token, UserDetails userDetails, String key) {
		final String username = getUserName(token, key);
		return (username.equals(userDetails.getUsername())) && !isExpired(token, key);
	}

	private static Key getKey(String key) {
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
