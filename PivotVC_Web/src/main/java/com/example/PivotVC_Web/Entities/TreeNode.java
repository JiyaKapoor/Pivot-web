package com.example.PivotVC_Web.Entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tree_nodes")
public class TreeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sha;

    @Column(name = "repo_id", nullable = false)
    private Long repoId;

    @OneToMany(mappedBy = "treeNode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreeEntry> entries = new ArrayList<>();

    public TreeNode() {}

    public TreeNode(String sha, Long repoId) {
        this.sha = sha;
        this.repoId = repoId;
    }

    public Long getId() { return id; }
    public String getSha() { return sha; }
    public Long getRepoId() { return repoId; }
    public List<TreeEntry> getEntries() { return entries; }

    public void setSha(String sha) { this.sha = sha; }
    public void setRepoId(Long repoId) { this.repoId = repoId; }
    public void setEntries(List<TreeEntry> entries) { this.entries = entries; }

    public void addEntry(TreeEntry entry) {
        entries.add(entry);
        entry.setTreeNode(this);
    }

    public void removeEntry(TreeEntry entry) {
        entries.remove(entry);
        entry.setTreeNode(null);
    }
}