package com.targetx.auth.model.service;


import com.targetx.auth.model.entity.DeviceKey;
import com.targetx.auth.model.repository.DeviceKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceKeyService {
    private final DeviceKeyRepository deviceKeyRepository;

    @Autowired
    public DeviceKeyService(DeviceKeyRepository deviceKeyRepository) {
        this.deviceKeyRepository = deviceKeyRepository;
    }


    public DeviceKey getNext(UUID deviceId, Integer seq) {
        return deviceKeyRepository.findByDeviceIdAndSeq(deviceId, seq);
    }
    public DeviceKey get(UUID deviceId) {
        return deviceKeyRepository.findTop1ByDeviceIdAndResponseValIsNullOrderBySeq(deviceId);
    }

    public List<DeviceKey> getAll(UUID deviceId) {
        return deviceKeyRepository.findAllByDeviceId(deviceId);
    }

    public List<DeviceKey> getAllUnused(UUID deviceId) {
        return deviceKeyRepository.findAllByDeviceIdAndResponseValIsNull(deviceId);
    }

    public DeviceKey save(DeviceKey deviceKey) {
        return deviceKeyRepository.save(deviceKey);
    }
}
