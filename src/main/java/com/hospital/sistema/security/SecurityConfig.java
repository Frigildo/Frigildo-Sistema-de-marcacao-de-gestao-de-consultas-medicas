package com.hospital.sistema.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthenticationSuccessHandler successHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> auth

				// Público
				.requestMatchers("/login", "/register", "/css/**", "/js/**", "/adminlte/**", "/images/**",
						"/webjars/**", "/files/**" // ← ficheiros
				).permitAll()

				// Só ADMINISTRADOR
				.requestMatchers("/users/**", "/administrators/**", "/doctors/**", "/specialties/**", "/agenda/**")
				.hasRole("ADMINISTRADOR")

				// ADMINISTRADOR e MEDICO
				.requestMatchers("/consultations/**", "/attendances/**", "/exams/**", "/counselings/**",
						"/presences/**")
				.hasAnyRole("ADMINISTRADOR", "MEDICO")

				// Só PACIENTE
				.requestMatchers("/patients/profile/**", "/patients/consultations/**", "/patients/history",
						"/available-slots/**")
				.hasRole("PACIENTE")

				// Só ADMINISTRADOR gere pacientes
				.requestMatchers("/patients/**").hasRole("ADMINISTRADOR")

				// Autenticado
				.requestMatchers("/dashboard", "/").authenticated()

				.anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").successHandler(successHandler)
						.failureUrl("/login?error=true").usernameParameter("email").passwordParameter("password")
						.permitAll())

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())

				.exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}