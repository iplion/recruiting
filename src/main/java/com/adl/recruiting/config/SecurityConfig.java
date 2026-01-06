package com.adl.recruiting.config;

import com.adl.recruiting.repository.UserRepository;
import com.adl.recruiting.security.CandidateAccessTokenFilter;
import com.adl.recruiting.security.JwtService;
import com.adl.recruiting.security.StaffJwtAuthFilter;
import com.adl.recruiting.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByLogin(username)
            .map(u -> {
                String roleName = u.getRole().getName().toUpperCase();
                List<SimpleGrantedAuthority> auth =
                    List.of(new SimpleGrantedAuthority("ROLE_" + roleName));

                return (UserDetails) new org.springframework.security.core.userdetails.User(
                    u.getLogin(),
                    u.getPasswordHash(),
                    auth
                );
            })
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public StaffJwtAuthFilter staffJwtAuthFilter(JwtService jwtService, UserDetailsService uds) {
        return new StaffJwtAuthFilter(jwtService, uds);
    }

    @Bean
    public CandidateAccessTokenFilter candidateAccessTokenFilter(CandidateRepository candidateRepository) {
        return new CandidateAccessTokenFilter(candidateRepository);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminChain(HttpSecurity http, StaffJwtAuthFilter staffJwtAuthFilter) throws Exception {
        return http
            .securityMatcher("/api/v1/admin/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> res.sendError(401, "UNAUTHORIZED"))
                .accessDeniedHandler((req, res, ex) -> res.sendError(403, "FORBIDDEN"))
            )
            .addFilterBefore(staffJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain candidateChain(HttpSecurity http, CandidateAccessTokenFilter candidateAccessTokenFilter) throws Exception {
        return http
            .securityMatcher("/api/v1/candidate/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("CANDIDATE"))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> res.sendError(401, "UNAUTHORIZED"))
                .accessDeniedHandler((req, res, ex) -> res.sendError(403, "FORBIDDEN"))
            )
            .addFilterBefore(candidateAccessTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    @Order(99)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .anyRequest().permitAll()
            )
            .build();
    }
}
