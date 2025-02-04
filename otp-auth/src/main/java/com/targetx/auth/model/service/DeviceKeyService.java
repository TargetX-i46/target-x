package com.targetx.auth.model.service;


import com.targetx.auth.model.entity.DeviceKey;
import com.targetx.auth.model.repository.DeviceKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public DeviceKey getExists(UUID deviceId, String key) {
        return deviceKeyRepository.findByDeviceIdAndKeyVal(deviceId, key);
    }


    public List<DeviceKey> getAll(UUID deviceId) {
        return deviceKeyRepository.findAllByDeviceIdOrderBySeq(deviceId);
    }

    public List<DeviceKey> getAllUnused(UUID deviceId) {
        return deviceKeyRepository.findAllByDeviceIdAndResponseValIsNull(deviceId);
    }

    public void reset(UUID deviceId) {

        List<DeviceKey> keys = deviceKeyRepository.findAllByDeviceIdOrderBySeq(deviceId);
        keys = keys.stream().filter(k -> k.getResponseVal() != null).toList();
        for (DeviceKey key : keys){
            key.setResponseVal(null);
            deviceKeyRepository.save(key);
        }
    }

    public DeviceKey save(DeviceKey deviceKey) {
        return deviceKeyRepository.save(deviceKey);
    }
}
