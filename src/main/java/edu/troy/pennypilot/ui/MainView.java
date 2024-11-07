package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.JavaFxApplication;
import edu.troy.pennypilot.dialog.BudgetDialog;
import edu.troy.pennypilot.dialog.TransactionDialog;
import edu.troy.pennypilot.model.*;
import edu.troy.pennypilot.service.BudgetService;
import edu.troy.pennypilot.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MainView {

    private final TransactionService transactionService;
    private final BudgetService budgetService;

    @EventListener
    void onStageReady(JavaFxApplication.StageReadyEvent event) {
        Scene scene = new Scene(getRoot(), 800, 600);
        scene.getStylesheets().add("/stylesheet.css");

        Stage stage = event.getStage();
        stage.setTitle("Penny Pilot");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/money.jpg")));
        stage.show();
    }

    Parent getRoot() {
        
        
        return new TabPane(homeTab(), transactionTab(), budgetTab(), statisticsTab());
    }

    Tab homeTab() {
        Text text = new Text();
        text.setId("income");
        var homePane = new BorderPane(text);
        homePane.getStyleClass().add("home");

        // Home Tab
        Tab homeTab = new Tab("Home", homePane);
        homeTab.setGraphic(new FontIcon(FontAwesomeSolid.HOME));
        homeTab.setClosable(false);
        homeTab.setOnSelectionChanged(event -> {
            if (homeTab.isSelected()) {
                text.setText("PENNY PILOT"); //+ transactionService.getTotalThisMonth(TransactionType.INCOME));
            }
        });

        return homeTab;
    }

    Tab transactionTab() {
        // Transaction Tab
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactionService.getAllTransactions());
        FilteredList<Transaction> incomeTransactionList = new FilteredList<>(transactionList, transaction -> transaction.getType() == TransactionType.INCOME);
        FilteredList<Transaction> expenseTransactionList = new FilteredList<>(transactionList, transaction -> transaction.getType() == TransactionType.EXPENSE);

        // Create Transaction
        Button create = new Button("Add", new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
        create.setOnAction(actionEvent -> new TransactionDialog(null).showAndWait().ifPresent(response -> {
             log.info("Transaction: {}", response);
             transactionService.addTransaction(response);
             transactionList.add(response);
         }));

        ListView<Transaction> incomeListView = new ListView<>(incomeTransactionList);
        incomeListView.setContextMenu(buildContextMenu(incomeListView, transactionList));
        ListView<Transaction> expenseListView = new ListView<>(expenseTransactionList);
        expenseListView.setContextMenu(buildContextMenu(expenseListView, transactionList));

        GridPane pane = new GridPane(10,5);
        pane.setPadding(new Insets(10));
        pane.addRow(0, incomeListView, expenseListView);
        pane.addRow(1, create);

        GridPane.setHgrow(incomeListView, Priority.ALWAYS);
        GridPane.setVgrow(incomeListView, Priority.ALWAYS);
        GridPane.setHgrow(expenseListView, Priority.ALWAYS);
        GridPane.setVgrow(expenseListView, Priority.ALWAYS);

        Tab transactionTab = new Tab("Transactions", pane);
        transactionTab.setClosable(false);
        transactionTab.setGraphic(new FontIcon(FontAwesomeSolid.MONEY_BILL));

        return transactionTab;
    }

    Tab budgetTab(){
        FlowPane tiles = new FlowPane();
        Button add = new Button("Add", new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
        add.setStyle("-fx-background-radius: 2em;");
        add.setOnAction(actionEvent -> new BudgetDialog(null).showAndWait().ifPresent(response -> {
            log.info("Budget: {}", response);
            Budget budget = budgetService.addBudget(response);
            tiles.getChildren().add(new BudgetTile(budget, transactionService.getAllExpenseTransactions(), budgetService));
        }));

        BorderPane budgetPane = new BorderPane(tiles);
        budgetPane.setPadding(new Insets(5));
        budgetPane.setTop(add);
        BorderPane.setMargin(add, new Insets(5));
        Tab budgetTab = new Tab("Budget", budgetPane);
        budgetTab.setOnSelectionChanged(event -> {
            if (budgetTab.isSelected()) {
                tiles.getChildren().clear();
                List<Transaction> expenses = transactionService.getAllExpenseTransactions();
                budgetService.getAllBudgets().forEach(budget ->
                        tiles.getChildren().add(new BudgetTile(budget, expenses, budgetService))
                );
            }
        });
        budgetTab.setClosable(false);
        budgetTab.setGraphic(new FontIcon(FontAwesomeSolid.COINS));

        return budgetTab;
    }

    Tab statisticsTab() {
        // Statistics Tab
        Tab statisticsTab = new Tab("Statistics");
        statisticsTab.setClosable(false);
        statisticsTab.setGraphic(new FontIcon(FontAwesomeSolid.CHART_PIE));
        statisticsTab.setOnSelectionChanged(event -> {
            if (statisticsTab.isSelected()) {
                // income pie chart
                ObservableList<PieChart.Data> incomePieChartData = FXCollections.observableArrayList();
                List<Transaction> IncomeTransactions = transactionService.getAllIncomeTransactions();
                double totalIncome = IncomeTransactions.stream().mapToDouble(Transaction::getAmount).sum();
                for (IncomeCategory category : IncomeCategory.values()) {
                    double categoryTotal = IncomeTransactions.stream()
                            .filter(transaction -> transaction.getIncomeCategory() == category)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    if (categoryTotal != 0) {
                        double percentage = (Math.abs(categoryTotal) / Math.abs(totalIncome)) * 100;
                        incomePieChartData.add(new PieChart.Data(category.name() + ": " + String.format("%.2f", percentage) + "%", Math.abs(categoryTotal)));
                    }
                }
                final PieChart incomeChart = new PieChart(incomePieChartData);
                incomeChart.setTitle("Income");
                incomeChart.setLabelsVisible(false);

                // expense pie chart
                ObservableList<PieChart.Data> expensePieChartData = FXCollections.observableArrayList();
                List<Transaction> Expensetransactions = transactionService.getAllExpenseTransactions();
                double totalExpenses = Expensetransactions.stream().mapToDouble(Transaction::getAmount).sum();
                for (ExpenseCategory category : ExpenseCategory.values()) {
                    double categoryTotal = Expensetransactions.stream()
                            .filter(transaction -> transaction.getExpenseCategory() == category)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    if (categoryTotal != 0) {
                        double percentage = (Math.abs(categoryTotal) / Math.abs(totalExpenses)) * 100;
                        expensePieChartData.add(new PieChart.Data(category.name() + ": " + String.format("%.2f", percentage) + "%", Math.abs(categoryTotal)));
                    }
                }
                final PieChart expenseChart = new PieChart(expensePieChartData);
                expenseChart.setTitle("Expenses");
                expenseChart.setLabelsVisible(false);

                // add to grid pane
                GridPane pane = new GridPane();
                pane.setPadding(new Insets(10));
                pane.addRow(0, incomeChart, expenseChart);
                GridPane.setHgrow(incomeChart, Priority.ALWAYS);
                GridPane.setVgrow(incomeChart, Priority.ALWAYS);
                GridPane.setHgrow(expenseChart, Priority.ALWAYS);
                GridPane.setVgrow(expenseChart, Priority.ALWAYS);
                statisticsTab.setContent(pane);
            }
        });

        return statisticsTab;
    }

    ContextMenu buildContextMenu(ListView<Transaction> lv, ObservableList<Transaction> transactionList) {
        var edit = new MenuItem("Edit Transaction", new FontIcon(FontAwesomeSolid.EDIT));
        edit.setOnAction(event -> {
            Transaction selected = lv.getSelectionModel().getSelectedItem();
            if (selected != null) {
                log.info("Edit: {}", selected);
                new TransactionDialog(selected).showAndWait().ifPresent(response -> {
                    transactionService.addTransaction(response);
                    transactionList.set(transactionList.indexOf(selected), response);
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
                        transactionList.remove(selected);
                        transactionService.deleteTransactionById(selected.getId());
                    }
                }
            });
        });

        return new ContextMenu(edit, delete);
    }
}
