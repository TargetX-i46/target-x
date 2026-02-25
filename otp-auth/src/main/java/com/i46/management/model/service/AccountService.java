package com.i46.management.model.service;


import com.i46.management.model.entity.Account;
import com.i46.management.model.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public boolean isAppIdExists(String appId) {
        return accountRepository.isAppIdExists(appId);
    }

    public Optional<Account> getByAppId(String appId) {
        return accountRepository.findByAppId(appId);
    }

    public Optional<Account> getById(UUID id) {
        return accountRepository.findById(id);
    }

    @Transactional
    public Account save(Account account, int initCredit) {
        return accountRepository.save(account, initCredit);
    }

}
