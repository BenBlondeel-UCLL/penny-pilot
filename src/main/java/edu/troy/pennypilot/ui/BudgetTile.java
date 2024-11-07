package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.dialog.BudgetDialog;
import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.service.BudgetService;
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
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

@Slf4j
public class BudgetTile extends BorderPane {
    private Budget budget;
    private List<Transaction> expenses;
    private BudgetService budgetService;

    public BudgetTile(Budget budget, List<Transaction> expenses, BudgetService budgetService) {
        this.expenses = expenses;
        this.budgetService = budgetService;

        setPrefHeight(250);
        setPrefWidth(250);
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        updateBudget(budget);
        setTop(buttonBar());
    }

    void updateBudget(Budget budget) {
        this.budget = budget;
        setCenter(chart());
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
        edit.setOnAction(e -> new BudgetDialog(budget).showAndWait().ifPresent(response -> {
                Budget budget = budgetService.updateBudget(response.getId(), response.getAmount());
                updateBudget(budget);
            })
        );

        Button delete = new Button("", new FontIcon(FontAwesomeSolid.TRASH));   
        delete.setOnAction(e -> {
            budgetService.deleteBudgetById(budget.getId());
            ((Pane) getParent()).getChildren().remove(this);
        });

        HBox buttonBar = new HBox(edit, delete);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getStyleClass().add("budget-button-bar");

        return buttonBar;
    }
}
