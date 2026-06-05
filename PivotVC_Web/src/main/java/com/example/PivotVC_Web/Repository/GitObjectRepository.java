package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.GitObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitObjectRepository extends JpaRepository<GitObject, Long> {
    boolean existsByShaAndRepoId(String sha,Long repoId);
    Optional<GitObject> findByShaAndRepoId(String sha, Long repoId);
}
