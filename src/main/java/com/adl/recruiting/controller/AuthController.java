package com.adl.recruiting.controller;

import com.adl.recruiting.dto.LoginRequestDto;
import com.adl.recruiting.dto.LoginResponseDto;
import com.adl.recruiting.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto req) {
        return authService.login(req);
    }
}
