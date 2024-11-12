package edu.troy.pennypilot.budget.ui;

import edu.troy.pennypilot.budget.persistence.Budget;

public interface BudgetListener {
    void budgetAdded(Budget budget);
    void budgetUpdated(Budget budget);
    void budgetDeleted(Budget budget);
}
