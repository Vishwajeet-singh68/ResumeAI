package com.app.repository;

import com.app.entity.ResumeSection;
import com.app.enums.SectionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends MongoRepository<ResumeSection, String> {

    List<ResumeSection> findByResumeId(Integer resumeId);

    Optional<ResumeSection> findByResumeIdAndSectionType(Integer resumeId, SectionType sectionType);

    List<ResumeSection> findByResumeIdOrderByDisplayOrder(Integer resumeId);

    List<ResumeSection> findByAiGenerated(Boolean aiGenerated);

    long countByResumeId(Integer resumeId);

    void deleteByResumeId(Integer resumeId);
}