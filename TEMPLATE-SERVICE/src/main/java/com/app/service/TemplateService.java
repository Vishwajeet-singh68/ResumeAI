package com.app.service;

import com.app.entity.ResumeTemplate;

import java.util.List;
import java.util.Optional;

public interface TemplateService {

    ResumeTemplate createTemplate(ResumeTemplate template);

    Optional<ResumeTemplate> getTemplateById(Long id);

    List<ResumeTemplate> getAllTemplates();

    List<ResumeTemplate> getFreeTemplates();

    List<ResumeTemplate> getPremiumTemplates();

    List<ResumeTemplate> getByCategory(String category);

    ResumeTemplate updateTemplate(Long id, ResumeTemplate template);

    void deactivateTemplate(Long id);

    void incrementUsage(Long id);

    List<ResumeTemplate> getPopularTemplates();
}