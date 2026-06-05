package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Repository.AgentAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repo")
public class AgentController {
    @Autowired
    private AgentAnalysisRepository agentAnalysisRepository;
    @GetMapping("/analysis")
    public ResponseEntity<?> getAnalysis(@RequestParam String commitSha) {
        return agentAnalysisRepository
                .findTopByCommitShaOrderByCreatedAtDesc(commitSha)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
