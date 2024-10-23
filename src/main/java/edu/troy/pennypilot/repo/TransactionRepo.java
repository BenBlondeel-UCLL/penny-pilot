package edu.troy.pennypilot.repo;

import edu.troy.pennypilot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    Transaction getTransactionById(Long id);
    Transaction findByDescription(String description);
}
