package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
@Service
public class CommitService {
    @Autowired
    StagingEntryRepository stagingEntryRepository;
    @Autowired
    HeadRepository headRepository;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    TreeService treeService;
    @Autowired
    TreeNodeRepository treeNodeRepository;
    @Autowired
    CommitRepository commitRepository;
    public void commit(GitRepository gitRepository, User user,String message){
        List<StagingEntry> currIdx=stagingEntryRepository.findByRepoAndUser(gitRepository,user);
        //now we need to track the files which were already present in the branch
        Head head=headRepository.findByRepoId(gitRepository.getId());
        String commitSha;
        if(head.getRefType()==RefType.DETACHED)commitSha=head.getCommitSha();
        else{
            String branchName=head.getBranchName();
            Branch branch=branchRepository.findByBranchName(branchName);
            commitSha=branch.getHeadCommitSha();
        }
        //now we load the current tree using this commitSha
        Set<TreeEntry> entries=treeService.buildTree(commitSha,currIdx);
        TreeNode node=new TreeNode(gitRepository.getId(),entries.stream().toList());
        StringBuilder sb = new StringBuilder();

        node.getEntries().stream()
                .sorted(Comparator.comparing(TreeEntry::getName))
                .forEach(entry -> {
                    sb.append(entry.getType())
                            .append(" ")
                            .append(entry.getSha())
                            .append(" ")
                            .append(entry.getName())
                            .append("\n");
                });

        String treeSha = ComputeSha.computeBlobSha(sb.toString().getBytes());
        node.setSha(treeSha);
        treeNodeRepository.save(node);
        LocalDateTime timestamp = LocalDateTime.now();

        Commit commit = new Commit(
                gitRepository,
                treeSha,
                commitSha,
                null,
                user,
                message
        );

        String commitContent =
                treeSha +
                        commitSha +
                        message +
                        user.getUsername() +
                        timestamp;

        String newCommitSha =
                ComputeSha.computeBlobSha(commitContent.getBytes());

        commit.setCommitSha(newCommitSha);
        commitRepository.save(commit);
        if(head.getRefType()==RefType.DETACHED){
            head.setCommitSha(newCommitSha);
            headRepository.save(head);
        }
        else{
            String branchName=head.getBranchName();
            Branch branch=branchRepository.findByBranchName(branchName);
            branch.setHeadCommitSha(newCommitSha);
            branchRepository.save(branch);
        }
        stagingEntryRepository.deleteAll(currIdx);
    }
}
