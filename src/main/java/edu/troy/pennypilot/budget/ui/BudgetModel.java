package edu.troy.pennypilot.budget.ui;

import edu.troy.pennypilot.budget.persistence.Budget;
import edu.troy.pennypilot.transaction.persistence.ExpenseCategory;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.EnumSet;

@Getter
public class BudgetModel {

    private final ObservableList<Budget> budgetList;
    private final EnumSet<ExpenseCategory> unusedCategories;

    BudgetModel() {
        budgetList = FXCollections.observableArrayList();
        unusedCategories = EnumSet.allOf(ExpenseCategory.class);

        budgetList.addListener((ListChangeListener<Budget>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().stream()
                            .map(Budget::getExpenseCategory)
                            .forEach(unusedCategories::remove);
                } else if (change.wasRemoved()) {
                    change.getRemoved().stream()
                            .map(Budget::getExpenseCategory)
                            .forEach(unusedCategories::add);
                }
            }
        });
    }

}
