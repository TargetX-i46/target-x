package com.targetx.auth.model.service;


import com.targetx.auth.model.entity.Device;
import com.targetx.auth.model.entity.DiskMountKey;
import com.targetx.auth.model.repository.DeviceRepository;
import com.targetx.auth.model.repository.DiskMountKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DiskMountKeyService {
    private final DiskMountKeyRepository diskMountKeyRepository;

    @Autowired
    public DiskMountKeyService(DiskMountKeyRepository diskMountKeyRepository) {
        this.diskMountKeyRepository = diskMountKeyRepository;
    }

    public Optional<DiskMountKey> get(UUID id) {
        return diskMountKeyRepository.findById(id);
    }
    public DiskMountKey save(DiskMountKey diskMountKey) {
        return diskMountKeyRepository.save(diskMountKey);
    }

}
