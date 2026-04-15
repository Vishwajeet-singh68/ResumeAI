package org.app.aicontentservice.service;

import lombok.RequiredArgsConstructor;
import org.app.aicontentservice.client.GeminiClient;
import org.app.aicontentservice.entity.AiRequest;
import org.app.aicontentservice.repository.AiRequestRepository;
import org.app.aicontentservice.util.GeminiParser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final GeminiClient geminiClient;
    private final AiRequestRepository repo;

    // 🔥 CORE PROCESS METHOD (COMMON FOR ALL AI CALLS)
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

            String result = GeminiParser.getText(raw);
            int tokens = GeminiParser.getTokens(raw);

            req.setAiResponse(result);
            req.setTokensUsed(tokens);
            req.setModel("GEMINI");
            req.setStatus("COMPLETED");
            req.setCompletedAt(LocalDateTime.now());

            repo.save(req);

            return result;

        } catch (Exception e) {
            req.setStatus("FAILED");
            repo.save(req);
            e.printStackTrace();
            throw new RuntimeException("AI failed: " + e.getMessage());
        }
    }

    // 🔒 QUOTA CHECK
    private void validateQuota(int userId) {
        if (repo.countByUserIdToday(userId) > 50) {
            throw new RuntimeException("Daily limit exceeded");
        }
    }

    // 🧠 SUMMARY
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

    // 📌 BULLET POINTS
    @Override
    public List<String> generateBulletPoints(int userId, String exp, String jobDesc) {

        String res = process(userId, "BULLETS", """
        Convert the experience into 5 strong resume bullet points.

        Format:
        - point 1
        - point 2
        - point 3
        - point 4
        - point 5

        Experience:
        %s
        """.formatted(exp));

        return Arrays.stream(res.split("\n"))
                .filter(line -> line.trim().startsWith("-"))
                .map(line -> line.replaceFirst("-", "").trim())
                .toList();
    }

    // 📄 COVER LETTER
    @Override
    public String generateCoverLetter(int userId, String resume, String jobDesc) {
        return process(userId, "COVER_LETTER", """
        Write a professional cover letter (150-200 words).

        Resume:
        %s

        Job Description:
        %s
        """.formatted(resume, jobDesc));
    }

    // ✨ IMPROVE SECTION
    @Override
    public String improveSection(int userId, String section, String jobDesc) {
        return process(userId, "IMPROVE", """
        Rewrite this section using strong action verbs and impact.

        Return only improved version.

        Section:
        %s
        """.formatted(section));
    }

    // 📊 ATS CHECK
    @Override
    public String checkAtsCompatibility(int userId, String resume) {
        return process(userId, "ATS", """
        Analyze resume for ATS compatibility.

        Format:
        Score: <0-100>
        Strengths:
        - ...
        Weaknesses:
        - ...
        Suggestions:
        - ...

        Resume:
        %s
        """.formatted(resume));
    }

    // 🧠 SKILLS
    @Override
    public List<String> suggestSkills(int userId, String jobDesc) {

        String res = process(userId, "SKILLS", """
        Extract top 10 relevant skills.

        Return ONLY comma separated values.

        Job Description:
        %s
        """.formatted(jobDesc));

        return Arrays.stream(res.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // 🎯 TAILOR RESUME
    @Override
    public String tailorResumeForJob(int userId, String resume, String jobDesc) {
        return process(userId, "TAILOR", """
        Tailor this resume for the job.

        Keep structure same but improve content relevance.

        Resume:
        %s

        Job Description:
        %s
        """.formatted(resume, jobDesc));
    }

    // 🌍 TRANSLATE
    @Override
    public Map<String, String> translateResume(int userId, String resume) {

        String res = process(userId, "TRANSLATE", """
        Translate this resume into simple professional English.

        Resume:
        %s
        """.formatted(resume));

        return Map.of("translated", res);
    }

    // 📜 HISTORY
    @Override
    public List<?> getAiHistory(int userId) {
        return repo.findByUserId(userId);
    }

    // 📊 QUOTA
    @Override
    public int getRemainingQuota(int userId) {
        return 50 - repo.countByUserIdToday(userId);
    }
}