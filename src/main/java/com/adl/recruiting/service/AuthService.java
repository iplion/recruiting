package com.adl.recruiting.service;

import com.adl.recruiting.dto.LoginRequestDto;
import com.adl.recruiting.dto.LoginResponseDto;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.exception.NotFoundException;
import com.adl.recruiting.repository.UserRepository;
import com.adl.recruiting.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.login(), req.password())
        );

        UserDetails principal = (UserDetails) auth.getPrincipal();
        String token = jwtService.generate(principal);

        User u = userRepository.findByLogin(principal.getUsername())
            .orElseThrow(() -> new NotFoundException("User not found: " + principal.getUsername()));

        return new LoginResponseDto(
            token,
            u.getFullName(),
            u.getRole().getName()
        );
    }
}
