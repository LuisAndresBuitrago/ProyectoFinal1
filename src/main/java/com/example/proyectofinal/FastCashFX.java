package com.example.proyectofinal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Date;

public class FastCashFX extends Stage {

    private final String pin;

    public FastCashFX(String pin) {
        this.pin = pin;

        Text title = new Text("SELECT WITHDRAWAL AMOUNT");
        title.setFont(new Font("Arial", 20));

        Button b100 = createButton("Rs 100");
        Button b500 = createButton("Rs 500");
        Button b1000 = createButton("Rs 1000");
        Button b2000 = createButton("Rs 2000");
        Button b5000 = createButton("Rs 5000");
        Button b10000 = createButton("Rs 10000");
        Button back = createButton("BACK");
        back.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, b100, b500);
        grid.addRow(1, b1000, b2000);
        grid.addRow(2, b5000, b10000);
        grid.add(back, 1, 3);

        VBox layout = new VBox(20, title, grid);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 400, 400);
        setScene(scene);
        setTitle("Fast Cash");
        setResizable(false);

        // Acciones
        b100.setOnAction(e -> withdraw(100));
        b500.setOnAction(e -> withdraw(500));
        b1000.setOnAction(e -> withdraw(1000));
        b2000.setOnAction(e -> withdraw(2000));
        b5000.setOnAction(e -> withdraw(5000));
        b10000.setOnAction(e -> withdraw(10000));
        back.setOnAction(e -> {
            close();
            try {
                TransactionsFX transactions = new TransactionsFX(pin);
                transactions.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setFont(new Font("Arial", 16));
        btn.setPrefWidth(150);
        btn.setStyle("-fx-background-color: #2a9d8f; -fx-text-fill: white; -fx-font-weight: bold;");
        return btn;
    }

    private void withdraw(int amount) {
        try {
            Conn c = new Conn();
            ResultSet rs = c.getStatement().executeQuery("SELECT * FROM bank WHERE pin = '" + pin + "'");
            int balance = 0;

            while (rs.next()) {
                String mode = rs.getString("mode");
                int amt = Integer.parseInt(rs.getString("amount"));
                balance += mode.equals("Deposit") ? amt : -amt;
            }

            if (balance < amount) {
                showAlert("Insufficient Balance", "Your current balance is less than Rs " + amount);
                c.close();
                return;
            }

            c.getStatement().executeUpdate("INSERT INTO bank (pin, date, mode, amount) VALUES ('"
                    + pin + "', '" + new Date() + "', 'Withdraw', '" + amount + "')");
            c.close();

            showAlert("Success", "Rs " + amount + " Debited Successfully!");
            close();
            try {
                TransactionsFX transactions = new TransactionsFX(pin);
                transactions.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while processing the transaction.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
