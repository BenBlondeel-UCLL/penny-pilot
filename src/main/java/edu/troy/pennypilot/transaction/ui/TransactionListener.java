package edu.troy.pennypilot.transaction.ui;

import edu.troy.pennypilot.transaction.persistence.Transaction;

public interface TransactionListener {
    void transactionAdded(Transaction transaction);
    void transactionUpdated(Transaction oldTx, Transaction newTx);
    void transactionDeleted(Transaction transaction);
}
