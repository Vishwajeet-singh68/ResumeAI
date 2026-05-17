package com.app.service;

import com.app.entity.ResumeSection;
import com.app.enums.SectionType;
import com.app.repository.SectionRepository;
import com.app.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository repository;

    @Override
    @CacheEvict(value = "sections", key = "'resume_' + #section.resumeId")
    public ResumeSection addSection(ResumeSection section) {
        return repository.save(section);
    }

    @Override
    @Cacheable(value = "sections", key = "'resume_' + #resumeId")
    public List<ResumeSection> getSectionsByResume(Integer resumeId) {
        return repository.findByResumeIdOrderByDisplayOrder(resumeId);
    }

    @Override
    @Cacheable(value = "sections", key = "#sectionId")
    public Optional<ResumeSection> getSectionById(String sectionId) {
        return repository.findById(sectionId);
    }

    @Override
    @CachePut(value = "sections", key = "#sectionId")
    @CacheEvict(value = "sections", key = "'resume_' + #section.resumeId")
    public ResumeSection updateSection(String sectionId, ResumeSection section) {
        section.setSectionId(sectionId);
        return repository.save(section);
    }

    @Override
    @CacheEvict(value = "sections", allEntries = true)
    public void deleteSection(String sectionId) {
        repository.deleteById(sectionId);
    }

    @Override
    @CacheEvict(value = "sections", key = "'resume_' + #resumeId")
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
    @CacheEvict(value = "sections", allEntries = true)
    public void toggleVisibility(String sectionId) {
        ResumeSection section = repository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        section.setIsVisible(!section.getIsVisible());
        repository.save(section);
    }

    @Override
    @CacheEvict(value = "sections", key = "'resume_' + #resumeId")
    public void deleteAllSections(Integer resumeId) {
        repository.deleteByResumeId(resumeId);
    }

    @Override
    @Cacheable(value = "sections", key = "'resume_' + #resumeId + '_' + #type")
    public Optional<ResumeSection> getSectionsByType(Integer resumeId, SectionType type) {
        return repository.findByResumeIdAndSectionType(resumeId, type);
    }

    @Override
    @CacheEvict(value = "sections", key = "'resume_' + #resumeId")
    public List<ResumeSection> bulkUpdateSections(Integer resumeId, List<ResumeSection> sections) {
        sections.forEach(section -> section.setResumeId(resumeId));
        return repository.saveAll(sections);
    }
}