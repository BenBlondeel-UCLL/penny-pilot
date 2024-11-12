package edu.troy.pennypilot.transaction.ui;

import edu.troy.pennypilot.transaction.persistence.Transaction;
import edu.troy.pennypilot.transaction.persistence.TransactionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

@Getter
public class TransactionModel {

    private final ObservableList<Transaction> transactionList;
    private final FilteredList<Transaction> incomeList;
    private final FilteredList<Transaction> expenselist;

    TransactionModel() {
        transactionList = FXCollections.observableArrayList();
        incomeList = new FilteredList<>(transactionList, transaction -> transaction.getType() == TransactionType.INCOME);
        expenselist = new FilteredList<>(transactionList, transaction -> transaction.getType() == TransactionType.EXPENSE);
    }

}
