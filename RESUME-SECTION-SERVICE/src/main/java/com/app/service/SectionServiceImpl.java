package com.app.service.impl;

import com.app.entity.ResumeSection;
import com.app.enums.SectionType;
import com.app.repository.SectionRepository;
import com.app.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository repository;

    @Override
    public ResumeSection addSection(ResumeSection section) {
        return repository.save(section);
    }

    @Override
    public List<ResumeSection> getSectionsByResume(Integer resumeId) {
        return repository.findByResumeIdOrderByDisplayOrder(resumeId);
    }

    @Override
    public Optional<ResumeSection> getSectionById(String sectionId) {
        return repository.findById(sectionId);
    }

    @Override
    public ResumeSection updateSection(String sectionId, ResumeSection section) {
        section.setSectionId(sectionId);
        return repository.save(section);
    }

    @Override
    public void deleteSection(String sectionId) {
        repository.deleteById(sectionId);
    }

    @Override
    public void reorderSections(Integer resumeId, List<String> sectionIds) {
        List<ResumeSection> sections = repository.findByResumeId(resumeId);

        for (int i = 0; i < sectionIds.size(); i++) {
            for (ResumeSection section : sections) {
                if (section.getSectionId().equals(sectionIds.get(i))) {
                    section.setDisplayOrder(i);
                }
            }
        }

        repository.saveAll(sections);
    }

    @Override
    public void toggleVisibility(String sectionId) {
        ResumeSection section = repository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        section.setIsVisible(!section.getIsVisible());
        repository.save(section);
    }

    @Override
    public void deleteAllSections(Integer resumeId) {
        repository.deleteByResumeId(resumeId);
    }

    @Override
    public Optional<ResumeSection> getSectionsByType(Integer resumeId, SectionType type) {
        return repository.findByResumeIdAndSectionType(resumeId, type);
    }

    @Override
    public List<ResumeSection> bulkUpdateSections(Integer resumeId, List<ResumeSection> sections) {
        sections.forEach(section -> section.setResumeId(resumeId));
        return repository.saveAll(sections);
    }
}