package com.i46.management.model.repository;

import com.i46.management.model.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {
    boolean existsStorageByUserIdAndSlot(UUID userId, Integer slot);
    
}