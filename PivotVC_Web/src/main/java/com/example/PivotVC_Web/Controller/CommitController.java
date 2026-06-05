package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.Commit;
import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.Head;
import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.CommitRepository;
import com.example.PivotVC_Web.Repository.HeadRepository;
import com.example.PivotVC_Web.Repository.RepoRepository;
import com.example.PivotVC_Web.Repository.UserRepository;
import com.example.PivotVC_Web.Services.AgentService;
import com.example.PivotVC_Web.Services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@CrossOrigin("*")
@RestController
@RequestMapping("/repo")
public class CommitController {
    @Autowired
    private CommitService commitService;
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AgentService agentService;
    @Autowired
    private HeadRepository headRepository;
    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestParam Long repoId, @RequestParam Long userId, @RequestParam String message,@RequestParam String filePath,
                                         @RequestParam MultipartFile file){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()-> new RuntimeException());
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException());
        try {
            String commitSha=commitService.commitAdd(
                    gitRepository,
                    user,
                    message,
                    filePath,
                    file.getBytes()
            );
            Head head=headRepository.findByRepoId(gitRepository.getId());
            String branchName=head.getBranchName();
            agentService.triggerAnalysis(
                    gitRepository.getId(),
                    commitSha,
                    head.getBranchName(),
                    false
            );
            agentService.indexRepository(repoId,branchName);
        } catch (Exception e) {
            throw new RuntimeException("Commit failed", e);
        }
        return ResponseEntity.ok("Commit successful");
    }
    @GetMapping("/commit-log")
    public ResponseEntity<List<Commit>> log(@RequestParam Long repoId){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()->new RuntimeException());
        List<Commit> commitLog=commitService.log(gitRepository);
        return ResponseEntity.ok(commitLog);
    }
}
