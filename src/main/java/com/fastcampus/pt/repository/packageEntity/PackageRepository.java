package com.fastcampus.pt.repository.packageEntity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
     List<PackageEntity> findByCreatedAtAfter(LocalDateTime dateTime, Pageable packageSeq);
}
