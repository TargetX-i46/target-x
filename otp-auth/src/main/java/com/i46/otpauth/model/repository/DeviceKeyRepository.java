package com.i46.otpauth.model.repository;

import com.i46.otpauth.model.entity.DeviceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeviceKeyRepository extends JpaRepository<DeviceKey, Integer> {
    Boolean existsByDeviceId(String deviceId);
    DeviceKey findByDeviceIdAndSeq(String deviceId, Integer seq);
    DeviceKey findTop1ByDeviceIdAndResponseValIsNullOrderBySeq(String deviceId);
    List<DeviceKey> findAllByDeviceIdAndResponseValIsNull(String deviceId);
}