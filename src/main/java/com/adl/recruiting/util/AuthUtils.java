package com.adl.recruiting.util;

import com.adl.recruiting.entity.User;
import com.adl.recruiting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final UserRepository userRepository;

    public User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String login;

        if (principal instanceof UserDetails ud) {
            login = ud.getUsername();
        } else if (principal instanceof String s) {
            // иногда principal = "username"
            login = s;
        } else {
            throw new IllegalStateException("Invalid authentication principal: " + principal);
        }

        return userRepository.findByLogin(login)
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + login));
    }
}
