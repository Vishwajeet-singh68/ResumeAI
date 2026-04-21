package com.app.entity;

import com.app.enums.SectionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "resume_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeSection {

    @Id
    private String sectionId;

    private Integer resumeId;

    private SectionType sectionType;

    private String title;

    // Flexible JSON content (rich text / structured data)
    private Map<String, Object> content;

    private Integer displayOrder;

    private Boolean isVisible;

    private Boolean aiGenerated;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}