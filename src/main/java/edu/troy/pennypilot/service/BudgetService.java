package edu.troy.pennypilot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.repo.BudgetRepo;

@Service
@Transactional
public class BudgetService {
    @Autowired
    private BudgetRepo budgetRepo;
    
    public List<Budget> getAllBudgets() {
        return budgetRepo.findAll();
    }

    public Budget addBudget(Budget newBudget) {
        if (budgetRepo.findByExpenseCategory(newBudget.getExpenseCategory()) != null) {
            throw new IllegalArgumentException("Budget already exists for this category");
        }
        return budgetRepo.save(newBudget);
    }

    public Budget updateBudget(long id, float amount) {
        Budget budget = budgetRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Budget not found for id: " + id));
        budget.setAmount(amount);
        return budget;
    }

    public void deleteBudgetById(long id) {
        budgetRepo.deleteById(id);
    }
}
