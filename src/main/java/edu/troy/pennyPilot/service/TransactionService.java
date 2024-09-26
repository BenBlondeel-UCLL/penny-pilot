package edu.troy.pennyPilot.service;

import edu.troy.pennyPilot.model.Transaction;
import edu.troy.pennyPilot.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionService {
    @Autowired
    TransactionRepo transactionRepo;

    public List<Transaction> getAllTransactions(){
        return transactionRepo.findAll();
    }

    public void addTransaction(Transaction newTransaction) {
        transactionRepo.save(newTransaction);
    }
}
