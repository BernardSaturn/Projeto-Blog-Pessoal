package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	// Chave secreta usada para assinar o JWT (Base64 codificada)
	public static final String SECRET = "94a92867d194e8c1e71c73db3e1dd15f6f71060da2b935f6aba9d2f806f14a75";

	// Método para obter a chave de assinatura a partir da chave secreta
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// Método para extrair todas as claims de um token JWT
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	// Método genérico para extrair uma claim específica de um token JWT
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// Método para extrair o nome de usuário (subject) de um token JWT
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Método para extrair a data de expiração de um token JWT
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// Método para verificar se um token JWT está expirado
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// Método para validar um token JWT em relação ao UserDetails fornecido
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Método privado para criar um token JWT com claims específicas e um nome de
	// usuário
	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token válido por 1 hora
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	// Método para gerar um token JWT para um determinado nome de usuário
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}
}
