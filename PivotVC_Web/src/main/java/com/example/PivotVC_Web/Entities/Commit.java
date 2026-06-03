package com.example.PivotVC_Web.Entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commits")
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sha;

    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private GitRepository repo;


    @Column(name = "tree_sha", nullable = false)
    private String treeSha;

    @Column(name = "parent_sha")
    private String parentSha;

    @Column(name = "second_parent_sha")
    private String secondParentSha;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String message;

    @Column(updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public Commit() {}

    public Commit(GitRepository repo, String treeSha, String parentSha,String secondParentSha, User author, String message) {
        this.repo = repo;
        this.treeSha = treeSha;
        this.parentSha = parentSha;
        this.secondParentSha=secondParentSha;
        this.author = author;
        this.message = message;

    }

    public Long getId() { return id; }
    public String getSha() { return sha; }
    public GitRepository getRepo() { return repo; }
    public String getTreeSha() { return treeSha; }
    public String getParentSha() { return parentSha; }
    public String setSecondParentSha(){return secondParentSha;}
    public User getAuthor() { return author; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isMergeCommit() {
        return secondParentSha != null;
    }
    public void setSha(String sha) { this.sha = sha; }
    public void setRepo(GitRepository repo) { this.repo = repo; }
    public void setTreeSha(String treeSha) { this.treeSha = treeSha; }
    public void setParentSha(String parentSha) { this.parentSha = parentSha; }
    public void setAuthor(User author) { this.author = author; }
    public void setMessage(String message) { this.message = message; }


    public String getSecondParentSha() {
        return this.secondParentSha;
    }
}