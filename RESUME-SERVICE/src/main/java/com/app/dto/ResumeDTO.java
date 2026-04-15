package com.app.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeDTO {

    private Integer userId;
    private String title;
    private String targetJobTitle;
    private Integer templateId;
    private Integer atsScore;
    private String status;
    private String language;
    private boolean isPublic;
    private Integer viewCount;
}