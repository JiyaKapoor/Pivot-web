package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Long> {
    Branch findByBranchName(String branchName);
}
