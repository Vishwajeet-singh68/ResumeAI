package org.app.aicontentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRequest {

    @Id
    private String requestId;

    private int userId;
    private int resumeId;

    private String requestType; // SUMMARY, BULLETS, etc.

    @Column(columnDefinition = "TEXT")
    private String inputPrompt;

    @Column(columnDefinition = "TEXT")
    private String aiResponse;

    private String model; // GEMINI

    private int tokensUsed;

    private String status; // QUEUED, COMPLETED, FAILED

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}