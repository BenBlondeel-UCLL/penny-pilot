package edu.troy.pennypilot.dialog;

import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.model.ExpenseCategory;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BudgetDialog extends Dialog<Budget> {
    private ChoiceBox<ExpenseCategory> expenseCategory;
    private TextField amount;

    public BudgetDialog(Budget budget) {
        uiBuilder(budget);
        setTitle("Budget dialog");
        getDialogPane().setHeaderText("Create budget");

        // Convert result to budget when SAVE button is clicked
        setResultConverter(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                try {
                    return Budget.builder().id(budget == null ? null : budget.getId()).expenseCategory(expenseCategory.getValue()).amount(Float.parseFloat(amount.getText())).build();
                } catch (NumberFormatException e) {
                    getDialogPane().setHeaderText("alles is omzeep");
                }
            }
            return null;
        });
    }

    private void uiBuilder(Budget budget) {
        expenseCategory = new ChoiceBox<>(FXCollections.observableArrayList(ExpenseCategory.values()));
        amount = new TextField();

        getDialogPane().setContent(new VBox(new Label("Category: "), expenseCategory, new Label("Amount: "), amount));
        getDialogPane().getButtonTypes().addAll(new ButtonType("SAVE", ButtonData.OK_DONE), ButtonType.CANCEL);
    }
}
