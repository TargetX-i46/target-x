package com.i46.management.model.service;


import com.i46.management.model.repository.CreditRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class CreditService {
    private final CreditRepository creditRepository;

    @Autowired
    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    @Transactional
    public int getCredit(UUID id) {
        return creditRepository.findCreditById(id);
    }

}
