package com.example.proyectofinal;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Date;

public class WithdrawFX extends Stage {

    private final String pin;

    public WithdrawFX(String pin) {
        this.pin = pin;

        ImageView background = new ImageView(
                new Image(getClass().getResourceAsStream("/com/example/proyectofinal/atm.jpg"))
        );
        background.setFitWidth(960);
        background.setFitHeight(1080);

        Label title = new Label("MAXIMUM WITHDRAWAL IS RS.10,000\nPLEASE ENTER YOUR AMOUNT");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to withdraw");

        Button withdrawBtn = new Button("WITHDRAW");
        Button backBtn = new Button("BACK");

        VBox vbox = new VBox(20, title, amountField, withdrawBtn, backBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setLayoutX(340);
        vbox.setLayoutY(380);

        AnchorPane root = new AnchorPane(background, vbox);
        Scene scene = new Scene(root, 960, 1080);

        withdrawBtn.setOnAction(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                if (amount > 10000) {
                    showAlert("Error", "Maximum withdrawal is Rs.10,000");
                    return;
                }

                Conn conn = new Conn();
                ResultSet rs = conn.getStatement().executeQuery("SELECT * FROM bank WHERE pin = '" + pin + "'");
                int balance = 0;
                while (rs.next()) {
                    String mode = rs.getString("mode");
                    int amt = rs.getInt("amount");
                    balance += mode.equals("Deposit") ? amt : -amt;
                }

                if (balance < amount) {
                    showAlert("Error", "Insufficient funds!");
                    conn.close();
                    return;
                }

                conn.getStatement().executeUpdate(
                        "INSERT INTO bank (pin, date, mode, amount) VALUES ('" + pin + "', '" + new Date() + "', 'Withdraw', '" + amount + "')"
                );
                conn.close();

                showAlert("Success", "Rs. " + amount + " withdrawn successfully!");
                close();
                try {
                    TransactionsFX transactions = new TransactionsFX(pin);
                    transactions.start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Invalid amount or database error.");
            }
        });

        backBtn.setOnAction(e -> {
            close();
            try {
                TransactionsFX transactions = new TransactionsFX(pin);
                transactions.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        setScene(scene);
        setTitle("Withdraw Money");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
