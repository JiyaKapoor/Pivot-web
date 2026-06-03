package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.Branch;
import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Repository.RepoRepository;
import com.example.PivotVC_Web.Services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/branch")
public class BranchController {
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private BranchService branchService;
    @PostMapping("/create")
    public ResponseEntity<String> createBranch(@RequestParam String branchName, @RequestParam Long repoId){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()-> new RuntimeException("Repository not found"));
        branchService.createBranch(gitRepository,branchName);
        return ResponseEntity.ok("Repository created");
    }
    @GetMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestParam Long repoId,@RequestParam String branchName){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()-> new RuntimeException());
        branchService.checkOut(branchName,gitRepository);
        return ResponseEntity.ok("Switched to"+branchName);
    }
    @GetMapping("/listAll")
    public ResponseEntity<List<Branch>> listBranches(@RequestParam Long repoId){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()-> new RuntimeException());
        List<Branch> branches=branchService.listBranches(gitRepository);
        return ResponseEntity.ok(branches);
    }
}
