package edu.troy.pennypilot.budget.ui;

import edu.troy.pennypilot.budget.persistence.Budget;
import edu.troy.pennypilot.dialog.BudgetDialog;
import edu.troy.pennypilot.transaction.persistence.Transaction;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

@Slf4j
public class BudgetTile extends BorderPane {
    @Getter
    private final Budget budget;
    private final FilteredList<Transaction> expenses;
    private final List<BudgetListener> listeners;

    public BudgetTile(Budget budget, FilteredList<Transaction> expenses, List<BudgetListener> listeners) {
        this.budget = budget;
        this.expenses = expenses;
        this.listeners = listeners;

        setPrefHeight(250);
        setPrefWidth(250);
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setTop(buttonBar());
        updateBudget();

        updateIfExpensesChanges();
    }

    void updateBudget() {
        setCenter(chart());
    }

    void updateIfExpensesChanges() {
        this.expenses.addListener((ListChangeListener<Transaction>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    updateChangedExpenses(change.getAddedSubList());
                } else if (change.wasRemoved()) {
                    updateChangedExpenses(change.getRemoved());
                }
            }
        });
    }

    void updateChangedExpenses(List<? extends Transaction> transactions) {
        transactions.stream()
                .map(Transaction::getExpenseCategory)
                .filter(category -> category == budget.getExpenseCategory())
                .findAny()
                .ifPresent(c -> updateBudget());
    }

    Node chart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Budget (" + budget.getAmount() + ')', budget.getAmount()));
        float sum = expenses.stream()
                .filter(e -> e.getExpenseCategory() == budget.getExpenseCategory())
                .map(Transaction::getAmount)
                .reduce(0.f, Float::sum);
        series.getData().add(new XYChart.Data<>("Spent (" + sum + ')'  , sum));

        var categoryAxis = new CategoryAxis();
        var valueAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(categoryAxis, valueAxis);
        chart.setTitle(budget.getExpenseCategory().name());
        chart.setLegendVisible(false);

        chart.getData().add(series);

        if (sum > budget.getAmount()) {
            chart.lookupAll(".data1.chart-bar")
                    .forEach(n -> n.setStyle("-fx-bar-fill: red;"));
        }

        return chart;
    }

    Parent buttonBar() {
        Button edit = new Button("", new FontIcon(FontAwesomeSolid.EDIT));
        edit.setOnAction(e -> new BudgetDialog(budget, List.of(budget.getExpenseCategory())).showAndWait().ifPresent(response -> {
            listeners.forEach(listener -> listener.budgetUpdated(response));
            updateBudget();
        }));

        Button delete = new Button("", new FontIcon(FontAwesomeSolid.TRASH));   
        delete.setOnAction(e -> {
            listeners.forEach(listener -> listener.budgetDeleted(budget));
            ((Pane) getParent()).getChildren().remove(this);
        });

        HBox buttonBar = new HBox(edit, delete);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getStyleClass().add("budget-button-bar");

        return buttonBar;
    }
}
