package com.i46.management.model.service;


import com.i46.management.model.entity.OTP;
import com.i46.management.model.repository.OTPRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OTPService {
    private final OTPRepository otpRepository;

    @Autowired
    public OTPService(OTPRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Transactional
    public OTP save(OTP otp) {
        return otpRepository.save(otp);
    }

    public OTP getByKey(String key) {
        return otpRepository.findByKey(key);
    }

    @Transactional
    public void deleteByStorageId(Integer storageId) {
        otpRepository.deleteByStorageId(storageId);

    }
}
