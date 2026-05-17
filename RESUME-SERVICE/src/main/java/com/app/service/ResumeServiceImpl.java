package com.app.service;

import com.app.dto.ResumeDTO;
import com.app.entity.Resume;
import com.app.exception.ResourceNotFoundException;
import com.app.mapper.ResumeMapper;
import com.app.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository repository;
    private final ResumeMapper mapper;

    @Override
    @CacheEvict(value = "resumes", allEntries = true)
    public Resume createResume(ResumeDTO dto) {
        Resume resume = mapper.toEntity(dto);
        return repository.save(resume);
    }

    @Override
    @Cacheable(value = "resumes", key = "#id")
    public Resume getResumeById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
    }

    @Override
    @Cacheable(value = "resumes", key = "'user_' + #userId")
    public List<Resume> getResumesByUser(int userId) {
        return repository.findByUserId(userId)
                .stream()
                .toList();
    }

    @Override
    @CachePut(value = "resumes", key = "#id")
    @CacheEvict(value = "resumes", key = "'user_' + #dto.userId")
    public Resume updateResume(int id, ResumeDTO dto) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        mapper.updateResumeFromDto(dto, resume);
        return repository.save(resume);
    }

    @Override
    @CacheEvict(value = "resumes", allEntries = true)
    public void deleteResume(int id) {
        repository.deleteById(id);
    }

    @Override
    @CacheEvict(value = "resumes", allEntries = true)
    public Resume duplicateResume(int id) {
        Resume original = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        Resume copy = Resume.builder()
                .userId(original.getUserId())
                .title(original.getTitle() + " (Copy)")
                .targetJobTitle(original.getTargetJobTitle())
                .templateId(original.getTemplateId())
                .atsScore(original.getAtsScore())
                .status(original.getStatus())
                .language(original.getLanguage())
                .isPublic(false)
                .viewCount(0)
                .build();

        return repository.save(copy);
    }

    @Override
    @CacheEvict(value = "resumes", key = "#id")
    public void updateAtsScore(int id, int score) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setAtsScore(score);
        repository.save(resume);
    }

    @Override
    @CacheEvict(value = "resumes", key = "#id")
    public void publishResume(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setPublic(true);
        repository.save(resume);
    }

    @Override
    @CacheEvict(value = "resumes", key = "#id")
    public void unpublishResume(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setPublic(false);
        repository.save(resume);
    }

    @Override
    @Cacheable(value = "resumes", key = "'public'")
    public List<Resume> getPublicResumes() {
        return repository.findByIsPublic(true)
                .stream()
                .toList();
    }

    @Override
    @CacheEvict(value = "resumes", key = "#id")
    public void incrementViewCount(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setViewCount(
                resume.getViewCount() == null ? 1 : resume.getViewCount() + 1
        );

        repository.save(resume);
    }

    @Override
    @Cacheable(value = "resumes", key = "'template_' + #templateId")
    public List<Resume> getResumesByTemplate(int templateId) {
        return repository.findByTemplateId(templateId)
                .stream()
                .toList();
    }
}