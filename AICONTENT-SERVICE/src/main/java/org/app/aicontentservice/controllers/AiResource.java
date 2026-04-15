package org.app.aicontentservice.controllers;

import lombok.RequiredArgsConstructor;
import org.app.aicontentservice.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiResource {

    private final AiService aiService;

    @PostMapping("/summary")
    public ResponseEntity<?> summary(@RequestBody Map<String, String> req) {
        return ResponseEntity.ok(
                aiService.generateSummary(
                        Integer.parseInt(req.get("userId")),
                        req.get("resume"),
                        req.get("jobDesc"))
        );
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> history(@PathVariable int userId) {
        return ResponseEntity.ok(aiService.getAiHistory(userId));
    }

    @GetMapping("/quota/{userId}")
    public ResponseEntity<?> quota(@PathVariable int userId) {
        return ResponseEntity.ok(aiService.getRemainingQuota(userId));
    }
}