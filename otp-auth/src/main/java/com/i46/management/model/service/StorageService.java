package com.i46.management.model.service;


import com.i46.management.model.entity.Storage;
import com.i46.management.model.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class StorageService {
    private final StorageRepository storageRepository;

    @Autowired
    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    public Optional<Storage> get(Integer id) {
        return storageRepository.findById(id);
    }
    public Storage save(Storage storage) {
        return storageRepository.save(storage);
    }

    public boolean existsStorageByUserIdAndSlot(UUID userId, Integer slot) {
        return storageRepository.existsStorageByUserIdAndSlot(userId, slot);
    }

}
