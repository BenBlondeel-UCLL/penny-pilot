package edu.troy.pennypilot;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        log.info("Starting Spring...");
        applicationContext = new SpringApplicationBuilder(SpringApplication.class)
                .web(WebApplicationType.NONE)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

//    public void insertData (){
//        Transaction trans1 = Transaction.builder().amount(100).description("shoes").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build();
//        Transaction trans2 = Transaction.builder().amount(50).description("shirt").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build();
//        if (transactionService.getTranasctionByDescription("shoes") == null) {
//            transactionService.addTransaction(trans1);
//        }
//        if (transactionService.getTranasctionByDescription("shirt") == null) {
//            transactionService.addTransaction(trans2);
//        }
//    }

    @Override
    public void start(Stage stage) throws Exception {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping Spring...");
        applicationContext.stop();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}
