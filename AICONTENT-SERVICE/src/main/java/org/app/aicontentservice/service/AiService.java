package org.app.aicontentservice.service;

import java.util.List;
import java.util.Map;

public interface AiService {

    String generateSummary(int userId, String resume, String jobDesc);

    List<String> generateBulletPoints(int userId, String exp, String jobDesc);

    String generateCoverLetter(int userId, String resume, String jobDesc);

    String improveSection(int userId, String section, String jobDesc);

    String checkAtsCompatibility(int userId, String resume);

    List<String> suggestSkills(int userId, String jobDesc);

    String tailorResumeForJob(int userId, String resume, String jobDesc);

    List<?> getAiHistory(int userId);

    int getRemainingQuota(int userId);

    Map<String, String> translateResume(int userId, String resume);
}