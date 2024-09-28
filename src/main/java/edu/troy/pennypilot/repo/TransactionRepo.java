package edu.troy.pennypilot.repo;

import edu.troy.pennypilot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
}
