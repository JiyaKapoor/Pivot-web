package com.example.PivotVC_Web.Repository;

import com.example.PivotVC_Web.Entities.GitObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitObjectRepository extends JpaRepository<GitObject, Long> {
}
