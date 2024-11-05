package edu.troy.pennypilot.ui;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import edu.troy.pennypilot.model.Budget;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class BudgetTile extends BorderPane{

    public BudgetTile(Budget budget, Float totalExpensesThisMonthFromCategory) {
        float progress = totalExpensesThisMonthFromCategory / budget.getAmount();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        BarChart<String,Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setTitle(budget.getExpenseCategory().toString());
        if (progress > 1) {
        //     setStyle("-fx-background-color: rgba(255, 0, 0, 0.3);"); // Light red background
            bc.lookup(".default-color1.chart-bar;").setStyle("-fx-bar-fill: rgba(255, 0, 0, 0.3);"); // Light red background
        } else {
        //     setStyle("-fx-background-color: rgba(0, 255, 0, 0.3);"); // Light green background
            bc.setStyle("-fx-background-color: rgba(0, 255, 0, 0.3);"); // Light green background
        }
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Goal"); 
        series1.getData().add(new XYChart.Data("Goal", budget.getAmount()));
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Spent");
        series2.getData().add(new XYChart.Data("Spent", totalExpensesThisMonthFromCategory));
        bc.getData().addAll(series1, series2); 
        bc.getStyleClass().add("chart");
        setCenter(bc);
        HBox buttonBar = new HBox(new Button("", new FontIcon(FontAwesomeSolid.EDIT)), new Button("", new FontIcon(FontAwesomeSolid.TRASH)));
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        setTop(buttonBar);
        setPrefHeight(150);
        setPrefWidth(150);
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}
