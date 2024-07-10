package com.i46.otpauth.model.repository;

import com.i46.otpauth.model.entity.DeviceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface DeviceKeyRepository extends JpaRepository<DeviceKey, Integer> {

    DeviceKey findByDeviceId(@Param("deviceId") String deviceId);
}