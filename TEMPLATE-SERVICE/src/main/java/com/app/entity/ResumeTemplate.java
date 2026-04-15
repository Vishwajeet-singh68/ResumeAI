package com.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "resume_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;

    private String name;

    @Column(length = 1000)
    private String description;

    private String thumbnailUrl;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String htmlLayout;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String cssStyles;

    private String category;

    private Boolean isPremium;
    private Boolean isActive;

    private Integer usageCount;

    private LocalDate createdAt;
}