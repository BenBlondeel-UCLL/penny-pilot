package edu.troy.pennypilot.repo;

import edu.troy.pennypilot.model.ExpenseCategory;
import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.model.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    Transaction getTransactionById(Long id);
    Optional<Transaction> findByDescription(String description);
    List<Transaction> findAllByType(TransactionType income);

    
    List<Transaction> findByTypeAndDateBetween(
        @Param("type") TransactionType type, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );

    List<Transaction> findByExpenseCategoryAndDateBetween(
        @Param("category") ExpenseCategory category, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
}
