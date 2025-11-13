package com.example.proyectofinal;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DepositFX extends Application {

    private String pin;
    private TextField amountField;

    public DepositFX() {
        this.pin = "0000"; // valor por defecto si no viene desde Login
    }

    public DepositFX(String pin) {
        this.pin = pin;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Deposit Money");

        // Fondo (imagen tipo cajero)
        Image backgroundImage = new Image(getClass().getResourceAsStream("/com/example/proyectofinal/atm.jpg"));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(700);
        backgroundView.setFitHeight(500);
        backgroundView.setOpacity(0.3);

        StackPane root = new StackPane();
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40, 40, 40, 40));
        vbox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-background-radius: 15;");

        Label title = new Label("ENTER AMOUNT YOU WANT TO DEPOSIT");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        amountField = new TextField();
        amountField.setPromptText("Enter amount in Rs");
        amountField.setStyle("-fx-font-size: 16px; -fx-pref-width: 250px;");

        Button depositBtn = new Button("DEPOSIT");
        depositBtn.setStyle("-fx-background-color: #0B6623; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 150px;");
        depositBtn.setOnAction(e -> depositMoney(primaryStage));

        Button backBtn = new Button("BACK");
        backBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 150px;");
        backBtn.setOnAction(e -> primaryStage.close());

        vbox.getChildren().addAll(title, amountField, depositBtn, backBtn);
        root.getChildren().addAll(backgroundView, vbox);

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void depositMoney(Stage stage) {
        String amountText = amountField.getText().trim();

        if (amountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter the amount you want to deposit.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Please enter a valid positive amount.");
                return;
            }

            Conn conn = new Conn();
            Connection c = conn.getConnection();

            String sql = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, pin);
            stmt.setString(2, new Date().toString());
            stmt.setString(3, "Deposit");
            stmt.setDouble(4, amount);

            stmt.executeUpdate();
            c.close();

            showAlert(Alert.AlertType.INFORMATION, "Rs. " + amount + " deposited successfully!");

            // Aquí podrías redirigir a la ventana de Transacciones
            // new TransactionsFX(pin).start(new Stage());
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid number.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
