package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.Head;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadRepository extends JpaRepository<Head,Long>{
    Head findByRepoId(Long repoId);
}
