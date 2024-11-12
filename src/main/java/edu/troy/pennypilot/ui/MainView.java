package edu.troy.pennypilot.ui;

import edu.troy.pennypilot.JavaFxApplication;
import edu.troy.pennypilot.budget.service.BudgetService;
import edu.troy.pennypilot.budget.ui.BudgetController;
import edu.troy.pennypilot.transaction.persistence.Transaction;
import edu.troy.pennypilot.transaction.service.TransactionService;
import edu.troy.pennypilot.transaction.ui.TransactionController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MainView {

    private final TransactionController transactionController;
    private final BudgetController budgetController;

    public MainView(TransactionService transactionService, BudgetService budgetService) {
        this.transactionController = new TransactionController(transactionService);
        this.budgetController = new BudgetController(budgetService, transactionController.getModel());
    }

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
        TabPane tabPane = new TabPane(homeTab(), transactionTab(), budgetTab(), statisticsTab());
        // load data after listeners
        transactionController.loadTransactions();
        budgetController.loadBudgets();

        return tabPane;
    }

    Tab homeTab() {
        Text text = new Text("PENNY PILOT");
        text.setId("income");
        var homePane = new BorderPane(text);
        homePane.getStyleClass().add("home");

        // Home Tab
        Tab homeTab = new Tab("Home", homePane);
        homeTab.setGraphic(new FontIcon(FontAwesomeSolid.HOME));
        homeTab.setClosable(false);

        return homeTab;
    }

    Tab transactionTab() {
        Tab transactionTab = new Tab("Transactions", transactionController.getView());
        transactionTab.setClosable(false);
        transactionTab.setGraphic(new FontIcon(FontAwesomeSolid.MONEY_BILL));

        return transactionTab;
    }

    Tab budgetTab(){
        Tab budgetTab = new Tab("Budget", budgetController.getView());
        budgetTab.setClosable(false);
        budgetTab.setGraphic(new FontIcon(FontAwesomeSolid.COINS));

        return budgetTab;
    }

    Tab statisticsTab() {
        ObservableList<PieChart.Data> incomePieChartData = FXCollections.observableArrayList();
        PieChart incomeChart = new PieChart(incomePieChartData);
        incomeChart.setTitle("Income");
        incomeChart.setLabelsVisible(false);

        var incomeList = transactionController.getModel().getIncomeList();
        updatePieChart(incomeList, Transaction::getIncomeCategory, incomePieChartData);

        ObservableList<PieChart.Data> expensePieChartData = FXCollections.observableArrayList();
        PieChart expenseChart = new PieChart(expensePieChartData);
        expenseChart.setTitle("Expenses");
        expenseChart.setLabelsVisible(false);

        var expenseList = transactionController.getModel().getExpenselist();
        updatePieChart(expenseList, Transaction::getExpenseCategory, expensePieChartData);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.addRow(0, incomeChart, expenseChart);
        GridPane.setHgrow(incomeChart, Priority.ALWAYS);
        GridPane.setVgrow(incomeChart, Priority.ALWAYS);
        GridPane.setHgrow(expenseChart, Priority.ALWAYS);
        GridPane.setVgrow(expenseChart, Priority.ALWAYS);

        // Statistics Tab
        Tab statisticsTab = new Tab("Statistics", pane);
        statisticsTab.setClosable(false);
        statisticsTab.setGraphic(new FontIcon(FontAwesomeSolid.CHART_PIE));

        return statisticsTab;
    }

    void updatePieChart(FilteredList<Transaction> transactionList, Function<Transaction, Enum<?>> groupByCategory, ObservableList<PieChart.Data> pieChartData) {
        transactionList.addListener((ListChangeListener<Transaction>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    pieChartData.clear();
                    float total = sum(transactionList);
                    transactionList.stream()
                            .collect(Collectors.groupingBy(groupByCategory))
                            .forEach((category, transactionsForCategory) -> {
                                float sum = sum(transactionsForCategory);
                                if (sum != 0) {
                                    double percentage = sum * 100 / total;
                                    pieChartData.add(new PieChart.Data(category.name() + ": " + String.format("%.2f", percentage) + "%", sum));
                                }
                            });
                }
            }
        });
    }

    float sum(Collection<Transaction> transactions) {
        return transactions.stream().map(Transaction::getAmount).reduce(0.0f, Float::sum);
    }
}
