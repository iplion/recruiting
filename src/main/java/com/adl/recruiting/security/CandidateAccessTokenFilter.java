package com.adl.recruiting.security;

import com.adl.recruiting.repository.CandidateRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CandidateAccessTokenFilter extends OncePerRequestFilter {

    private final CandidateRepository candidateRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        var candidateOpt = candidateRepository.findByAccessToken(token);
        if (candidateOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        var candidate = candidateOpt.get();
        if (candidate.getTokenExpiresAt() != null &&
            candidate.getTokenExpiresAt().isBefore(OffsetDateTime.now())) {
            filterChain.doFilter(request, response);
            return;
        }

        // principal кладём candidateId — удобно в контроллерах
        var auth = new UsernamePasswordAuthenticationToken(
            candidate.getId(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // вариант 1: Authorization: Bearer <candidateAccessToken>
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7).trim();
        }

        // вариант 2: X-Candidate-Token: <candidateAccessToken> (предпочтительнее)
        String x = request.getHeader("X-Candidate-Token");
        if (x != null && !x.isBlank()) {
            return x.trim();
        }

        // вариант 3: query ?token=...
        return request.getParameter("token");
    }
}
