package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.Commit;
import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.CommitRepository;
import com.example.PivotVC_Web.Repository.RepoRepository;
import com.example.PivotVC_Web.Repository.UserRepository;
import com.example.PivotVC_Web.Services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repo")
public class CommitController {
    @Autowired
    private CommitService commitService;
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestParam Long repoId, @RequestParam Long userId, @RequestParam String message){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()-> new RuntimeException());
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException());
        commitService.commit(gitRepository,user,message);
        return ResponseEntity.ok("Commit succesful");
    }
    @GetMapping("/commit-log")
    public ResponseEntity<List<Commit>> log(@RequestParam Long repoId){
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()->new RuntimeException());
        List<Commit> commitLog=commitService.log(gitRepository);
        return ResponseEntity.ok(commitLog);
    }
}
