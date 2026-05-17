package org.app.aicontentservice.controllers;

import lombok.RequiredArgsConstructor;
import org.app.aicontentservice.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiResource {

    private final AiService aiService;

    // 🧠 SUMMARY
    @PostMapping("/generateSummary")
    public ResponseEntity<?> generateSummary(@RequestBody Map<String, String> req) {
        String result = aiService.generateSummary(
                Integer.parseInt(req.get("userId")),
                req.get("resume"),
                req.get("jobDesc")
        );
        return ResponseEntity.ok(Map.of("result",result));
    }

    // 📌 BULLET POINTS
    @PostMapping("/generateBullets")
    public ResponseEntity<List<String>> generateBullets(@RequestBody Map<String, String> req) {

        int userId = Integer.parseInt(req.get("userId"));

        String role = req.getOrDefault("role", "");
        String company = req.getOrDefault("company", "");
        String description = req.getOrDefault("description", "");

        // Combine into experience text (since your service expects it)
        String experience = String.format(
                "Role: %s\nCompany: %s\nDescription: %s",
                role, company, description
        );

        List<String> result = aiService.generateBulletPoints(
                userId,
                experience,
                "" // jobDesc not provided from frontend
        );

        return ResponseEntity.ok(result);
    }

    // 📄 COVER LETTER
    @PostMapping("/generateCoverLetter")
    public ResponseEntity<?> generateCoverLetter(@RequestBody Map<String, String> req) {

        String resume = """
        Name: %s
        Job Title: %s

        Summary:
        %s

        Skills:
        %s

        Experience:
        %s
        """.formatted(
                req.get("fullName"),
                req.get("jobTitle"),
                req.get("summary"),
                req.get("skills"),
                req.get("experience")
        );

        String jobDesc = """
        Company: %s
        Role: %s
        """.formatted(
                req.get("company"),
                req.get("jobTitle")
        );

        String result = aiService.generateCoverLetter(
                Integer.parseInt(req.get("userId")),
                resume,
                jobDesc
        );

        return ResponseEntity.ok(Map.of("result",result));
    }

    // ✨ IMPROVE SECTION
    @PostMapping("/improveSection")
    public ResponseEntity<?> improveSection(@RequestBody Map<String, String> req) {
        String result = aiService.improveSection(
                Integer.parseInt(req.get("userId")),
                req.get("section"),
                req.get("jobDesc"),
                req.get("jobTitle")
        );
        return ResponseEntity.ok(Map.of("result", result));
    }

    // 📊 ATS CHECK
    @PostMapping("/checkAts")
    public ResponseEntity<?> checkAts(@RequestBody Map<String, String> req) {
        String result = aiService.checkAtsCompatibility(
                Integer.parseInt(req.get("userId")),
                req.get("resume")
        );
        return ResponseEntity.ok(result);
    }

    // 🧠 SUGGEST SKILLS
    @PostMapping("/suggestSkills")
    public ResponseEntity<List<String>> suggestSkills(@RequestBody Map<String, String> req) {
        List<String> result = aiService.suggestSkills(
                Integer.parseInt(req.get("userId")),
                req.get("jobDesc")
        );
        return ResponseEntity.ok(result);
    }

    // 🎯 TAILOR RESUME
    @PostMapping("/tailorForJob")
    public ResponseEntity<?> tailorForJob(@RequestBody Map<String, String> req) {
        String result = aiService.tailorResumeForJob(
                Integer.parseInt(req.get("userId")),
                req.get("resume"),
                req.get("jobDesc")
        );
        return ResponseEntity.ok(result);
    }

    // 🌍 TRANSLATE
    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translate(@RequestBody Map<String, String> req) {
        Map<String, String> result = aiService.translateResume(
                Integer.parseInt(req.get("userId")),
                req.get("resume")
        );
        return ResponseEntity.ok(result);
    }

    // 📜 HISTORY
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getHistory(@PathVariable int userId) {
        return ResponseEntity.ok(aiService.getAiHistory(userId));
    }

    // 📊 QUOTA
    @GetMapping("/quota/{userId}")
    public ResponseEntity<?> getQuota(@PathVariable int userId) {
        return ResponseEntity.ok(aiService.getRemainingQuota(userId));
    }

//    @PostMapping("/analyze")
//    public Map<String, Object> analyze(@RequestBody Map<String, String> request) {
//
//        String resume = request.get("resume");
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("skills", aiService.extractSkills(resume));
//        response.put("experienceLevel", aiService.getExperienceLevel(resume));
//        response.put("keywords", aiService.extractKeywords(resume));
//
//        return response;
//    }

    // 🎯 Job Recommendations
//    @PostMapping("/recommendations")
//    public String getRecommendations(@RequestBody Map<String, String> request) {
//
//        int userId = Integer.parseInt(request.get("userId"));
//        String resume = request.get("resume");
//        String jobDesc = request.get("jobDescription");
//
//        return aiService.generateRecommendations(userId, resume, jobDesc).toString();
//    }
}