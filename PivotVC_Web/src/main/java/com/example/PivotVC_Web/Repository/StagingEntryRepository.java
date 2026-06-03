package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.StagingEntry;
import com.example.PivotVC_Web.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StagingEntryRepository extends JpaRepository<StagingEntry, Long> {
    Optional<StagingEntry> findByRepoAndUserAndFilePath(GitRepository repo, User user, String filePath);
}
