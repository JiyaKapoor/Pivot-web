package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.BranchRepository;
import com.example.PivotVC_Web.Repository.CommitRepository;
import com.example.PivotVC_Web.Repository.RepoRepository;
import com.example.PivotVC_Web.Repository.UserRepository;
import com.example.PivotVC_Web.Services.BranchService;
import com.example.PivotVC_Web.Services.InitService;
import com.example.PivotVC_Web.Services.StagingAreaService;
import com.example.PivotVC_Web.Services.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@CrossOrigin("*")
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
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    CommitRepository commitRepository;
    @Autowired
    TreeService treeService;
    @PostMapping("/initRepo")
    public ResponseEntity<String> initRepo(@RequestParam String repoName,@RequestParam Long userId,@RequestParam Boolean isPrivate){
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException());
        initService.initRepo(repoName,isPrivate,user);
        return ResponseEntity.ok("Repository created successfully");
    }
    @GetMapping("/listRepos")
    public ResponseEntity<List<GitRepository>> listRepositories(@RequestParam Long userId){
        User user=userRepository.findById(userId).orElseThrow();
        List<GitRepository> currRepos=repoRepository.findByOwner(user);
        return ResponseEntity.ok(currRepos);
    }
    @GetMapping("/repo-detail")
    public ResponseEntity<RepoDetailDTO> fetchDetail(
            @RequestParam Long repoId,
            @RequestParam Long userId,
            @RequestParam(required = false) String branchName) {

        System.out.println("========== REPO DETAIL ==========");
        System.out.println("repoId = " + repoId);
        System.out.println("userId = " + userId);
        System.out.println("requested branch = " + branchName);

        User user = userRepository.findById(userId).orElseThrow();

        System.out.println("user found = " + user.getUsername());

        GitRepository repo = repoRepository.findByOwnerAndId(user, repoId);

        System.out.println("repo found = " + repo.getName());

        List<Branch> branches = branchRepository.findByRepo(repo);

        System.out.println("branch count = " + branches.size());

        branches.forEach(b ->
                System.out.println(
                        "branch -> name=" + b.getName() +
                                " head=" + b.getHeadCommitSha()
                )
        );

        List<String> branchNames = branches.stream()
                .map(Branch::getName)
                .toList();

        String currentBranch =
                (branchName != null) ? branchName : repo.getDefaultBranch();

        System.out.println("currentBranch = " + currentBranch);

        Branch branch = branchRepository.findByRepoAndName(repo, currentBranch);

        System.out.println("branch object = " + branch);

        if (branch != null) {
            System.out.println("headCommitSha = " + branch.getHeadCommitSha());
        }

        List<Commit> commits = new ArrayList<>();

        Commit curr = commitRepository.findBySha(branch.getHeadCommitSha());

        while (curr != null) {
            System.out.println(
                    "commit -> sha=" + curr.getSha() +
                            " parent=" + curr.getParentSha() +
                            " msg=" + curr.getMessage()
            );

            commits.add(curr);

            curr = commitRepository.findBySha(curr.getParentSha());
        }

        System.out.println("total commits = " + commits.size());

        List<String> fileTree = new ArrayList<>();

        Commit headCommit =
                commitRepository.findBySha(branch.getHeadCommitSha());

        System.out.println("headCommit = " + headCommit);

        if (headCommit != null) {

            System.out.println("treeSha = " + headCommit.getTreeSha());

            HashMap<String, String> files = new HashMap<>();

            treeService.flattenTree(
                    headCommit.getTreeSha(),
                    "",
                    files
            );

            System.out.println("flattened files = " + files);

            fileTree = new ArrayList<>(files.keySet());
        }

        System.out.println("fileTree size = " + fileTree.size());
        System.out.println("fileTree = " + fileTree);

        RepoDetailDTO dto = new RepoDetailDTO(
                repoId,
                repo.getName(),
                repo.isPrivate(),
                branchNames,
                currentBranch,
                commits,
                fileTree
        );



        return ResponseEntity.ok(dto);
    }
}
