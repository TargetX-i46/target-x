package com.i46.management.model.service;


import com.i46.management.model.entity.Transaction;
import com.i46.management.model.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction save(Transaction transaction, int availableCredit) {
        return transactionRepository.save(transaction, availableCredit);
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }


}
