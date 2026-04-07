package com.app.controller;

import com.app.dto.ResumeDTO;
import com.app.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService service;

    @PostMapping
    public ResponseEntity<ResumeDTO> createResume(@Valid @RequestBody ResumeDTO dto) {
        return ResponseEntity.ok(service.createResume(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeDTO> getResume(@PathVariable int id) {
        return ResponseEntity.ok(service.getResumeById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeDTO>> getByUser(@PathVariable int userId) {
        return ResponseEntity.ok(service.getResumesByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeDTO> updateResume(
            @PathVariable int id,
            @Valid @RequestBody ResumeDTO dto
    ) {
        return ResponseEntity.ok(service.updateResume(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteResume(@PathVariable int id) {
        service.deleteResume(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ResumeDTO> duplicate(@PathVariable int id) {
        return ResponseEntity.ok(service.duplicateResume(id));
    }

    @PatchMapping("/{id}/ats")
    public ResponseEntity<String> updateAts(
            @PathVariable int id,
            @RequestParam int score
    ) {
        service.updateAtsScore(id, score);
        return ResponseEntity.ok("ATS score updated");
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<String> publish(@PathVariable int id) {
        service.publishResume(id);
        return ResponseEntity.ok("Resume published");
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<String> unpublish(@PathVariable int id) {
        service.unpublishResume(id);
        return ResponseEntity.ok("Resume unpublished");
    }

    @GetMapping("/public")
    public ResponseEntity<List<ResumeDTO>> getPublicResumes() {
        return ResponseEntity.ok(service.getPublicResumes());
    }

    @PatchMapping("/{id}/view")
    public ResponseEntity<String> incrementView(@PathVariable int id) {
        service.incrementViewCount(id);
        return ResponseEntity.ok("View count incremented");
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<ResumeDTO>> getByTemplate(@PathVariable int templateId) {
        return ResponseEntity.ok(service.getResumesByTemplate(templateId));
    }
}