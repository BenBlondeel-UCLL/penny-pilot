package edu.troy.pennypilot.budget.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.troy.pennypilot.transaction.persistence.ExpenseCategory;


public interface BudgetRepo extends JpaRepository<Budget, Long>{
    Budget findByExpenseCategory(ExpenseCategory expenseCategory);
}
