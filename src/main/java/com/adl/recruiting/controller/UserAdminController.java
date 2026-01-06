package com.adl.recruiting.controller;

import com.adl.recruiting.dto.CreateUserRequestDto;
import com.adl.recruiting.dto.UserResponseDto;
import com.adl.recruiting.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('DIRECTOR')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody CreateUserRequestDto req) {
        return userService.create(req);
    }

    @GetMapping
    public List<UserResponseDto> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable long id) {
        return userService.getById(id);
    }
}
