package com.app.repository;

import com.app.entity.ResumeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<ResumeTemplate, Long> {

    Optional<ResumeTemplate> findByTemplateId(Long id);

    List<ResumeTemplate> findByCategory(String category);

    List<ResumeTemplate> findByIsPremium(boolean isPremium);

    List<ResumeTemplate> findByIsActive(boolean isActive);

    List<ResumeTemplate> findAllByOrderByUsageCountDesc();

    int countByCategory(String category);
}