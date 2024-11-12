package edu.troy.pennypilot.transaction.ui;

import edu.troy.pennypilot.transaction.persistence.Transaction;
import edu.troy.pennypilot.transaction.service.TransactionService;

public class TransactionController implements TransactionListener {

    private final TransactionService service;
    private final TransactionModel model;
    private final TransactionView view;

    public TransactionController(TransactionService service) {
        this.service = service;
        model = new TransactionModel();
        view = new TransactionView(model);

        view.addTransactionListener(this);
    }

    @Override
    public void transactionAdded(Transaction transaction) {
        Transaction tx = service.addTransaction(transaction);
        model.getTransactionList().add(tx);
    }

    @Override
    public void transactionUpdated(Transaction oldTx, Transaction newTx) {
        service.addTransaction(newTx);
        model.getTransactionList().set(model.getTransactionList().indexOf(oldTx), newTx);
    }

    @Override
    public void transactionDeleted(Transaction transaction) {
        model.getTransactionList().remove(transaction);
        service.deleteTransactionById(transaction.getId());
    }

    public void loadTransactions() {
        service.getAllTransactions()
                .forEach(model.getTransactionList()::add);
    }

    public TransactionModel getModel() {
        return model;
    }

    public TransactionView getView() {
        return view;
    }
}
