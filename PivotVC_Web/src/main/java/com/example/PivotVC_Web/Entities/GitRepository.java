package com.example.PivotVC_Web.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
public class GitRepository  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column(nullable = false)
    private String defaultBranch = "main";

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public GitRepository() {}

    public GitRepository(String name, User owner, boolean isPrivate) {
        this.name = name;
        this.owner = owner;
        this.isPrivate = isPrivate;
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    public User getOwner() { return owner; }
    public boolean isPrivate() { return isPrivate; }
    public String getDefaultBranch() { return defaultBranch; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }

    public void setOwner(User owner) { this.owner = owner; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }
}