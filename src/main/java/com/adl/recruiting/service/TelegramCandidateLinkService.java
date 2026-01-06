package com.adl.recruiting.service;

import com.adl.recruiting.controller.TelegramWebhookController.TelegramUpdate;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.integration.TelegramClient;
import com.adl.recruiting.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TelegramCandidateLinkService {

    private final CandidateRepository candidateRepository;
    private final TelegramClient telegramClient;

    @Value("${telegram.bot-token:}")
    private String botToken;

    @Value("${telegram.webhook-secret:}")
    private String webhookSecret;

    @Value("${app.candidate-portal-base-url:http://localhost:5173}")
    private String portalBaseUrl;

    public void handleUpdate(String secret, TelegramUpdate update) {
        if (!webhookSecret.equals(secret)) {
            return;
        }

        if (update == null || update.message() == null) return;
        Long chatId = update.message().chat() != null ? update.message().chat().id() : null;
        String text = update.message().text();
        if (chatId == null || text == null) return;

        String trimmed = text.trim();
        if (!trimmed.startsWith("/start")) {
            telegramClient.sendMessage(botToken, chatId, "Привет! Открой полученную ссылку (там /start <token>).");
            return;
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length < 2) {
            telegramClient.sendMessage(botToken, chatId, "Не вижу токен. Открой чат через ссылку.");
            return;
        }

        String token = parts[1];

        Candidate candidate = candidateRepository.findByAccessToken(token).orElse(null);
        if (candidate == null) {
            telegramClient.sendMessage(botToken, chatId, "Токен не найден или уже недействителен.");
            return;
        }

        OffsetDateTime exp = candidate.getTokenExpiresAt();
        if (exp != null && exp.isBefore(OffsetDateTime.now())) {
            telegramClient.sendMessage(botToken, chatId, "Токен истёк. Попроси директора создать нового кандидата/токен.");
            return;
        }

        candidate.setTelegramChatId(chatId);
        candidateRepository.save(candidate);

        String testLink = portalBaseUrl + "/candidate?token=" + candidate.getAccessToken();

        telegramClient.sendMessage(botToken, chatId,
            "✅ Аккаунт привязан.\n" +
                "Ссылка на тесты: " + testLink);
    }
}
