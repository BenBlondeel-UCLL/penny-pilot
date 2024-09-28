package edu.troy.pennypilot.service;

import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public List<Transaction> getAllTransactions(){
        return transactionRepo.findAll();
    }

    public Transaction addTransaction(Transaction newTransaction) {
        return transactionRepo.save(newTransaction);
    }
}
