package com.i46.management.model.service;


import com.i46.management.model.entity.Account;
import com.i46.management.model.repository.ContactRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional
    public Account save(Account account, UUID contactAccountId) {
        return contactRepository.save(account, contactAccountId);
    }
}
