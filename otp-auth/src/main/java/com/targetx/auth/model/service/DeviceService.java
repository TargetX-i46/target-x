package com.targetx.auth.model.service;


import com.targetx.auth.model.entity.Device;
import com.targetx.auth.model.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> get(UUID id) {
        return deviceRepository.findById(id);
    }
    public List<Device> get() {
        return deviceRepository.findAll();
    }
    public Boolean existsByDeviceName(String deviceId) {
        return deviceRepository.existsByDeviceName(deviceId);
    }
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

}
