package com.app.controller;

import com.app.entity.ResumeSection;
import com.app.enums.SectionType;
import com.app.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService service;

    @PostMapping
    public ResponseEntity<ResumeSection> add(@RequestBody ResumeSection section) {
        return ResponseEntity.ok(service.addSection(section));
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<ResumeSection>> getByResume(@PathVariable Integer resumeId) {
        return ResponseEntity.ok(service.getSectionsByResume(resumeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeSection> getById(@PathVariable String id) {
        return service.getSectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeSection> update(
            @PathVariable String id,
            @RequestBody ResumeSection section) {
        return ResponseEntity.ok(service.updateSection(id, section));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteSection(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reorder/{resumeId}")
    public ResponseEntity<Void> reorder(
            @PathVariable Integer resumeId,
            @RequestBody List<String> sectionIds) {
        service.reorderSections(resumeId, sectionIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/toggle/{id}")
    public ResponseEntity<Void> toggleVisibility(@PathVariable String id) {
        service.toggleVisibility(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/resume/{resumeId}")
    public ResponseEntity<Void> deleteAll(@PathVariable Integer resumeId) {
        service.deleteAllSections(resumeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/type")
    public ResponseEntity<ResumeSection> getByType(
            @RequestParam Integer resumeId,
            @RequestParam SectionType type) {
        return service.getSectionsByType(resumeId, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bulk/{resumeId}")
    public ResponseEntity<List<ResumeSection>> bulkUpdate(
            @PathVariable Integer resumeId,
            @RequestBody List<ResumeSection> sections) {
        return ResponseEntity.ok(service.bulkUpdateSections(resumeId, sections));
    }
}