package com.app.service;

import com.app.dto.ResumeDTO;
import com.app.entity.Resume;
import com.app.exception.ResourceNotFoundException;
import com.app.mapper.ResumeMapper;
import com.app.repository.ResumeRepository;
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
public class ResumeServiceTest {

    @Mock
    private ResumeRepository repository;

    @Mock
    private ResumeMapper mapper;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private Resume resume;
    private ResumeDTO resumeDTO;

    @BeforeEach
    void setUp() {
        resume = Resume.builder()
                .resumeId(1)
                .userId(1)
                .title("Software Engineer Resume")
                .targetJobTitle("Full Stack Developer")
                .templateId(1)
                .atsScore(85)
                .status("DRAFT")
                .build();

        resumeDTO = new ResumeDTO();
        resumeDTO.setTitle("Software Engineer Resume");
        resumeDTO.setUserId(1);
    }

    @Test
    void createResume_ShouldReturnSavedResume() {
        when(mapper.toEntity(any(ResumeDTO.class))).thenReturn(resume);
        when(repository.save(any(Resume.class))).thenReturn(resume);

        Resume saved = resumeService.createResume(resumeDTO);

        assertNotNull(saved);
        assertEquals("Software Engineer Resume", saved.getTitle());
        verify(repository, times(1)).save(any(Resume.class));
    }

    @Test
    void getResumeById_ShouldReturnResume() {
        when(repository.findById(1)).thenReturn(Optional.of(resume));

        Resume found = resumeService.getResumeById(1);

        assertNotNull(found);
        assertEquals(1, found.getResumeId());
    }

    @Test
    void getResumeById_ShouldThrowExceptionWhenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resumeService.getResumeById(99));
    }

    @Test
    void duplicateResume_ShouldCreateCopy() {
        when(repository.findById(1)).thenReturn(Optional.of(resume));
        when(repository.save(any(Resume.class))).thenAnswer(i -> i.getArguments()[0]);

        Resume copy = resumeService.duplicateResume(1);

        assertNotNull(copy);
        assertTrue(copy.getTitle().contains("(Copy)"));
        assertEquals(resume.getUserId(), copy.getUserId());
        verify(repository, times(1)).save(any(Resume.class));
    }

    @Test
    void updateAtsScore_ShouldUpdateScore() {
        when(repository.findById(1)).thenReturn(Optional.of(resume));

        resumeService.updateAtsScore(1, 95);

        assertEquals(95, resume.getAtsScore());
        verify(repository, times(1)).save(resume);
    }
}
