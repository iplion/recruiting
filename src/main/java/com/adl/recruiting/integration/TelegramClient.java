package com.adl.recruiting.integration;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TelegramClient {

    private final RestClient restClient = RestClient.create();

    public void sendMessage(String botToken, Long chatId, String text) {
        if (botToken == null || botToken.isBlank()) return;
        if (chatId == null) return;
        if (text == null || text.isBlank()) return;

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        restClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new SendMessageRequest(chatId, text))
            .retrieve()
            .toBodilessEntity();
    }

    private record SendMessageRequest(Long chat_id, String text) {}
}
