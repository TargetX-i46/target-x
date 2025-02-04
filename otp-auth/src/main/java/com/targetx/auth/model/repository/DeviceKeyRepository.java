package com.targetx.auth.model.repository;

import com.targetx.auth.model.entity.DeviceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface DeviceKeyRepository extends JpaRepository<DeviceKey, Integer> {
    DeviceKey findByDeviceIdAndSeq(UUID deviceId, Integer seq);
    DeviceKey findTop1ByDeviceIdAndResponseValIsNullOrderBySeq(UUID deviceId);
    DeviceKey findByDeviceIdAndKeyVal(UUID deviceId, String keyVal);
    List<DeviceKey> findAllByDeviceIdAndResponseValIsNull(UUID deviceId);

    List<DeviceKey> findAllByDeviceIdOrderBySeq(UUID deviceId);
}