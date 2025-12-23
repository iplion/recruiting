package com.adl.recruiting.controller;

import com.adl.recruiting.dto.LoginRequest;
import com.adl.recruiting.dto.LoginResponse;
import com.adl.recruiting.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.login(), req.password())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generate(user);

        return new LoginResponse(token);
    }
}
