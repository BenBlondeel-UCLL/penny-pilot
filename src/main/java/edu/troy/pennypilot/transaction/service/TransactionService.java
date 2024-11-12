package edu.troy.pennypilot.transaction.service;

import edu.troy.pennypilot.transaction.persistence.ExpenseCategory;
import edu.troy.pennypilot.transaction.persistence.Transaction;
import edu.troy.pennypilot.transaction.persistence.TransactionType;
import edu.troy.pennypilot.transaction.persistence.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    public List<Transaction> getAllIncomeTransactions() {
        return transactionRepo.findAllByType(TransactionType.INCOME);
    }

    public float getTotalThisMonth(TransactionType type){
        return transactionRepo.findByTypeAndDateBetween(type, LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
                .stream()
                .map(Transaction::getAmount)
                .reduce(0.0f, Float::sum);
    }

    public float getTotalExpensesThisMonthFromCategory(ExpenseCategory category) {
        return transactionRepo.findByExpenseCategoryAndDateBetween(category, LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
                .stream()
                .map(Transaction::getAmount)
                .reduce(0.0f, Float::sum);
    }

    public List<Transaction> getAllExpenseTransactions() {
        return transactionRepo.findAllByType(TransactionType.EXPENSE);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepo.getTransactionById(id);
    }

    public Optional<Transaction> getTransactionByDescription(String description) {
        return transactionRepo.findByDescription(description);
    }

    public Transaction addTransaction(Transaction newTransaction) {
        return transactionRepo.save(newTransaction);
    }

    public void deleteTransactionById(Long id) {
        transactionRepo.deleteById(id);
    }
}
