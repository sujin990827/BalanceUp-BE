package com.balanceup.keum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.balanceup.keum.config.auth.PrincipalDetailService;
import com.balanceup.keum.config.filter.CustomAccessDeniedHandler;
import com.balanceup.keum.config.filter.CustomEntryPoint;
import com.balanceup.keum.config.filter.JwtFilter;
import com.balanceup.keum.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.balanceup.keum.config.oauth.PrincipalOauth2UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final PrincipalOauth2UserService principalOauth2UserService;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final PrincipalDetailService principalDetailService;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Value("$jwt.secret-key")
	private String key;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			 .authorizeRequests()
			 .antMatchers("/admin/**").authenticated()
			.anyRequest().permitAll()
			.and()
			// .sessionManagement()
			// .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			// .and()
			.logout()
			.logoutSuccessUrl("/")
			// .and()
			// .addFilterBefore(new JwtFilter(key, principalDetailService), UsernamePasswordAuthenticationFilter.class)
			// .exceptionHandling()
			// .authenticationEntryPoint(new CustomEntryPoint())
			// .accessDeniedHandler(customAccessDeniedHandler)
			// .and()
			// .oauth2Login()
			// .userInfoEndpoint()
			// .userService(principalOauth2UserService)
/*			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler)*/

		;
	}

}
