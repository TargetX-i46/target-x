package com.targetx.auth.model.repository;

import com.targetx.auth.model.entity.DiskMountKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface DiskMountKeyRepository extends JpaRepository<DiskMountKey, UUID> {
}