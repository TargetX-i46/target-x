package com.i46.otpauth.model.service;


import com.i46.otpauth.model.entity.DeviceKey;
import com.i46.otpauth.model.repository.DeviceKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceKeyService {
    private final DeviceKeyRepository deviceKeyRepository;

    @Autowired
    public DeviceKeyService(DeviceKeyRepository deviceKeyRepository) {
        this.deviceKeyRepository = deviceKeyRepository;
    }

    public Boolean existsByDeviceId(String deviceId) {
        return deviceKeyRepository.existsByDeviceId(deviceId);
    }
    public DeviceKey getNext(String deviceId, Integer seq) {
        return deviceKeyRepository.findByDeviceIdAndSeq(deviceId, seq);
    }
    public DeviceKey get(String deviceId) {
        return deviceKeyRepository.findTop1ByDeviceIdAndResponseValIsNullOrderBySeq(deviceId);
    }

    public List<DeviceKey> getAll(String deviceId) {
        return deviceKeyRepository.findAllByDeviceIdAndResponseValIsNull(deviceId);
    }

    public DeviceKey save(DeviceKey deviceKey) {
        return deviceKeyRepository.save(deviceKey);
    }
}
