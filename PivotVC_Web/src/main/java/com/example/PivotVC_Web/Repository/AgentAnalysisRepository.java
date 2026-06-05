package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.AgentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentAnalysisRepository extends JpaRepository<AgentAnalysis,Long> {
    Optional<AgentAnalysis> findTopByCommitShaOrderByCreatedAtDesc(String commitSha);
    List<AgentAnalysis> findByRepoIdOrderByCreatedAtDesc(Long repoId);
}
