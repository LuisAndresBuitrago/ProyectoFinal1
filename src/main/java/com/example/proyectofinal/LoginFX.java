package com.example.proyectofinal;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class LoginFX extends Application {

    private TextField cardField;
    private PasswordField pinField;
    private Label messageLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("AUTOMATED TELLER MACHINE");

        // --- Logo ---
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/com/example/proyectoFinal/logo.jpg")));
        logo.setFitHeight(100);
        logo.setFitWidth(100);

        // --- Labels ---
        Label title = new Label("WELCOME TO ATM");
        title.setFont(new Font("Oswald", 38));

        Label cardLabel = new Label("Card No:");
        cardLabel.setFont(new Font("Raleway", 24));

        Label pinLabel = new Label("PIN:");
        pinLabel.setFont(new Font("Raleway", 24));

        // --- Inputs ---
        cardField = new TextField();
        cardField.setPromptText("Enter your card number");
        cardField.setFont(new Font(16));

        pinField = new PasswordField();
        pinField.setPromptText("Enter your PIN");
        pinField.setFont(new Font(16));

        // --- Message Label ---
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14;");

        // --- Buttons ---
        Button signInBtn = new Button("SIGN IN");
        signInBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        signInBtn.setFont(new Font(14));

        Button clearBtn = new Button("CLEAR");
        clearBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        clearBtn.setFont(new Font(14));

        Button signUpBtn = new Button("SIGN UP");
        signUpBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        signUpBtn.setFont(new Font(14));

        // --- Layout principal ---
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: white;");

        // --- Parte superior ---
        HBox logoBox = new HBox(10, logo, title);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10, 0, 30, 0));

        // --- Formulario ---
        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(20);
        form.setAlignment(Pos.CENTER);

        form.add(cardLabel, 0, 0);
        form.add(cardField, 1, 0);
        form.add(pinLabel, 0, 1);
        form.add(pinField, 1, 1);

        // --- Botones ---
        HBox buttonBox = new HBox(20, signInBtn, clearBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox fullBox = new VBox(20, logoBox, form, buttonBox, signUpBtn, messageLabel);
        fullBox.setAlignment(Pos.CENTER);

        root.getChildren().add(fullBox);

        // --- Eventos ---
        signInBtn.setOnAction(e -> handleLogin(stage));
        clearBtn.setOnAction(e -> {
            cardField.clear();
            pinField.clear();
            messageLabel.setText("");
        });
        signUpBtn.setOnAction(e -> {
            try {
                new SignupFX().start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 800, 480);
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogin(Stage stage) {
        String cardno = cardField.getText().trim();
        String pin = pinField.getText().trim();

        if (cardno.isEmpty() || pin.isEmpty()) {
            messageLabel.setText("⚠️ Please enter both Card No and PIN");
            return;
        }

        try {
            Conn conn = new Conn();
            // 🔹 Usa el nombre correcto de columna (cardnumber)
            String query = "SELECT * FROM login WHERE cardnumber = ? AND pin = ?";
            PreparedStatement ps = conn.getConnection().prepareStatement(query);
            ps.setString(1, cardno);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("✅ Login successful!");

                // 🔹 Abre TransactionsFX con el pin actual
                new TransactionsFX(pin).start(new Stage());
                stage.close();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("❌ Incorrect Card Number or PIN");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("⚠️ Database error.");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
