package com.app.service;

import com.app.dto.ResumeDTO;
import com.app.entity.Resume;
import com.app.exception.ResourceNotFoundException;
import com.app.mapper.ResumeMapper;
import com.app.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository repository;
    private final ResumeMapper mapper;

    @Override
    public ResumeDTO createResume(ResumeDTO dto) {
        Resume resume = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(resume));
    }

    @Override
    public ResumeDTO getResumeById(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        return mapper.toDTO(resume);
    }

    @Override
    public List<ResumeDTO> getResumesByUser(int userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ResumeDTO updateResume(int id, ResumeDTO dto) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        mapper.updateResumeFromDto(dto, resume);
        return mapper.toDTO(repository.save(resume));
    }

    @Override
    public void deleteResume(int id) {
        repository.deleteById(id);
    }

    @Override
    public ResumeDTO duplicateResume(int id) {
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

        return mapper.toDTO(repository.save(copy));
    }

    @Override
    public void updateAtsScore(int id, int score) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setAtsScore(score);
        repository.save(resume);
    }

    @Override
    public void publishResume(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setPublic(true);
        repository.save(resume);
    }

    @Override
    public void unpublishResume(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setPublic(false);
        repository.save(resume);
    }

    @Override
    public List<ResumeDTO> getPublicResumes() {
        return repository.findByIsPublic(true)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void incrementViewCount(int id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.setViewCount(
                resume.getViewCount() == null ? 1 : resume.getViewCount() + 1
        );

        repository.save(resume);
    }

    @Override
    public List<ResumeDTO> getResumesByTemplate(int templateId) {
        return repository.findByTemplateId(templateId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}