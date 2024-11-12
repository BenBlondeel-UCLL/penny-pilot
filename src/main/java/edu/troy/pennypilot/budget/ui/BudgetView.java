package edu.troy.pennypilot.budget.ui;

import edu.troy.pennypilot.budget.persistence.Budget;
import edu.troy.pennypilot.dialog.BudgetDialog;
import edu.troy.pennypilot.transaction.ui.TransactionModel;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BudgetView extends BorderPane {

    private final BudgetModel model;
    private final TransactionModel transactionModel;
    private final List<BudgetListener> listeners = new ArrayList<>();

    BudgetView(BudgetModel model, TransactionModel transactionModel) {
        this.model = model;
        this.transactionModel = transactionModel;

        buildUI();
    }

    void buildUI() {

        Button add = new Button("Add", new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
        add.setStyle("-fx-background-radius: 2em;");
        add.setOnAction(actionEvent -> new BudgetDialog(null, model.getUnusedCategories()).showAndWait().ifPresent(response -> {
            listeners.forEach(listener -> listener.budgetAdded(response));
        }));

        FlowPane tiles = new FlowPane();
        model.getBudgetList().addListener((ListChangeListener<Budget>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().stream()
                            .forEach(b -> tiles.getChildren().add(new BudgetTile(b, transactionModel.getExpenselist(), listeners)));
                }
                add.setDisable(model.getUnusedCategories().isEmpty());
            }
        });

        setPadding(new Insets(5));
        setTop(add);
        setCenter(tiles);
        setMargin(add, new Insets(5));
    }


    public void addBudgetListener(BudgetListener listener) {
        listeners.add(listener);
    }
}

