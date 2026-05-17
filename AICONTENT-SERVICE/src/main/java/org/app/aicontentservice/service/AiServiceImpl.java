package org.app.aicontentservice.service;

import lombok.RequiredArgsConstructor;
import org.app.aicontentservice.client.GeminiClient;
import org.app.aicontentservice.entity.AiRequest;
import org.app.aicontentservice.repository.AiRequestRepository;
import org.app.aicontentservice.util.GeminiParser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final GeminiClient geminiClient;
    private final AiRequestRepository repo;

    // =========================================
    // 🔥 CORE PROCESS METHOD
    // =========================================
    private String process(int userId, String type, String prompt) {

        validateQuota(userId);

        String finalPrompt = """
        You are an AI Resume Assistant.

        Rules:
        - Give professional and concise output
        - Do NOT add explanations
        - Follow format strictly
        - Keep response clean and structured

        %s
        """.formatted(prompt);

        AiRequest req = AiRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .userId(userId)
                .requestType(type)
                .inputPrompt(finalPrompt)
                .status("QUEUED")
                .createdAt(LocalDateTime.now())
                .build();

        repo.save(req);

        try {
            String raw = geminiClient.generate(finalPrompt);
            int tokens = GeminiParser.getTokens(raw);

            req.setAiResponse(raw);
            req.setTokensUsed(tokens);
            req.setModel("GEMINI");
            req.setStatus("COMPLETED");
            req.setCompletedAt(LocalDateTime.now());

            repo.save(req);

            return raw;

        } catch (Exception e) {
            req.setStatus("FAILED");
            repo.save(req);
            throw new RuntimeException("AI failed: " + e.getMessage());
        }
    }

    // =========================================
    // 🔒 QUOTA
    // =========================================
    private void validateQuota(int userId) {
        if (repo.countByUserIdToday(userId) > 50) {
            throw new RuntimeException("Daily limit exceeded");
        }
    }

    @Override
    public int getRemainingQuota(int userId) {
        return 50 - repo.countByUserIdToday(userId);
    }

    // =========================================
    // 🧠 SUMMARY
    // =========================================
    @Override
    public String generateSummary(int userId, String resume, String jobDesc) {
        return process(userId, "SUMMARY", """
        Write a professional resume summary (3-4 lines).

        Resume:
        %s

        Job Description:
        %s
        """.formatted(resume, jobDesc));
    }

    // =========================================
    // 📌 BULLETS
    // =========================================
    @Override
    public List<String> generateBulletPoints(int userId, String exp, String jobDesc) {

        String res = process(userId, "BULLETS", """
        Convert the experience into exactly 3 strong resume bullet points.

        Rules:
        - Use action verbs
        - Max 20 words each
        - Start with "-"
        - No extra text

        Experience:
        %s
        """.formatted(exp));

        return parseBullets(res, 3);
    }

    // =========================================
    // 📄 COVER LETTER
    // =========================================
    @Override
    public String generateCoverLetter(int userId, String resume, String jobDesc) {
        return process(userId, "COVER_LETTER", """
        Write a professional cover letter (150-200 words).

        Use candidate details if available.

        Resume:
        %s

        Job Description:
        %s
        """.formatted(resume, jobDesc));
    }

    // =========================================
    // ✨ IMPROVE SECTION
    // =========================================
    @Override
    public String improveSection(int userId, String section, String content, String jobTitle) {
        return process(userId, "IMPROVE", """
        Improve the following %s section for %s role.

        Rules:
        - Keep meaning same
        - No fake data
        - Improve clarity only
        - ATS-friendly

        Content:
        %s
        """.formatted(section, jobTitle, content));
    }

    // =========================================
    // 📊 ATS
    // =========================================
    @Override
    public String checkAtsCompatibility(int userId, String resume) {
        return process(userId, "ATS", """
        Analyze resume for ATS.

        Return ONLY JSON:
        {
          "score": number,
          "strengths": [],
          "weaknesses": [],
          "suggestions": []
        }

        Resume:
        %s
        """.formatted(resume));
    }

    // =========================================
    // 🧠 SKILLS (AI + SAFE PARSE)
    // =========================================
    @Override
    public List<String> suggestSkills(int userId, String jobDesc) {

        String res = process(userId, "SKILLS", """
        Extract top 10 relevant skills.

        Return comma-separated values ONLY.

        Job Description:
        %s
        """.formatted(jobDesc));

        return Arrays.stream(res.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .limit(10)
                .toList();
    }

    // =========================================
    // 🎯 TAILOR
    // =========================================
    @Override
    public String tailorResumeForJob(int userId, String resume, String jobDesc) {
        return process(userId, "TAILOR", """
        Tailor resume for job.

        Keep structure same.

        Resume:
        %s

        Job Description:
        %s
        """.formatted(resume, jobDesc));
    }

    // =========================================
    // 🌍 TRANSLATE
    // =========================================
    @Override
    public Map<String, String> translateResume(int userId, String resume) {

        String res = process(userId, "TRANSLATE", """
        Translate to professional English.

        Resume:
        %s
        """.formatted(resume));

        return Map.of("translated", res);
    }

    // =========================================
    // 📜 HISTORY
    // =========================================
    @Override
    public List<?> getAiHistory(int userId) {
        return repo.findByUserId(userId);
    }

    // =========================================
    // 🔥 AI-BASED RECOMMENDATIONS
    // =========================================
    @Override
    public List<String> generateRecommendations(int userId, String resume, String jobDesc) {

        String res = process(userId, "RECOMMENDATIONS", """
        Act as a career coach.

        Give 5-7 improvement suggestions.

        Rules:
        - Start each with "-"
        - Be specific
        - No generic advice

        Resume:
        %s

        Job Description:
        %s
        """.formatted(resume, jobDesc));

        return parseBullets(res, 7);
    }

    // =========================================
    // ⚙️ NON-AI HELPERS (FAST)
    // =========================================

    @Override
    public List<String> extractSkills(String resume) {

        Set<String> skills = new HashSet<>();

        String[] known = {
                "Java", "Spring Boot", "React", "Node.js",
                "MongoDB", "MySQL", "Docker", "AWS"
        };

        String lower = resume.toLowerCase();

        for (String s : known) {
            if (lower.contains(s.toLowerCase())) {
                skills.add(s);
            }
        }

        return new ArrayList<>(skills);
    }

    @Override
    public String getExperienceLevel(String resume) {

        Matcher m = Pattern.compile("(\\d+)\\s*year").matcher(resume.toLowerCase());

        if (resume.toLowerCase().contains("intern")) return "Fresher";

        if (m.find()) {
            int y = Integer.parseInt(m.group(1));
            if (y <= 1) return "Fresher";
            if (y <= 3) return "Junior";
            if (y <= 6) return "Mid-Level";
            return "Senior";
        }

        return "Unknown";
    }

    @Override
    public List<String> extractKeywords(String resume) {

        String[] keys = {"backend", "api", "cloud", "microservices"};

        return Arrays.stream(keys)
                .filter(k -> resume.toLowerCase().contains(k))
                .toList();
    }

    // =========================================
    // 🧩 COMMON PARSER
    // =========================================
    private List<String> parseBullets(String res, int limit) {

        return Arrays.stream(res.split("\n"))
                .map(String::trim)
                .filter(line -> line.startsWith("-"))
                .map(line -> line.replaceFirst("^[-\\s]+", "").trim())
                .filter(s -> !s.isEmpty())
                .limit(limit)
                .toList();
    }
}