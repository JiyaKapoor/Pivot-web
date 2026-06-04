package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.Commit;
import com.example.PivotVC_Web.Services.MergeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/repo")
public class MergeController {
    @Autowired
    private MergeService mergeService;
    @PostMapping("/merge")
    public ResponseEntity<String> mergeRequest(@RequestParam Long userId,@RequestParam Long repoId,@RequestParam String branchNameA,@RequestParam String branchNameB) throws IOException {
        mergeService.threeWayMerge(branchNameA,branchNameB,repoId,userId);
        return ResponseEntity.ok("merge successful");
    }
}
