package edu.troy.pennypilot.dialog;

import edu.troy.pennypilot.model.Transaction;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;

public class TransactionDialog extends Dialog<Transaction> {
        public TransactionDialog(){
        TextField description = new TextField();
        VBox vBox = new VBox(new Label("Description: "), description);
        getDialogPane().setContent(vBox);
        setTitle("Transaction dialog");
        getDialogPane().setHeaderText("Create transaction");
        getDialogPane().getButtonTypes().addAll(new ButtonType("SAVE", ButtonData.OK_DONE), ButtonType.CANCEL);

        // Convert result to transaction when SAVE button is clicked
        setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonData.OK_DONE) {
                return Transaction.builder().description(description.getText()).build();
            }
            return null;
        });
    }
}
