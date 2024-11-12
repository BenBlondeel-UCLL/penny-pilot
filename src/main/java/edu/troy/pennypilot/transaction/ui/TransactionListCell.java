package edu.troy.pennypilot.transaction.ui;

import edu.troy.pennypilot.transaction.persistence.Transaction;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;

public class TransactionListCell extends ListCell<Transaction> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private GridPane grid = new GridPane(5,5);
    private Label date = new Label("", new FontIcon(FontAwesomeSolid.CALENDAR_ALT));
    private Label amount = new Label("", new FontIcon(FontAwesomeSolid.DOLLAR_SIGN));
    private Text description = new Text();
    private ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/money.jpg")));

    public TransactionListCell() {
        imageView.setFitHeight(30);
        imageView.setFitWidth(50);

        grid.addRow(0, imageView, date, amount);
        grid.add(description, 1, 1);

        GridPane.setRowSpan(imageView, 2);
        GridPane.setColumnSpan(description, 2);
        GridPane.setHgrow(date, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            date.setText(formatter.format(item.getDate()));
            amount.setText(String.format("$%.2f", item.getAmount()));
            description.setText(item.getDescription());

            setGraphic(grid);
        }
    }
}
