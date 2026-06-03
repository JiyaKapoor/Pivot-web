package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.Branch;
import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.RepoRepository;
import com.example.PivotVC_Web.Repository.UserRepository;
import com.example.PivotVC_Web.Services.BranchService;
import com.example.PivotVC_Web.Services.InitService;
import com.example.PivotVC_Web.Services.StagingAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/repo")
public class RepoController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    InitService initService;
    @Autowired
    RepoRepository repoRepository;
    @Autowired
    StagingAreaService stagingAreaService;
    @PostMapping("/initRepo")
    public ResponseEntity<String> initRepo(@RequestParam String repoName,@RequestParam Long userId,@RequestParam Boolean isPrivate){
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException());
        initService.initRepo(repoName,isPrivate,user);
        return ResponseEntity.ok("Repository created successfully");
    }
    @PostMapping("/stageFile")
    public ResponseEntity<String> stageFile(@RequestParam Long repoId,@RequestParam Long userId,@RequestParam String filePath,@RequestParam MultipartFile file) throws IOException {
        byte[] content=file.getBytes();
        GitRepository gitRepository=repoRepository.findById(repoId).orElseThrow(()->new RuntimeException());
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException());
        stagingAreaService.stageFile(gitRepository,user,filePath,content);
        return ResponseEntity.ok("File staged");
    }
}
