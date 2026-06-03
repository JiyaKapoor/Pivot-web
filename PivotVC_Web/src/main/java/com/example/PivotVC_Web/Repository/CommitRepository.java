package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends JpaRepository<Commit,Long> {
    Commit findBySha(String commitSha);
}
