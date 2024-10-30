package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.model.Budget;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BudgetTile extends BorderPane{

    public BudgetTile(Budget budget) {
        setCenter(new Text(budget.getExpenseCategory() + ": " + budget.getAmount()));
        setPrefHeight(100);
        setPrefWidth(100);
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}
