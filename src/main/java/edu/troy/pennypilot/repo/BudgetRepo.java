package edu.troy.pennypilot.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.model.ExpenseCategory;


public interface BudgetRepo extends JpaRepository<Budget, Long>{
    Budget findByExpenseCategory(ExpenseCategory expenseCategory);
}
