package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoRepository extends JpaRepository<GitRepository,Long> {
    boolean existsByOwnerAndName(User owner, String name);
    GitRepository findByOwnerAndId(User user, Long repoId);

    List<GitRepository> findByOwner(User user);
}
