package com.adl.recruiting.service;

import com.adl.recruiting.dto.CreateUserRequestDto;
import com.adl.recruiting.dto.UserResponseDto;
import com.adl.recruiting.entity.Role;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.exception.NotFoundException;
import com.adl.recruiting.repository.RoleRepository;
import com.adl.recruiting.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto create(CreateUserRequestDto req) {
        String login = req.login().trim();

        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalStateException("Login already exists: " + login); // 409
        }

        String roleName = req.role().trim().toLowerCase(Locale.ROOT);
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + req.role())); // 400

        User u = new User();
        u.setFullName(req.fullName());
        u.setLogin(login);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(role);

        u = userRepository.save(u);
        return toResponse(u);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getById(long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id)); // 404
        return toResponse(u);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> list() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    private UserResponseDto toResponse(User u) {
        return new UserResponseDto(u.getId(), u.getFullName(), u.getLogin(), u.getRole().getName());
    }
}
