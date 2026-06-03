package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

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
            Branch branch=branchRepository.findByName(branchName);
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
            Branch branch=branchRepository.findByName(branchName);
            branch.setHeadCommitSha(newCommitSha);
            branchRepository.save(branch);
        }
        stagingEntryRepository.deleteAll(currIdx);
    }
    public List<Commit> log(GitRepository gitRepository){
        //prints the commit history of whichever brnach we are on
        Head head=headRepository.findByRepoId(gitRepository.getId());
        String currCommit;
        if(head.getRefType()==RefType.DETACHED){
            currCommit=head.getCommitSha();
        }
        else{
            String branchName=head.getBranchName();
            Branch branch=branchRepository.findByName(branchName);
            currCommit=branch.getHeadCommitSha();
        }
        if(currCommit == null){
            return Collections.emptyList();
        }
        Queue<String> q=new LinkedList<>();
        Set<String> vis=new HashSet<>();
        List<Commit> commitLog=new ArrayList<>();
        q.add(currCommit);
        vis.add(currCommit);
        while(!q.isEmpty()){
            String latestCommit=q.poll();
            Commit commit=commitRepository.findByCommitSha(latestCommit);
            commitLog.add(commit);
            String parentSha=commit.getParentSha();
            if(parentSha != null && !vis.contains(parentSha)){
                q.add(parentSha);
                vis.add(parentSha);
            }
            if(commit.isMergeCommit()){
                String secondParent=commit.getSecondParentSha();
                if(!vis.contains(secondParent)){
                    q.add(secondParent);
                    vis.add(secondParent);
                }
            }
        }
        return commitLog;
    }
}

