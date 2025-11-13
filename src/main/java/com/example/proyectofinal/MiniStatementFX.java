package com.example.proyectofinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.ResultSet;

public class MiniStatementFX extends Stage {

    private final String pin;
    private final TableView<Transaction> table;
    private final Label balanceLabel;

    public MiniStatementFX(String pin) {
        this.pin = pin;

        Label title = new Label("Indian Bank - Mini Statement");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-text-fill: #264653; -fx-font-weight: bold;");

        Label cardLabel = new Label();
        cardLabel.setFont(new Font("Arial", 14));

        balanceLabel = new Label("Balance: Rs 0");
        balanceLabel.setFont(new Font("Arial", 14));
        balanceLabel.setStyle("-fx-text-fill: #2a9d8f; -fx-font-weight: bold;");

        table = new TableView<>();
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        dateCol.setPrefWidth(120);

        TableColumn<Transaction, String> modeCol = new TableColumn<>("Mode");
        modeCol.setCellValueFactory(data -> data.getValue().modeProperty());
        modeCol.setPrefWidth(100);

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> data.getValue().amountProperty());
        amountCol.setPrefWidth(100);

        table.getColumns().addAll(dateCol, modeCol, amountCol);

        Button backBtn = new Button("BACK");
        backBtn.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            close();
            try {
                TransactionsFX transactions = new TransactionsFX(pin);
                transactions.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        VBox layout = new VBox(15, title, cardLabel, table, balanceLabel, backBtn);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(400);
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 420, 600);
        setScene(scene);
        setTitle("Mini Statement");
        setResizable(false);

        loadCardNumber(cardLabel);
        loadTransactions();
    }

    private void loadCardNumber(Label cardLabel) {
        try {
            Conn c = new Conn();
            ResultSet rs = c.getStatement().executeQuery("SELECT * FROM login WHERE pin = '" + pin + "'");
            if (rs.next()) {
                String card = rs.getString("card_number");
                if (card != null && card.length() >= 16) {
                    String masked = card.substring(0, 4) + "XXXXXXXX" + card.substring(12);
                    cardLabel.setText("Card Number: " + masked);
                } else {
                    cardLabel.setText("Card Number: " + card);
                }
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        ObservableList<Transaction> data = FXCollections.observableArrayList();
        int balance = 0;

        try {
            Conn c = new Conn();
            ResultSet rs = c.getStatement().executeQuery("SELECT * FROM bank WHERE pin = '" + pin + "'");
            while (rs.next()) {
                String date = rs.getString("date");
                String mode = rs.getString("mode");
                String amount = rs.getString("amount");
                data.add(new Transaction(date, mode, amount));

                if ("Deposit".equalsIgnoreCase(mode)) balance += Integer.parseInt(amount);
                else balance -= Integer.parseInt(amount);
            }
            table.setItems(data);
            balanceLabel.setText("Balance: Rs " + balance);
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Transaction {
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleStringProperty mode;
        private final javafx.beans.property.SimpleStringProperty amount;

        public Transaction(String date, String mode, String amount) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.mode = new javafx.beans.property.SimpleStringProperty(mode);
            this.amount = new javafx.beans.property.SimpleStringProperty(amount);
        }

        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty modeProperty() { return mode; }
        public javafx.beans.property.StringProperty amountProperty() { return amount; }
    }
}
