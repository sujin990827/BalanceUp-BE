package com.balanceup.keum.config.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.balanceup.keum.config.auth.PrincipalDetailService;
import com.balanceup.keum.config.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final PrincipalDetailService principalDetailService;
	private final JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null || !header.startsWith("Bearer ")) {
			log.error("Error occurs while getting header, header is invalid");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String token = header.split(" ")[1].trim();
			String username = jwtTokenUtil.getUserName(token);
			UserDetails userDetails = principalDetailService.loadUserByUsername(username);

			if (jwtTokenUtil.validateToken(token, userDetails)) {
				//TODO : 만료를 어떻게 클라이언트에게 어떻게 알려줄껀지
				filterChain.doFilter(request, response);
				return;
			}

			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (RuntimeException e) {
			filterChain.doFilter(request, response);
			return;
		}
		filterChain.doFilter(request, response);
	}

}
