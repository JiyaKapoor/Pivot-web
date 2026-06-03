package com.example.PivotVC_Web.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "git_objects")
public class GitObject {

    public enum ObjectType {
        BLOB, TREE, COMMIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sha;

    @Column(name = "repo_id", nullable = false)
    private Long repoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjectType type;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private Long size;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public GitObject() {}

    public GitObject(String sha, Long repoId, ObjectType type, String storagePath, Long size) {
        this.sha = sha;
        this.repoId = repoId;
        this.type = type;
        this.storagePath = storagePath;
        this.size = size;
    }

    public Long getId() { return id; }
    public String getSha() { return sha; }
    public Long getRepoId() { return repoId; }
    public ObjectType getType() { return type; }
    public String getStoragePath() { return storagePath; }
    public Long getSize() { return size; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSha(String sha) { this.sha = sha; }
    public void setRepoId(Long repoId) { this.repoId = repoId; }
    public void setType(ObjectType type) { this.type = type; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public void setSize(Long size) { this.size = size; }
}