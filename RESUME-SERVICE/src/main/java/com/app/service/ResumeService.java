package com.app.service;

import com.app.dto.ResumeDTO;

import java.util.List;

public interface ResumeService {

    ResumeDTO createResume(ResumeDTO dto);

    ResumeDTO getResumeById(int id);

    List<ResumeDTO> getResumesByUser(int userId);

    ResumeDTO updateResume(int id, ResumeDTO dto);

    void deleteResume(int id);

    ResumeDTO duplicateResume(int id);

    void updateAtsScore(int id, int score);

    void publishResume(int id);

    void unpublishResume(int id);

    List<ResumeDTO> getPublicResumes();

    void incrementViewCount(int id);

    List<ResumeDTO> getResumesByTemplate(int templateId);
}