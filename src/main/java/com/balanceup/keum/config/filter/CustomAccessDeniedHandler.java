package com.balanceup.keum.config.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final UserRepository userRepository;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;");
		ResponseEntity<String> responseEntity = new ResponseEntity<>("error", HttpStatus.UNAUTHORIZED);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Principal principal = (Principal)authentication.getPrincipal();
		String username = principal.getName();
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("Username 이 존재하지 않습니다."));

		if (user.getNickname() == null) {
			response.setHeader("denied", "nickname이 존재하지 않는 유저입니다.");
		}

	}
}
