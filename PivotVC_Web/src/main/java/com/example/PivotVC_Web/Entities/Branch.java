package com.example.PivotVC_Web.Entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repo;

    @Column(nullable = false)
    private String name;

    @Column(name = "head_commit_sha", nullable = false)
    private String headCommitSha;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Branch() {}

    public Branch(Repository repo, String name, String headCommitSha) {
        this.repo = repo;
        this.name = name;
        this.headCommitSha = headCommitSha;
    }

    public Long getId() { return id; }
    public Repository getRepo() { return repo; }
    public String getName() { return name; }
    public String getHeadCommitSha() { return headCommitSha; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void setRepo(Repository repo) { this.repo = repo; }
    public void setHeadCommitSha(String headCommitSha) { this.headCommitSha = headCommitSha; }
}