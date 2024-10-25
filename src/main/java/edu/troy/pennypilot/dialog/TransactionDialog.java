package edu.troy.pennypilot.dialog;

import java.sql.Date;

import edu.troy.pennypilot.model.ExpenseCategory;
import edu.troy.pennypilot.model.IncomeCategory;
import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.model.TransactionType;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionDialog extends Dialog<Transaction> {
    private TextField description;
    private DatePicker date;
    private ChoiceBox<TransactionType> type;
    private TextField amount;
    private ChoiceBox<Enum> category;

    public TransactionDialog(Transaction transaction) {
        uiBuilder(transaction);
        setTitle("Transaction dialog");
        getDialogPane().setHeaderText("Create transaction");
        type.setOnAction(event -> {
            if (type.getValue() == TransactionType.INCOME) {
                category.setItems(FXCollections.observableArrayList(IncomeCategory.values()));
            } else if (type.getValue() == TransactionType.EXPENSE) {
                category.setItems(FXCollections.observableArrayList(ExpenseCategory.values()));
            }
        });

        // Convert result to transaction when SAVE button is clicked
        setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonData.OK_DONE) {
                try {
                    if(category.getValue().getClass() == IncomeCategory.class) {
                        return Transaction.builder().id(transaction==null ? null : transaction.getId()).description(description.getText()).date(date.getValue()).type(type.getValue()).amount(Float.parseFloat(amount.getText())).incomeCategory((IncomeCategory)category.getValue()).build();
                    } else {
                        return Transaction.builder().id(transaction==null ? null : transaction.getId()).description(description.getText()).date(date.getValue()).type(type.getValue()).amount(Float.parseFloat(amount.getText())).expenseCategory((ExpenseCategory)category.getValue()).build();
                    }
                } catch (NumberFormatException e) {
                    getDialogPane().setHeaderText("alles is omzeep");
                }
            }
            return null;
        });
    }

    private void uiBuilder(Transaction transaction) {
        description = new TextField();
        date = new DatePicker();
        type = new ChoiceBox<>(FXCollections.observableArrayList(TransactionType.values())); 
        amount = new TextField();
        category = new ChoiceBox<>();
        if (transaction != null) {
            description.setText(transaction.getDescription());
            date.setValue(transaction.getDate());
            type.setValue(transaction.getType());
            amount.setText(String.valueOf(transaction.getAmount()));
            if (transaction.getType() == TransactionType.INCOME) {
                category.setItems(FXCollections.observableArrayList(IncomeCategory.values()));
                category.setValue(transaction.getIncomeCategory());
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                category.setItems(FXCollections.observableArrayList(ExpenseCategory.values()));
                category.setValue(transaction.getExpenseCategory());
            }
        }
        VBox vBox = new VBox(new Label("Date: "), date, new Label("Income Type: "), type, new Label("Amount: "), amount, new Label("Description: "), description, new Label("Category: "), category);
        getDialogPane().setContent(vBox);
        getDialogPane().getButtonTypes().addAll(new ButtonType("SAVE", ButtonData.OK_DONE), ButtonType.CANCEL);
    }
}
