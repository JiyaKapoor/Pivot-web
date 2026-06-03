package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoRepository extends JpaRepository<GitRepository,Long> {
}
