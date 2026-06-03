package com.example.PivotVC_Web.Entities;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.sql.Ref;
@Entity
public class Head {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefType refType;
    @Column(name = "repo_id", nullable = false, unique = true)
    private Long repoId;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "commit_sha")
    private String commitSha; //in case it is detached
    public Head(){

    }
    public Head(Long repoId,String branchName){
        this.repoId=repoId;
        this.branchName=branchName;
        this.refType=RefType.BRANCH;
    }
    public Head(Long repoId,String commitSha,RefType refType){
        this.repoId=repoId;
        this.commitSha=commitSha;
        this.refType= RefType.DETACHED;
    }
    public Long getId() { return id; }
    public Long getRepo() { return repoId; }
    public RefType getRefType() { return refType; }
    public String getBranchName() { return branchName; }
    public String getCommitSha() { return commitSha; }

    public void setRepo(Long repoId) { this.repoId = repoId; }
    public void setRefType(RefType refType) { this.refType = refType; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }
}
