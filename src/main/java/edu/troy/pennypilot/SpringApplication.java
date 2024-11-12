package edu.troy.pennypilot;

import edu.troy.pennypilot.transaction.persistence.ExpenseCategory;
import edu.troy.pennypilot.transaction.persistence.Transaction;
import edu.troy.pennypilot.transaction.persistence.TransactionType;
import edu.troy.pennypilot.transaction.service.TransactionService;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@Slf4j
@SpringBootApplication
public class SpringApplication {

	@Bean
	ApplicationRunner init(TransactionService transactionService) {
		return args -> {
			log.info("Database initialization ...");
			if (transactionService.getTransactionByDescription("shoes").isEmpty()) {
				Transaction trans1 = Transaction.builder().amount(100).description("shoes").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build();
				transactionService.addTransaction(trans1);
			}
			if (transactionService.getTransactionByDescription("shirt").isEmpty()) {
				Transaction trans2 = Transaction.builder().amount(50).description("shirt").date(LocalDate.now()).type(TransactionType.EXPENSE).expenseCategory(ExpenseCategory.SHOPPING).build();
				transactionService.addTransaction(trans2);
			}
		};
	}

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}

}
