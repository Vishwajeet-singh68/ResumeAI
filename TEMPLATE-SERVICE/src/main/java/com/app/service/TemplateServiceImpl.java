package com.app.service;

import com.app.entity.ResumeTemplate;
import com.app.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository repository;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Override
    @CacheEvict(value = "templates", allEntries = true)
    public ResumeTemplate createTemplate(ResumeTemplate template) {
        template.setCreatedAt(LocalDate.now());
        template.setUsageCount(0);
        template.setIsActive(true);
        ResumeTemplate saved = repository.save(template);
        
        try {
            com.app.dto.TemplateEvent event = new com.app.dto.TemplateEvent(
                saved.getTemplateId(), 
                saved.getName(), 
                saved.getCategory()
            );
            rabbitTemplate.convertAndSend("notification_exchange", "template.new", event);
        } catch (Exception e) {
            System.err.println("Failed to publish template event: " + e.getMessage());
        }
        
        return saved;
    }

    @Override
    @Cacheable(value = "templates", key = "#id")
    public Optional<ResumeTemplate> getTemplateById(Long id) {
        return repository.findByTemplateId(id);
    }

    @Override
    @Cacheable(value = "templates", key = "'all'")
    public List<ResumeTemplate> getAllTemplates() {
        return repository.findAll();
    }

    @Override
    @Cacheable(value = "templates", key = "'free'")
    public List<ResumeTemplate> getFreeTemplates() {
        return repository.findByIsPremium(false);
    }

    @Override
    @Cacheable(value = "templates", key = "'premium'")
    public List<ResumeTemplate> getPremiumTemplates() {
        return repository.findByIsPremium(true);
    }

    @Override
    @Cacheable(value = "templates", key = "#category")
    public List<ResumeTemplate> getByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    @CachePut(value = "templates", key = "#id")
    @CacheEvict(value = "templates", key = "'all'")
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
    @CacheEvict(value = "templates", key = "#id")
    public void deactivateTemplate(Long id) {
        ResumeTemplate template = repository.findByTemplateId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setIsActive(false);
        repository.save(template);
    }

    @Override
    @CacheEvict(value = "templates", key = "#id")
    public void incrementUsage(Long id) {
        ResumeTemplate template = repository.findByTemplateId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setUsageCount(template.getUsageCount() + 1);
        repository.save(template);
    }

    @Override
    @Cacheable(value = "templates", key = "'popular'")
    public List<ResumeTemplate> getPopularTemplates() {
        return repository.findAllByOrderByUsageCountDesc();
    }

}