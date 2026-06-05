package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.QueryResponse;
import com.example.PivotVC_Web.Services.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/copilot")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @PostMapping("/ask")
    public ResponseEntity<QueryResponse> ask(
            @RequestParam Long repoId,
            @RequestParam String branchName,
            @RequestParam String question
    ) {
        QueryResponse response = queryService.ask(repoId, branchName, question);
        return ResponseEntity.ok(response);
    }
}