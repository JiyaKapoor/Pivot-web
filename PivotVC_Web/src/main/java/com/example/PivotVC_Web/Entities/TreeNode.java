package com.example.PivotVC_Web.Entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Entity
@Table(name = "tree_nodes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sha", "repo_id"})
})
public class TreeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sha;

    @Column(name = "repo_id", nullable = false)
    private Long repoId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tree_node_id", nullable = false)
    private List<TreeEntry> entries = new ArrayList<>();

    public TreeNode() {}

    public TreeNode(Long repoId,List<TreeEntry> entries) {
        this.entries = entries;
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
    }

    public void removeEntry(TreeEntry entry) {
        entries.remove(entry);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        for (TreeEntry entry : this.getEntries()) {
            map.put(entry.getName(), entry.getSha()); // name is already the full path
        }
        return map;
    }
}