package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    GitObjectRepository gitObjectRepository;
    @Autowired
    SupabaseStorageService supabaseStorageService;
    @Transactional
    public String commitAdd(GitRepository gitRepository,User user,String message,String filePath,byte[] fileContent){
        Head head=headRepository.findByRepoId(gitRepository.getId());
        String commitSha;
        if(head.getRefType()==RefType.DETACHED)commitSha=head.getCommitSha();
        else{
            String branchName=head.getBranchName();
            Branch branch=branchRepository.findByRepoAndName(gitRepository,branchName);
            commitSha=branch.getHeadCommitSha();
        }
        String blobSha=ComputeSha.computeBlobSha(fileContent);
        // Add this after computing blobSha
        if (!gitObjectRepository.existsBySha(blobSha)) {
            String blobPath = "repos/" + gitRepository.getId() + "/blob/"
                    +blobSha;
            GitObject blobObject = new GitObject(blobSha, gitRepository.getId(),
                    GitObject.ObjectType.BLOB, blobPath, (long) fileContent.length);
            gitObjectRepository.save(blobObject);
            supabaseStorageService.upload(blobPath, fileContent);
        }
        Set<TreeEntry> entries=treeService.buildTreeAdd(commitSha,filePath,blobSha);
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
        System.out.println("entries count before save: " + entries.size());
        entries.forEach(e -> System.out.println("  -> " + e.getName() + " | " + e.getSha()));
        treeNodeRepository.save(node);
        String treePath="repos/"+gitRepository.getId()+"/trees/"+treeSha;
        Long size=(long)sb.toString().getBytes().length;
        GitObject treeObject=new GitObject(treeSha,gitRepository.getId(),GitObject.ObjectType.TREE,treePath,size);
        gitObjectRepository.save(treeObject);
        supabaseStorageService.upload(treePath,sb.toString().getBytes());
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
                treeSha + "\n" +
                        commitSha + "\n" +
                        message + "\n" +
                        user.getUsername() + "\n" +
                        timestamp;

        String newCommitSha =
                ComputeSha.computeBlobSha(commitContent.getBytes());

        commit.setSha(newCommitSha);
        commitRepository.save(commit);
        Long commitSize=(long)commitContent.getBytes().length;
        String commitStoragePath="repos/"+gitRepository.getId()+"/commit/"+newCommitSha;
        GitObject commitObject=new GitObject(newCommitSha, gitRepository.getId(), GitObject.ObjectType.COMMIT,commitStoragePath,commitSize);
        gitObjectRepository.save(commitObject);
        supabaseStorageService.upload(commitStoragePath,commitContent.getBytes());
        if(head.getRefType()==RefType.DETACHED){
            head.setCommitSha(newCommitSha);
            headRepository.save(head);
        }
        else{
            String branchName=head.getBranchName();
            Branch branch=branchRepository.findByRepoAndName(gitRepository,branchName);
            branch.setHeadCommitSha(newCommitSha);
            branchRepository.save(branch);
        }
        return newCommitSha;
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
            Commit commit=commitRepository.findBySha(latestCommit);
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

