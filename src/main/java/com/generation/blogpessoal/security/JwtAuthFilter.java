package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	// Injeção de dependência do JwtService
	@Autowired
	private JwtService jwtService;

	// Injeção de dependência do UserDetailsServiceImpl
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Extrai o header Authorization do request
		
		String authHeader = request.getHeader("Authorization");
		
		String token = null;
		
		String username = null;

		try {
			// Verifica se o header Authorization está presente e começa com "Bearer "
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				// Extrai o token JWT do header
				token = authHeader.substring(7);
				// Extrai o username do token JWT
				username = jwtService.extractUsername(token);
			}

			// Verifica se o username foi extraído e se não há autenticação prévia no
			// contexto de segurança
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				// Carrega os detalhes do usuário com base no username extraído
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				// Valida o token JWT
				if (jwtService.validateToken(token, userDetails)) {
					// Cria um objeto de autenticação e define no contexto de segurança
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			// Continua o processamento do filtro
			filterChain.doFilter(request, response);

		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException
				| ResponseStatusException e) {
			// Em caso de exceção, define o status de resposta como FORBIDDEN (403)
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		}
	}
}
