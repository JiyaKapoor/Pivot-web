package com.example.PivotVC_Web.Entities;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "staging_entries")
public class StagingEntry {

    public enum Status {
        ADDED, MODIFIED, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private GitRepository repo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "blob_sha", nullable = false)
    private String blobSha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(updatable = false)
    private LocalDateTime stagedAt;

    @PrePersist
    protected void onCreate() {
        this.stagedAt = LocalDateTime.now();
    }

    public StagingEntry() {}

    public StagingEntry(GitRepository repo, User user, String filePath, String blobSha, Status status) {
        this.repo = repo;
        this.user = user;
        this.filePath = filePath;
        this.blobSha = blobSha;
        this.status = status;
    }

    public Long getId() { return id; }
    public GitRepository getRepo() { return repo; }
    public User getUser() { return user; }
    public String getFilePath() { return filePath; }
    public String getBlobSha() { return blobSha; }
    public Status getStatus() { return status; }
    public LocalDateTime getStagedAt() { return stagedAt; }

    public void setRepo(GitRepository repo) { this.repo = repo; }
    public void setUser(User user) { this.user = user; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setBlobSha(String blobSha) { this.blobSha = blobSha; }
    public void setStatus(Status status) { this.status = status; }
}