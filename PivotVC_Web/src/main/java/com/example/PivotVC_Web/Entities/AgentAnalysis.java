package com.example.PivotVC_Web.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AgentAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long repoId;
    private String commitSha;
    private String branchName;

    @Column(columnDefinition = "TEXT")
    private String codeReview;

    private boolean isMergeEvent;

    @Column(columnDefinition = "TEXT")
    private String branchSummary;

    @Column(columnDefinition = "TEXT")
    private String safetyReport;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public String getCommitSha() { return commitSha; }
    public String getBranchName() { return branchName; }
    public String getCodeReview() { return codeReview; }
    public String getBranchSummary() { return branchSummary; }
    public String getSafetyReport() { return safetyReport; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setRepoId(Long repoId) { this.repoId = repoId; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public void setCodeReview(String codeReview) { this.codeReview = codeReview; }
    public void setBranchSummary(String branchSummary) { this.branchSummary = branchSummary; }
    public void setSafetyReport(String safetyReport) { this.safetyReport = safetyReport; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setMergeEvent(boolean isMergeEvent){ this.isMergeEvent=isMergeEvent;}
    public boolean getMergeEvent(){return this.isMergeEvent;}
}