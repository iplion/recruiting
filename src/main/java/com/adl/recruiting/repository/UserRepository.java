package com.adl.recruiting.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    List<User> findAllByRole_Name(String roleName);

    List<User> findAllByRole_NameAndTelegramChatIdNotNull(String roleName);
}
