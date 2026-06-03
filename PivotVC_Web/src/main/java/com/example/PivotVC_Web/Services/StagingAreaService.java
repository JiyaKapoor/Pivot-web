package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.StagingEntry;
import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.StagingEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StagingAreaService {
    @Autowired
    private StagingEntryRepository stagingEntryRepository;
    public StagingEntry stageFile(GitRepository repo, User user,String filePath,byte[] fileContent){
        String sha=ComputeSha.computeBlobSha(fileContent);
        Optional<StagingEntry> existing = stagingEntryRepository
                .findByRepoAndUserAndFilePath(repo, user, filePath);
        if (existing.isPresent()) {
            StagingEntry entry = existing.get();
            entry.setBlobSha(sha);
            entry.setStatus(StagingEntry.Status.MODIFIED);
            return stagingEntryRepository.save(entry);
        }
        StagingEntry entry = new StagingEntry(repo, user, filePath, sha, StagingEntry.Status.ADDED);
        return stagingEntryRepository.save(entry);
    }
}
