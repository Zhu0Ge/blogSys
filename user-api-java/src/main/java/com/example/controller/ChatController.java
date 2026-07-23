package com.example.controller;

import com.example.common.R;
import com.example.service.IDeepSeekService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final IDeepSeekService deepSeekService;

    public ChatController(IDeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping
    public R<Map<String, Object>> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return R.error("Message is required");
        }
        Map<String, Object> reply = deepSeekService.chat(message);
        return R.success(reply);
    }
}