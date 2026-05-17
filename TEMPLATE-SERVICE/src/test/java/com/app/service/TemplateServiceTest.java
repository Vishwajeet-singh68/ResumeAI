package com.app.service;

import com.app.entity.ResumeTemplate;
import com.app.repository.TemplateRepository;
import com.app.dto.TemplateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

    @Mock
    private TemplateRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private ResumeTemplate template;

    @BeforeEach
    void setUp() {
        template = ResumeTemplate.builder()
                .templateId(1L)
                .name("Modern Template")
                .category("Professional")
                .isPremium(false)
                .usageCount(0)
                .isActive(true)
                .build();
    }

    @Test
    void createTemplate_ShouldReturnSavedTemplateAndPublishEvent() {
        when(repository.save(any(ResumeTemplate.class))).thenReturn(template);

        ResumeTemplate saved = templateService.createTemplate(template);

        assertNotNull(saved);
        assertEquals("Modern Template", saved.getName());
        verify(repository, times(1)).save(any(ResumeTemplate.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("notification_exchange"), eq("template.new"), any(TemplateEvent.class));
    }

    @Test
    void getTemplateById_ShouldReturnTemplate() {
        when(repository.findByTemplateId(1L)).thenReturn(Optional.of(template));

        Optional<ResumeTemplate> found = templateService.getTemplateById(1L);

        assertTrue(found.isPresent());
        assertEquals("Modern Template", found.get().getName());
    }

    @Test
    void getAllTemplates_ShouldReturnList() {
        when(repository.findAll()).thenReturn(Arrays.asList(template));

        List<ResumeTemplate> templates = templateService.getAllTemplates();

        assertFalse(templates.isEmpty());
        assertEquals(1, templates.size());
    }

    @Test
    void incrementUsage_ShouldIncreaseCount() {
        when(repository.findByTemplateId(1L)).thenReturn(Optional.of(template));

        templateService.incrementUsage(1L);

        assertEquals(1, template.getUsageCount());
        verify(repository, times(1)).save(template);
    }

    @Test
    void deactivateTemplate_ShouldSetIsActiveToFalse() {
        when(repository.findByTemplateId(1L)).thenReturn(Optional.of(template));

        templateService.deactivateTemplate(1L);

        assertFalse(template.getIsActive());
        verify(repository, times(1)).save(template);
    }
}
