package edu.troy.pennypilot.budget.ui;

import edu.troy.pennypilot.budget.persistence.Budget;
import edu.troy.pennypilot.budget.service.BudgetService;
import edu.troy.pennypilot.transaction.ui.TransactionModel;

public class BudgetController implements BudgetListener {

    private final BudgetService service;
    private final BudgetModel model;
    private final BudgetView view;

    public BudgetController(BudgetService service, TransactionModel transactionModel) {
        this.service = service;
        model = new BudgetModel();
        view = new BudgetView(model, transactionModel);

        view.addBudgetListener(this);
    }

    @Override
    public void budgetAdded(Budget budget) {
        Budget saved = service.addBudget(budget);
        model.getBudgetList().add(saved);
    }

    @Override
    public void budgetUpdated(Budget budget) {
        service.updateBudget(budget.getId(), budget.getAmount());
    }

    @Override
    public void budgetDeleted(Budget budget) {
        service.deleteBudgetById(budget.getId());
        model.getBudgetList().remove(budget);
    }

    public void loadBudgets() {
        service.getAllBudgets()
                .forEach(model.getBudgetList()::add);
    }

    public BudgetModel getModel() {
        return model;
    }

    public BudgetView getView() {
        return view;
    }
}
