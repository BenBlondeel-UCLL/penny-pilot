package edu.troy.pennyPilot.repo;

import edu.troy.pennyPilot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    public Optional<Transaction> findBillingById(Long id);
}
