package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.model.Budget;
import edu.troy.pennypilot.model.Transaction;
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
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class BudgetTile extends BorderPane {

    public BudgetTile(Budget budget, List<Transaction> expenses) {
        setPrefHeight(250);
        setPrefWidth(250);
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setTop(buttonBar());
        setCenter(chart(budget, expenses));
    }

    Node chart(Budget budget, List<Transaction> expenses) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Goal (" + budget.getAmount() + ')', budget.getAmount()));
        float sum = expenses.stream()
                .filter(e -> e.getExpenseCategory() == budget.getExpenseCategory())
                .map(Transaction::getAmount)
                .reduce(0.f, Float::sum);
        series.getData().add(new XYChart.Data<>("Actual (" + sum + ')'  , sum));

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
        Button delete = new Button("", new FontIcon(FontAwesomeSolid.TRASH));

        HBox buttonBar = new HBox(edit, delete);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getStyleClass().add("budget-button-bar");

        return buttonBar;
    }
}
