package com.example.PivotVC_Web.Entities;

public class QueryRequest {
    private Long repoId;
    private String branchName;
    private String question;

    public QueryRequest() {}

    public QueryRequest(Long repoId, String branchName, String question) {
        this.repoId = repoId;
        this.branchName = branchName;
        this.question = question;
    }

    public Long getRepoId() { return repoId; }
    public void setRepoId(Long repoId) { this.repoId = repoId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}