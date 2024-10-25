package edu.troy.pennypilot;

import edu.troy.pennypilot.dialog.TransactionDialog;
import edu.troy.pennypilot.model.ExpenseCategory;
import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.model.TransactionType;
import edu.troy.pennypilot.service.TransactionService;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class JavaFxApplication extends Application {

    private TransactionService transactionService;

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        log.info("Starting Spring...");
        applicationContext = new SpringApplicationBuilder(SpringApplication.class)
                .web(WebApplicationType.NONE)
                .run(getParameters().getRaw().toArray(new String[0]));
        transactionService = applicationContext.getBean(TransactionService.class);
    }

    public void insertData (){
        Transaction trans1 = Transaction.builder().amount(100).description("shoes").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build();     
        Transaction trans2 = Transaction.builder().amount(50).description("shirt").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build();
        if (transactionService.getTranasctionByDescription("shoes") == null) {
            transactionService.addTransaction(trans1);
        }
        if (transactionService.getTranasctionByDescription("shirt") == null) {
            transactionService.addTransaction(trans2);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Inserting data
        insertData();


        // Home Tab
        Text text = new Text("home");
        Tab homeTab = new Tab("Home", text);
        homeTab.setClosable(false);


        // Transaction Tab
        ObservableList<Transaction> incomeTransactionList = FXCollections.observableArrayList(transactionService.getAllIncomeTransactions());
        ObservableList<Transaction> expenseTransactionList = FXCollections.observableArrayList(transactionService.getAllExpenseTransactions());

        Button oke = new Button("Delete");
        oke.setOnAction(actionEvent -> {
            transactionService.deleteTransactionById(incomeTransactionList.remove(incomeTransactionList.size()-1).getId());
            log.info("Db: {}", transactionService.getAllTransactions());
        });

        // Create Transaction
        Dialog transactionDialog = new TransactionDialog();
        Button create = new Button("Create"); 
        create.setOnAction(actionEvent -> {
            Optional<Transaction> result = transactionDialog.showAndWait();
            result.ifPresent(response -> {
                log.info("Transaction: {}", response);
                transactionService.addTransaction(response);
                if (response.getType() == TransactionType.INCOME) {
                    incomeTransactionList.add(response);
                } else {
                    expenseTransactionList.add(response);
                }
            });
        });

        ListView incomeListView = new ListView<Transaction>(incomeTransactionList);
        incomeListView.prefWidthProperty().bind(stage.widthProperty().divide(2));
        ListView expenseListView = new ListView<Transaction>(expenseTransactionList);
        expenseListView.prefWidthProperty().bind(stage.widthProperty().divide(2));
        HBox listbox = new HBox(incomeListView, expenseListView);
        listbox.setAlignment(Pos.CENTER);
        listbox.setSpacing(10);
        HBox buttonBox = new HBox(create, oke);
        VBox vbox = new VBox(listbox, buttonBox);
        Tab transactionTab = new Tab("Transactions", vbox);
        transactionTab.setClosable(false);

        // Statistics Tab
        Tab statisticsTab = new Tab("Statistics");
        statisticsTab.setClosable(false);
        statisticsTab.setOnSelectionChanged(event -> {
            if (statisticsTab.isSelected()) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            List<Transaction> Expensetransactions = transactionService.getAllExpenseTransactions();
            double totalExpenses = Expensetransactions.stream().mapToDouble(Transaction::getAmount).sum();
            for (ExpenseCategory category : ExpenseCategory.values()) {
                double categoryTotal = Expensetransactions.stream()
                    .filter(transaction -> transaction.getExpenseCategory() == category)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
                if (categoryTotal != 0) {
                    double percentage = (Math.abs(categoryTotal) / Math.abs(totalExpenses)) * 100;
                    pieChartData.add(new PieChart.Data(category.name() + ": " + String.format("%.2f", percentage) + "%", Math.abs(categoryTotal)));
                }
            }
            final PieChart chart = new PieChart(pieChartData);
            statisticsTab.setContent(chart);
            }
        });


        // Initial Display
        TabPane tabPane = new TabPane(homeTab, transactionTab, statisticsTab);
        Scene scene = new Scene(tabPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Penny Pilot");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/money.jpg")));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
