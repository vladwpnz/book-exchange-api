package com.friends.sharing.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    String[] allAuthorities = Arrays.stream(Authorities.values()).map(Enum::name).toArray(String[]::new);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/held").hasAnyAuthority(allAuthorities)
                        .requestMatchers(HttpMethod.GET, "/owned").hasAnyAuthority(allAuthorities)
                        .requestMatchers(HttpMethod.GET, "/items")
                                .hasAuthority(Authorities.ADMIN.toString())
                        .requestMatchers(HttpMethod.DELETE, "/book/delete")
                                .hasAuthority(Authorities.ADMIN.toString())
                        .requestMatchers(HttpMethod.POST, "/book/return/force")
                        .hasAuthority(Authorities.ADMIN.toString())
                        /*.requestMatchers(HttpMethod.DELETE, "/present/delete")
                                .hasAuthority(Authorities.ADMIN.toString())*/
                        .requestMatchers(HttpMethod.POST, "/book/**").hasAnyAuthority(allAuthorities)
                        //.requestMatchers(HttpMethod.POST, "/present/**").hasAnyAuthority(allAuthorities)
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().denyAll()
                )
                .httpBasic(Customizer.withDefaults())     //to send basic auth in http
                .formLogin(Customizer.withDefaults())    //for default login form
                .csrf(AbstractHttpConfigurer::disable); // for POST requests via Postman;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
