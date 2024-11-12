package edu.troy.pennypilot.dialog;

import edu.troy.pennypilot.transaction.persistence.ExpenseCategory;
import edu.troy.pennypilot.transaction.persistence.IncomeCategory;
import edu.troy.pennypilot.transaction.persistence.Transaction;
import edu.troy.pennypilot.transaction.persistence.TransactionType;
import edu.troy.pennypilot.support.AmountStringConverter;
import edu.troy.pennypilot.support.BindingUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionDialog extends Dialog<Transaction> {
    private TextField description;
    private DatePicker date;
    private RadioButton income;
    private ChoiceBox<Enum> category;
    private TextFormatter<Float> formatter = new TextFormatter<>(new AmountStringConverter(), 0f, change -> change.getControlNewText().matches("\\d{0,15}(\\.\\d{0,2})?") ? change : null);

    public TransactionDialog(Transaction transaction) {
        setTitle("Transaction dialog");
        getDialogPane().setHeaderText((transaction == null ? "Add" : "Edit") + " transaction");
        buildUi(transaction);

        // Convert result to transaction when SAVE button is clicked
        setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonData.OK_DONE) {
                try {
                    var builder = Transaction.builder()
                            .id(transaction == null ? null : transaction.getId())
                            .description(description.getText())
                            .date(date.getValue())
                            .type(income.isSelected() ? TransactionType.INCOME : TransactionType.EXPENSE)
                            .amount(formatter.getValue());

                    if (income.isSelected()) {
                        builder.incomeCategory((IncomeCategory) category.getValue());
                    } else {
                        builder.expenseCategory((ExpenseCategory) category.getValue());
                    }

                    return builder.build();
                } catch (NumberFormatException e) {
                    getDialogPane().setHeaderText("alles is omzeep");
                }
            }
            return null;
        });
    }

    private void buildUi(Transaction transaction) {
        description = new TextField();
        date = new DatePicker();
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            category.setItems(FXCollections.observableArrayList(income.isSelected() ? IncomeCategory.values() : ExpenseCategory.values()));
            if (transaction != null) {
                category.setValue(income.isSelected() ? transaction.getIncomeCategory() : transaction.getExpenseCategory());
            }
        });
        income = new RadioButton("Income");
        income.setToggleGroup(toggleGroup);
        RadioButton expense = new RadioButton("Expense");
        expense.setToggleGroup(toggleGroup);
        TextField amount = new TextField();
        amount.setTextFormatter(formatter);
        category = new ChoiceBox<>();
        category.minWidthProperty().bind(description.widthProperty());

        if (transaction != null) {
            description.setText(transaction.getDescription());
            date.setValue(transaction.getDate());
            amount.setText(String.valueOf(transaction.getAmount()));
            if (transaction.getType() == TransactionType.INCOME) {
                income.setSelected(true);
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                expense.setSelected(true);
            }
        } else {
            expense.setSelected(true);
        }

        GridPane form = new GridPane(5,10);
        form.setPadding(new Insets(10));
        form.addRow(0, new Label("Date: "), date);
        form.addRow(1, new Label("Type: "), income, expense);
        form.addRow(2, new Label("Amount: "), amount);
        form.addRow(3, new Label("Description: "), description);
        form.addRow(4, new Label("Category: "), category);

        GridPane.setColumnSpan(date, 2);
        GridPane.setColumnSpan(amount, 2);
        GridPane.setColumnSpan(description, 2);
        GridPane.setColumnSpan(category, 2);

        getDialogPane().setContent(form);
        ButtonType save = new ButtonType("SAVE", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        getDialogPane().lookupButton(save).disableProperty().bind(
                date.valueProperty().isNull()
                        .or(BindingUtil.isBlank(description.textProperty()))
                        .or(category.valueProperty().isNull())
        );
    }
}
