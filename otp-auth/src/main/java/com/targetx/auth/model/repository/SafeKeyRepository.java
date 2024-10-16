package com.targetx.auth.model.repository;

import com.targetx.auth.model.entity.SafeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface SafeKeyRepository extends JpaRepository<SafeKey, UUID> {
}