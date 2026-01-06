package com.adl.recruiting.controller;

import com.adl.recruiting.service.TelegramCandidateLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telegram")
public class TelegramWebhookController {

    private final TelegramCandidateLinkService telegramCandidateLinkService;

    // секрет в URL, чтобы любой не мог спамить твою ручку
    @PostMapping("/webhook/{secret}")
    public void onUpdate(@PathVariable String secret, @RequestBody TelegramUpdate update) {
        telegramCandidateLinkService.handleUpdate(secret, update);
    }

    // минимальные DTO под то, что нам нужно
    public record TelegramUpdate(Message message) {
        public record Message(Chat chat, String text) {
            public record Chat(Long id) {}
        }
    }
}
