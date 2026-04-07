package com.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resumeId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String title;

    private String targetJobTitle;

    private Integer templateId;

    @Builder.Default
    private Integer atsScore = 0;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, PUBLISHED

    private String language;

    @Builder.Default
    @Column(name = "is_public")
    private boolean isPublic = false;

    @Builder.Default
    private Integer viewCount = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ✅ Automatically set before insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.atsScore == null) this.atsScore = 0;
        if (this.status == null) this.status = "DRAFT";
        if (this.viewCount == null) this.viewCount = 0;
    }

    // ✅ Automatically update timestamp
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}