package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.repo.BudgetRepo;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class BudgetTile extends BorderPane {
    private Budget budget;
    private List<Transaction> expenses;

    public BudgetTile(Budget budget, List<Transaction> expenses, Consumer<BudgetTile> deletAction, Consumer<BudgetTile> editAction) {
        this.budget = budget;
        this.expenses = expenses;
        setPrefHeight(250);
        setPrefWidth(250);
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setTop(buttonBar(deletAction, editAction));
        setBudget(budget);
    }

    Node chart(Budget budget, List<Transaction> expenses) {
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

    Parent buttonBar(Consumer<BudgetTile> deletActionCon, Consumer<BudgetTile> editAction) {
        Button edit = new Button("", new FontIcon(FontAwesomeSolid.EDIT));
        edit.setOnAction(e -> editAction.accept(this));

        Button delete = new Button("", new FontIcon(FontAwesomeSolid.TRASH));   
        delete.setOnAction(e -> deletActionCon.accept(this));

        HBox buttonBar = new HBox(edit, delete);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getStyleClass().add("budget-button-bar");

        return buttonBar;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
        setCenter(chart(budget, expenses));
    }
}
