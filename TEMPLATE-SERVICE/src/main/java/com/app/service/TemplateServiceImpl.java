package com.app.service;

import com.app.entity.ResumeTemplate;
import com.app.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository repository;

    @Override
    public ResumeTemplate createTemplate(ResumeTemplate template) {
        template.setCreatedAt(LocalDate.now());
        template.setUsageCount(0);
        template.setIsActive(true);
        return repository.save(template);
    }

    @Override
    public Optional<ResumeTemplate> getTemplateById(Long id) {
        return repository.findByTemplateId(id);
    }

    @Override
    public List<ResumeTemplate> getAllTemplates() {
        return repository.findAll();
    }

    @Override
    public List<ResumeTemplate> getFreeTemplates() {
        return repository.findByIsPremium(false);
    }

    @Override
    public List<ResumeTemplate> getPremiumTemplates() {
        return repository.findByIsPremium(true);
    }

    @Override
    public List<ResumeTemplate> getByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    public ResumeTemplate updateTemplate(Long id, ResumeTemplate template) {
        ResumeTemplate existing = repository.findByTemplateId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        existing.setName(template.getName());
        existing.setDescription(template.getDescription());
        existing.setHtmlLayout(template.getHtmlLayout());
        existing.setCssStyles(template.getCssStyles());
        existing.setCategory(template.getCategory());
        existing.setIsPremium(template.getIsPremium());

        return repository.save(existing);
    }

    @Override
    public void deactivateTemplate(Long id) {
        ResumeTemplate template = repository.findByTemplateId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setIsActive(false);
        repository.save(template);
    }

    @Override
    public void incrementUsage(Long id) {
        ResumeTemplate template = repository.findByTemplateId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setUsageCount(template.getUsageCount() + 1);
        repository.save(template);
    }

    @Override
    public List<ResumeTemplate> getPopularTemplates() {
        return repository.findAllByOrderByUsageCountDesc();
    }
}