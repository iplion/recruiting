package com.adl.recruiting.service;

import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.integration.TelegramClient;
import com.adl.recruiting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final TelegramClient telegramClient;
    private final UserRepository userRepository;

    @Value("${telegram.bot-token:}")
    private String botToken;

    @Value("${telegram.default-chat-id:}")
    private Long defaultChatId;

    public void notify(Long chatId, String message) {
        Long target = (chatId != null) ? chatId : defaultChatId;
        if (target == null) return;
        if (botToken == null || botToken.isBlank()) return;
        telegramClient.sendMessage(botToken, target, message);
    }

    public void notifyUser(User user, String message) {
        if (user == null) {
            notify((Long) null, message);
            return;
        }
        notify(user.getTelegramChatId(), message);
    }

    public void notifyCandidate(Candidate candidate, String message) {
        if (candidate == null) {
            notify((Long) null, message);
            return;
        }
        notify(candidate.getTelegramChatId(), message);
    }

    public void notifyRole(String roleName, String message) {
        if (roleName == null || roleName.isBlank()) return;

        List<User> users = userRepository.findAllByRole_NameAndTelegramChatIdNotNull(roleName);
        if (users.isEmpty()) {
            notify((Long) null, message);
            return;
        }

        for (User u : users) {
            notifyUser(u, message);
        }
    }
}
