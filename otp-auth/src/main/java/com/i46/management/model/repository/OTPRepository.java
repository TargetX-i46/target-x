package com.i46.management.model.repository;

import com.i46.management.model.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {
    OTP findByKey(String key);

    void deleteByStorageId(Integer storageId);
}