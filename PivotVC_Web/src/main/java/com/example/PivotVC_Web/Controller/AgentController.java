package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.AgentAnalysis;
import com.example.PivotVC_Web.Repository.AgentAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/repo")
public class AgentController {
    @Autowired
    private AgentAnalysisRepository agentAnalysisRepository;
    @GetMapping("/analysis")
    public ResponseEntity<?> getAnalysis(@RequestParam String commitSha) {

        System.out.println("QUERY SHA = [" + commitSha + "]");

        Optional<AgentAnalysis> result =
                agentAnalysisRepository
                        .findTopByCommitShaOrderByCreatedAtDesc(commitSha);

        System.out.println("FOUND = " + result.isPresent());

        if(result.isPresent()) {
            System.out.println("DB SHA = [" + result.get().getCommitSha() + "]");
        }

        return result
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
