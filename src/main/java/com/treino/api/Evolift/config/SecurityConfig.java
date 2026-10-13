package com.treino.api.Evolift.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.POST, "/treinos").authenticated()
						.requestMatchers(HttpMethod.POST, "/exercicios", "/exercicios/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/exercicios", "/exercicios/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/exercicios", "/exercicios/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/treinos/**").hasRole("ADMIN")
						.anyRequest().permitAll())
				.httpBasic(Customizer.withDefaults())
				.build();
	}
}
