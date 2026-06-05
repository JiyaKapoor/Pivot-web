package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.AgentAnalysis;
import com.example.PivotVC_Web.Repository.AgentAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AgentService {
    @Autowired
    AgentAnalysisRepository agentAnalysisRepository;

    @Async
    public void triggerAnalysis(Long repoId, String commitSha, String branchName, boolean mergeEvent) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> body = Map.of(
                    "repo_id", repoId.toString(),
                    "commit_sha", commitSha,
                    "branch_name", branchName,
                    "merge_event", mergeEvent
            );
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8000/analyze",
                    body,
                    Map.class
            );
            Map<String, Object> result = response.getBody();
            if (result != null) {
                // save to DB
                AgentAnalysis analysis = new AgentAnalysis();
                analysis.setRepoId(repoId);
                analysis.setCommitSha(commitSha);
                analysis.setBranchName(branchName);
                analysis.setCodeReview((String) result.get("code_review"));
                analysis.setSuggestedCommitMessage((String) result.get("suggested_commit_message"));
                analysis.setBranchSummary((String) result.get("branch_summary"));
                analysis.setSafetyReport((String) result.get("safety_report"));
                agentAnalysisRepository.save(analysis);
            }
        } catch (Exception e) {
            System.err.println("Agent analysis failed: " + e.getMessage());
        }
    }
}