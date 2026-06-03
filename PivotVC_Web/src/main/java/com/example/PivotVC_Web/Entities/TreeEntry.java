package com.example.PivotVC_Web.Entities;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tree_entries")
public class TreeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tree_node_id", nullable = false)
    private TreeNode treeNode;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    @Column(nullable = false)
    private String sha;

    public TreeEntry() {}

    public TreeEntry(String name, EntryType type, String sha) {
        this.name = name;
        this.type = type;
        this.sha = sha;
    }

    public Long getId() { return id; }
    public TreeNode getTreeNode() { return treeNode; }
    public String getName() { return name; }
    public EntryType getType() { return type; }
    public String getSha() { return sha; }

    public void setTreeNode(TreeNode treeNode) { this.treeNode = treeNode; }
    public void setName(String name) { this.name = name; }
    public void setType(EntryType type) { this.type = type; }
    public void setSha(String sha) { this.sha = sha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeEntry)) return false;

        TreeEntry other = (TreeEntry) o;

        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}