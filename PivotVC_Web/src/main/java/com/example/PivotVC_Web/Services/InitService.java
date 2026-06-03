package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.Branch;
import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.Head;
import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.BranchRepository;
import com.example.PivotVC_Web.Repository.HeadRepository;
import com.example.PivotVC_Web.Repository.RepoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InitService {
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private HeadRepository headRepository;

    @Transactional
    public GitRepository initRepo(String repoName, boolean isPrivate, User owner) {

        if (repoRepository.existsByOwnerAndName(owner, repoName)) {
            throw new IllegalArgumentException("Repository '" + repoName + "' already exists for user " + owner.getUsername());
        }
        GitRepository repo = new GitRepository(repoName, owner, isPrivate);
        repoRepository.save(repo);

        Branch mainBranch = new Branch();
        mainBranch.setRepo(repo);
        mainBranch.setName("main");
        mainBranch.setHeadCommitSha(null);
        branchRepository.save(mainBranch);

        Head head = new Head(repo.getId(), "main");
        headRepository.save(head);

        return repo;
    }
}
