package com.targetx.auth.model.repository;

import com.targetx.auth.model.entity.Device;
import com.targetx.auth.model.entity.DeviceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Boolean existsByDeviceName(String deviceName);
    Device findByDeviceName(String deviceName);
}