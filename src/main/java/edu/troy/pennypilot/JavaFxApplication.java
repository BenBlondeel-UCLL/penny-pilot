package edu.troy.pennypilot;

import edu.troy.pennypilot.dialog.TransactionDialog;
import edu.troy.pennypilot.model.ExpenseCategory;
import edu.troy.pennypilot.model.Transaction;
import edu.troy.pennypilot.model.TransactionType;
import edu.troy.pennypilot.service.TransactionService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

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

    @Override
    public void start(Stage stage) throws Exception {
        transactionService.addTransaction(Transaction.builder().amount(-100).description("shoes").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build());
        transactionService.addTransaction(Transaction.builder().amount(-50).description("shirt").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build());
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactionService.getAllTransactions());
        Button oke = new Button("OKE");
        oke.setOnAction(actionEvent -> {
            transactionService.deleteTransactionById(transactionList.remove(transactionList.size()-1).getId());
            log.info("Db: {}", transactionService.getAllTransactions());
        });
        Dialog transactionDialog = new TransactionDialog();
        Button create = new Button("Create"); 
        create.setOnAction(actionEvent -> {
            transactionDialog.showAndWait().ifPresent(response -> {
                log.info("dome response{}", response);
            });
        });
        VBox box = new VBox(new ListView<Transaction>(transactionList), oke, create);
        stage.setScene(new Scene(box, 200, 200));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
