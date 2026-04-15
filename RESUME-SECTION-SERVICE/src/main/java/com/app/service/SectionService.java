package com.app.service;

import com.app.entity.ResumeSection;
import com.app.enums.SectionType;

import java.util.List;
import java.util.Optional;

public interface SectionService {

    ResumeSection addSection(ResumeSection section);

    List<ResumeSection> getSectionsByResume(Integer resumeId);

    Optional<ResumeSection> getSectionById(String sectionId);

    ResumeSection updateSection(String sectionId, ResumeSection section);

    void deleteSection(String sectionId);

    void reorderSections(Integer resumeId, List<String> sectionIds);

    void toggleVisibility(String sectionId);

    void deleteAllSections(Integer resumeId);

    Optional<ResumeSection> getSectionsByType(Integer resumeId, SectionType type);

    List<ResumeSection> bulkUpdateSections(Integer resumeId, List<ResumeSection> sections);
}