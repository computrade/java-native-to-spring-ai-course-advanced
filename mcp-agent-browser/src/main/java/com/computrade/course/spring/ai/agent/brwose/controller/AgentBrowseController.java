package com.computrade.course.spring.ai.agent.brwose.controller;

import com.computrade.course.spring.ai.agent.brwose.service.ChatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentBrowseController {

    private final ChatService chatService;

    @GetMapping("/visual")
    public ResponseEntity<String> visual(String prompt) {
        return ResponseEntity.ok(chatService.chatWithVisualAgent(prompt));

    }
    @GetMapping("/ecommerce")
    public ResponseEntity<String> ecommerce(String prompt) {
        return ResponseEntity.ok(chatService.chatEcommerceAgent(prompt));

    }

    @GetMapping("/form")
    public ResponseEntity<String> form(String prompt) {
        return ResponseEntity.ok(chatService.chatFormAssistantAgent(prompt));

    }
    @GetMapping("/linkedIn")
    public ResponseEntity<String> form(String prompt, HttpSession session) {
        String sessionId = session.getId();
        return ResponseEntity.ok(chatService.chatLinkedInAgent(sessionId, prompt));

    }

}
