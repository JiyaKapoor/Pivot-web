package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.StagingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagingEntryRepository extends JpaRepository<StagingEntry, Long> {
}
