package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.TreeNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeNodeRepository extends JpaRepository<TreeNode,Long> {
    TreeNode findBySha(String treeSha);
}
