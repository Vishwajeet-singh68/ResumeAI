package com.app.controller;

import com.app.entity.ResumeTemplate;
import com.app.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<ResumeTemplate> create(@RequestBody ResumeTemplate template) {
        return ResponseEntity.ok(templateService.createTemplate(template));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @GetMapping
    public ResponseEntity<List<ResumeTemplate>> getAll() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/free")
    public ResponseEntity<List<ResumeTemplate>> getFree() {
        return ResponseEntity.ok(templateService.getFreeTemplates());
    }

    @GetMapping("/premium")
    public ResponseEntity<List<ResumeTemplate>> getPremium() {
        return ResponseEntity.ok(templateService.getPremiumTemplates());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ResumeTemplate>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(templateService.getByCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeTemplate> update(@PathVariable Long id,
                                                 @RequestBody ResumeTemplate template) {
        return ResponseEntity.ok(templateService.updateTemplate(id, template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivate(@PathVariable Long id) {
        templateService.deactivateTemplate(id);
        return ResponseEntity.ok("Template deactivated");
    }

    @PutMapping("/{id}/usage")
    public ResponseEntity<String> incrementUsage(@PathVariable Long id) {
        templateService.incrementUsage(id);
        return ResponseEntity.ok("Usage incremented");
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ResumeTemplate>> getPopular() {
        return ResponseEntity.ok(templateService.getPopularTemplates());
    }
}