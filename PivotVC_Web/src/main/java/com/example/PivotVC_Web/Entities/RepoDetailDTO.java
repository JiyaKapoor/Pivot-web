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

    public RepoDetailDTO() {
    }

    public RepoDetailDTO(Long repoId,
                         String repoName,
                         Boolean isPrivate,
                         List<String> branches,
                         String currentBranch,
                         List<Commit> commits,
                         List<String> fileTree) {
        this.repoId = repoId;
        this.repoName = repoName;
        this.isPrivate = isPrivate;
        this.branches = branches;
        this.currentBranch = currentBranch;
        this.commits = commits;
        this.fileTree = fileTree;
    }

    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public void setCurrentBranch(String currentBranch) {
        this.currentBranch = currentBranch;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public List<String> getFileTree() {
        return fileTree;
    }

    public void setFileTree(List<String> fileTree) {
        this.fileTree = fileTree;
    }
}
