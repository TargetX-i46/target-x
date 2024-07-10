package com.i46.otpauth.model.service;


import com.i46.otpauth.model.entity.DeviceKey;
import com.i46.otpauth.model.repository.DeviceKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceKeyService {
    private final DeviceKeyRepository deviceKeyRepository;

    @Autowired
    public DeviceKeyService(DeviceKeyRepository deviceKeyRepository) {
        this.deviceKeyRepository = deviceKeyRepository;
    }

    public DeviceKey get(String deviceId) {
        return deviceKeyRepository.findByDeviceId(deviceId);
    }
    public DeviceKey save(DeviceKey deviceKey) {
        return deviceKeyRepository.save(deviceKey);
    }
}
