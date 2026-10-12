package com.doctor_patient.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${app.cors.allowed-origins:http://localhost:4200}")
	private String[] allowedOrigins;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
			throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint((request, response, authException) ->
								response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/api/v1/login/**", "/api/v1/states/**", "/api/v1/cities/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/patient/register").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/attachment/saveAttachment").permitAll()
						.requestMatchers("/api/v1/staff/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/doctors/options").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PATIENT")
						.requestMatchers("/api/v1/doctors/by-user/**").hasAnyRole("ADMIN", "DOCTOR")
						.requestMatchers("/api/v1/doctors/**", "/api/v1/nurses/**")
						.hasAnyRole("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST")
						.requestMatchers("/api/v1/patient/allPatient").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/patient/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/patient/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/v1/patient/by-mobile/**")
						.hasAnyRole("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PATIENT")
						.requestMatchers(HttpMethod.GET, "/api/v1/patient/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST")
						.requestMatchers(HttpMethod.POST, "/api/v1/slot/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/slot/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/v1/slot/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/v1/appointment/*/consultation").hasAnyRole("ADMIN", "DOCTOR")
						.requestMatchers(HttpMethod.POST, "/api/v1/appointment/save")
						.hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/appointment/**").hasAnyRole("ADMIN", "RECEPTIONIST")
						.requestMatchers("/api/v1/appointment/allAppointment", "/api/v1/appointment/dashboard")
						.hasAnyRole("ADMIN", "NURSE", "RECEPTIONIST")
						.requestMatchers("/api/v1/appointment/dashboard/doctor/**", "/api/v1/appointment/doctor/**")
						.hasAnyRole("ADMIN", "DOCTOR")
						.requestMatchers("/api/v1/appointment/dashboard/patient/**", "/api/v1/appointment/patient/**")
						.hasAnyRole("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PATIENT")
						.requestMatchers("/api/v1/appointment/**").authenticated()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(allowedOrigins));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setExposedHeaders(List.of("Authorization"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		return source;
	}
}
