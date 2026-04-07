package com.app.repository;

import com.app.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {

    List<Resume> findByUserId(int userId);

    Optional<Resume> findByResumeId(int resumeId);

    List<Resume> findByStatus(String status);

    List<Resume> findByTargetJobTitle(String targetJobTitle);

    List<Resume> findByIsPublic(boolean isPublic);

    int countByUserId(int userId);

    List<Resume> findByTemplateId(int templateId);

    void deleteByResumeId(int resumeId);
}