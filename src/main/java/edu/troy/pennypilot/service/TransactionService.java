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

    public Transaction getTransactionById(Long id){
        return transactionRepo.getTransactionById(id);
    }

    public Transaction getTranasctionByDescription(String description){
        return transactionRepo.findByDescription(description);
    }

    public Transaction addTransaction(Transaction newTransaction) {
        return transactionRepo.save(newTransaction);
    }

    public void deleteTransactionById(Long id) {
        transactionRepo.deleteById(id);
    }
}
