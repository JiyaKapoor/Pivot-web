package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.AgentAnalysis;
import com.example.PivotVC_Web.Entities.FileIndexDTO;
import com.example.PivotVC_Web.Repository.AgentAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentService {
    @Autowired
    AgentAnalysisRepository agentAnalysisRepository;
    @Autowired
    TreeService treeService;
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
                AgentAnalysis analysis = new AgentAnalysis();
                analysis.setRepoId(repoId);
                analysis.setCommitSha(commitSha);
                analysis.setBranchName(branchName);
                analysis.setMergeEvent(mergeEvent);  // track this for context
                if(!mergeEvent)analysis.setCodeReview((String) result.get("code_review"));

                // only set these for merge events
                if (mergeEvent) {
                    analysis.setSafetyReport((String) result.get("safety_report"));
                }

                agentAnalysisRepository.save(analysis);
            }
        } catch (Exception e) {
            System.err.println("Agent analysis failed: " + e.getMessage());
        }
    }
    public String runMergeSafetyCheck(
            Long repoId,
            String sourceBranch,
            String targetBranch
    ) {

        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> sourceFiles = treeService.loadBranches(repoId, sourceBranch);
        Map<String, String> targetFiles = treeService.loadBranches(repoId, targetBranch);

        Map<String,Object> body = Map.of(
                "repo_id", repoId.toString(),
                "source_branch", sourceBranch,
                "target_branch", targetBranch,
                "source_files", sourceFiles,
                "target_files", targetFiles
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "http://localhost:8000/analyze-merge",
                        body,
                        Map.class
                );

        return (String) response.getBody().get("safety_report");
    }
    public void indexRepository(
            Long repoId,
            String branchName
    ) {

        List<FileIndexDTO> files =
                treeService.loadBranchFiles(
                        repoId,
                        branchName
                );

        Map<String,Object> body = new HashMap<>();

        body.put("repo_id", repoId.toString());
        body.put("files", files);
        body.put("branchName",branchName);

        RestTemplate restTemplate =
                new RestTemplate();

        restTemplate.postForEntity(
                "http://localhost:8000/index-repo",
                body,
                Map.class
        );
    }
}