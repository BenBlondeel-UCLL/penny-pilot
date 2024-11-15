package edu.troy.pennypilot.transaction.ui;

import edu.troy.pennypilot.dialog.TransactionDialog;
import edu.troy.pennypilot.transaction.persistence.Transaction;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TransactionView extends GridPane {

    private final TransactionModel  model;
    private final List<TransactionListener> listeners = new ArrayList<>();

    TransactionView(TransactionModel model) {
        super(10,5);
        this.model = model;

        buildUI();
    }

    void buildUI() {
        Button create = new Button("Add", new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
        create.setOnAction(actionEvent -> new TransactionDialog(null).showAndWait().ifPresent(response -> {
            listeners.forEach(listener -> listener.transactionAdded(response));
         }));

        ListView<Transaction> incomeListView = new ListView<>(model.getIncomeList());
        incomeListView.setCellFactory(lv -> new TransactionListCell());
        incomeListView.setContextMenu(buildContextMenu(incomeListView));

        ListView<Transaction> expenseListView = new ListView<>(model.getExpenselist());
        expenseListView.setCellFactory(lv -> new TransactionListCell());
        expenseListView.setContextMenu(buildContextMenu(expenseListView));

        setPadding(new Insets(10));
        addRow(0, new Label("Income"), new Label("Expenses"));
        addRow(1, incomeListView, expenseListView);
        addRow(2, create);

        GridPane.setHgrow(incomeListView, Priority.ALWAYS);
        GridPane.setVgrow(incomeListView, Priority.ALWAYS);
        GridPane.setHgrow(expenseListView, Priority.ALWAYS);
        GridPane.setVgrow(expenseListView, Priority.ALWAYS);
    }

    ContextMenu buildContextMenu(ListView<Transaction> lv) {
        var edit = new MenuItem("Edit Transaction", new FontIcon(FontAwesomeSolid.EDIT));
        edit.setOnAction(event -> {
            Transaction selected = lv.getSelectionModel().getSelectedItem();
            if (selected != null) {
                log.info("Edit: {}", selected);
                new TransactionDialog(selected).showAndWait().ifPresent(response -> {
                    listeners.forEach(listener -> listener.transactionUpdated(selected, response));
                });
            }
        });

        var delete = new MenuItem("Delete Transaction", new FontIcon(FontAwesomeSolid.TRASH));
        delete.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this transaction?", ButtonType.NO, ButtonType.YES);
            alert.setHeaderText("Delete Transaction");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    Transaction selected = lv.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        listeners.forEach(listener -> listener.transactionDeleted(selected));
                    }
                }
            });
        });

        return new ContextMenu(edit, delete);
    }

    public void addTransactionListener(TransactionListener listener) {
        listeners.add(listener);
    }
}

