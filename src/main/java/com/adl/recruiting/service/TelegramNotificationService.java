package com.adl.recruiting.service;

import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.integration.TelegramClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final TelegramClient telegramClient;

    @Value("${telegram.bot-token:}")
    private String botToken;

    @Value("${telegram.default-chat-id:}")
    private Long defaultChatId;

    public void notify(Long chatId, String message) {
        Long target = (chatId != null) ? chatId : defaultChatId;
        if (target == null) return;
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
}
