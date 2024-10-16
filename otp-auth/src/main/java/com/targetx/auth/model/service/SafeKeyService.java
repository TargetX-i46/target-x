package com.targetx.auth.model.service;


import com.targetx.auth.model.entity.SafeKey;
import com.targetx.auth.model.repository.SafeKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SafeKeyService {
    private final SafeKeyRepository safeKeyRepository;

    @Autowired
    public SafeKeyService(SafeKeyRepository diskMountKeyRepository) {
        this.safeKeyRepository = diskMountKeyRepository;
    }

    public Optional<SafeKey> get(UUID id) {
        return safeKeyRepository.findById(id);
    }
    public SafeKey save(SafeKey diskMountKey) {
        return safeKeyRepository.save(diskMountKey);
    }

}
