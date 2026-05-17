package com.app.service;

import com.app.entity.ResumeSection;
import com.app.enums.SectionType;
import com.app.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    @Mock
    private SectionRepository repository;

    @InjectMocks
    private SectionServiceImpl sectionService;

    private ResumeSection section;

    @BeforeEach
    void setUp() {
        section = ResumeSection.builder()
                .sectionId("sec123")
                .resumeId(1)
                .sectionType(SectionType.EXPERIENCE)
                .title("Work Experience")
                .isVisible(true)
                .displayOrder(0)
                .build();
    }

    @Test
    void addSection_ShouldReturnSavedSection() {
        when(repository.save(any(ResumeSection.class))).thenReturn(section);

        ResumeSection saved = sectionService.addSection(section);

        assertNotNull(saved);
        assertEquals("sec123", saved.getSectionId());
        verify(repository, times(1)).save(any(ResumeSection.class));
    }

    @Test
    void getSectionsByResume_ShouldReturnList() {
        when(repository.findByResumeIdOrderByDisplayOrder(1)).thenReturn(Arrays.asList(section));

        List<ResumeSection> sections = sectionService.getSectionsByResume(1);

        assertFalse(sections.isEmpty());
        assertEquals(1, sections.size());
    }

    @Test
    void toggleVisibility_ShouldInvertValue() {
        when(repository.findById("sec123")).thenReturn(Optional.of(section));

        sectionService.toggleVisibility("sec123");

        assertFalse(section.getIsVisible());
        verify(repository, times(1)).save(section);
    }

    @Test
    void bulkUpdateSections_ShouldSetResumeIdAndSaveAll() {
        List<ResumeSection> sections = Arrays.asList(
                ResumeSection.builder().title("S1").build(),
                ResumeSection.builder().title("S2").build()
        );
        when(repository.saveAll(anyList())).thenReturn(sections);

        List<ResumeSection> result = sectionService.bulkUpdateSections(1, sections);

        assertEquals(2, result.size());
        assertEquals(1, sections.get(0).getResumeId());
        verify(repository, times(1)).saveAll(sections);
    }
}
