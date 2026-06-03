package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.Branch;
import com.example.PivotVC_Web.Entities.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Long> {
    Branch findByName(String branchName);

    List<Branch> findByRepo(GitRepository repo);
}
