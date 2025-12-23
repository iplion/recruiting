package com.adl.recruiting.service;

import com.adl.recruiting.dto.CreateUserRequest;
import com.adl.recruiting.dto.UserResponse;
import com.adl.recruiting.entity.Role;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.repository.RoleRepository;
import com.adl.recruiting.repository.UserRepository;
import java.util.List;
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
    public UserResponse create(CreateUserRequest req) {
        Role role = roleRepository.findByName(req.role())
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + req.role()));

        User u = new User();
        u.setFullName(req.fullName());
        u.setLogin(req.login());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(role);

        u = userRepository.save(u);
        return toResponse(u);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return toResponse(u);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getLogin(), u.getRole().getName());
    }
}
