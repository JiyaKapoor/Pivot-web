package com.example.PivotVC_Web.Entities;

import java.util.List;

public class RepoDetailDTO {
    private Long repoId;
    private String repoName;
    private Boolean isPrivate;

    private List<String> branches;
    private String currentBranch;

    private List<Commit> commits;
    private List<String> fileTree;
    public RepoDetailDTO(Long repoId, String repoName, Boolean isPrivate,
                         List<String> branches, String currentBranch,
                         List<Commit> commits, List<String> fileTree) {
        this.repoId = repoId;
        this.repoName = repoName;
        this.isPrivate = isPrivate;
        this.branches = branches;
        this.currentBranch = currentBranch;
        this.commits = commits;
        this.fileTree = fileTree;
    }
}
