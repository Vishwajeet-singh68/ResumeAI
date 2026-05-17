package com.app.service;

import com.app.dto.ResumeDTO;
import com.app.entity.Resume;

import java.util.List;

public interface ResumeService {

    Resume createResume(ResumeDTO dto);

    Resume getResumeById(int id);

    List<Resume> getResumesByUser(int userId);

    Resume updateResume(int id, ResumeDTO dto);

    void deleteResume(int id);

    Resume duplicateResume(int id);

    void updateAtsScore(int id, int score);

    void publishResume(int id);

    void unpublishResume(int id);

    List<Resume> getPublicResumes();

    void incrementViewCount(int id);

    List<Resume> getResumesByTemplate(int templateId);
}