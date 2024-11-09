package edu.troy.pennypilot.dialog;

import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.model.ExpenseCategory;
import edu.troy.pennypilot.support.AmountStringConverter;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class BudgetDialog extends Dialog<Budget> {
    private ChoiceBox<ExpenseCategory> expenseCategory;
    private final TextFormatter<Float> formatter = new TextFormatter<>(new AmountStringConverter(), 0f, change -> change.getControlNewText().matches("\\d{0,15}(\\.\\d{0,2})?") ? change : null);

    public BudgetDialog(Budget budget, Collection<ExpenseCategory> unusedCategories) {
        setTitle("Budget dialog");
        getDialogPane().setHeaderText((budget == null ? "Add" : "Edit") + " budget");

        buildUI(budget, unusedCategories);

        // Convert result to budget when SAVE button is clicked
        setResultConverter(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                try {
                    return Budget.builder()
                            .id(budget == null ? null : budget.getId())
                            .expenseCategory(expenseCategory.getValue())
                            .amount(formatter.getValue())
                            .build();
                } catch (NumberFormatException e) {
                    getDialogPane().setHeaderText("alles is omzeep");
                }
            }
            return null;
        });
    }

    private void buildUI(Budget budget, Collection<ExpenseCategory> unusedCategories) {
        expenseCategory = new ChoiceBox<>(FXCollections.observableArrayList(unusedCategories));
        TextField amount = new TextField();
        amount.setTextFormatter(formatter);
        if (budget != null) {
            expenseCategory.setValue(budget.getExpenseCategory());
            amount.setText(String.valueOf(budget.getAmount()));
            expenseCategory.setDisable(true);
        }

        GridPane form = new GridPane(5,10);
        form.setPadding(new Insets(10));
        form.addRow(0, new Label("Category: "), expenseCategory);
        form.addRow(1, new Label("Amount: "), amount);

        getDialogPane().setContent(form);
        ButtonType save = new ButtonType("SAVE", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        getDialogPane().lookupButton(save).disableProperty().bind(
                expenseCategory.valueProperty().isNull().or(amount.textProperty().isEmpty())
        );
    }
}
