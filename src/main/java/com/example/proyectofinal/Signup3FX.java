package com.example.proyectofinal;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Signup3FX extends Application {

    private String formno;
    private ToggleGroup accountTypeGroup;
    private CheckBox atmCard, mobileBanking, emailAlerts, chequeBook;

    public Signup3FX() {
        this.formno = "0000"; // valor temporal si no viene desde Signup2
    }

    public Signup3FX(String formno) {
        this.formno = formno;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NEW ACCOUNT APPLICATION FORM - PAGE 3");

        Label title = new Label("Page 3: Account Details");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Tipo de cuenta
        Label accountTypeLabel = new Label("Account Type:");
        RadioButton savingAcc = new RadioButton("Saving Account");
        RadioButton fixedAcc = new RadioButton("Fixed Deposit Account");
        RadioButton currentAcc = new RadioButton("Current Account");
        RadioButton recurringAcc = new RadioButton("Recurring Deposit Account");

        accountTypeGroup = new ToggleGroup();
        savingAcc.setToggleGroup(accountTypeGroup);
        fixedAcc.setToggleGroup(accountTypeGroup);
        currentAcc.setToggleGroup(accountTypeGroup);
        recurringAcc.setToggleGroup(accountTypeGroup);

        // Card y PIN
        Label cardLabel = new Label("Card Number:");
        Label cardValue = new Label("XXXX-XXXX-XXXX-4184");
        Label pinLabel = new Label("PIN:");
        Label pinValue = new Label("XXXX");

        // Servicios
        Label servicesLabel = new Label("Services Required:");
        atmCard = new CheckBox("ATM Card");
        mobileBanking = new CheckBox("Mobile Banking");
        emailAlerts = new CheckBox("EMAIL Alerts");
        chequeBook = new CheckBox("Cheque Book");

        // Declaración
        CheckBox declaration = new CheckBox("I hereby declare that the above entered details are correct to the best of my knowledge.");
        declaration.setSelected(true);

        // Botones
        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        submitBtn.setOnAction(e -> saveToDatabase());

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        cancelBtn.setOnAction(e -> ((Stage) cancelBtn.getScene().getWindow()).close());

        // Layout
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 50, 25, 50));

        grid.add(title, 0, 0, 2, 1);
        grid.add(accountTypeLabel, 0, 1);
        grid.add(savingAcc, 1, 1);
        grid.add(fixedAcc, 1, 2);
        grid.add(currentAcc, 1, 3);
        grid.add(recurringAcc, 1, 4);
        grid.add(cardLabel, 0, 5);
        grid.add(cardValue, 1, 5);
        grid.add(pinLabel, 0, 6);
        grid.add(pinValue, 1, 6);
        grid.add(servicesLabel, 0, 7);
        grid.add(atmCard, 1, 7);
        grid.add(mobileBanking, 1, 8);
        grid.add(emailAlerts, 1, 9);
        grid.add(chequeBook, 1, 10);
        grid.add(declaration, 0, 11, 2, 1);
        grid.add(submitBtn, 0, 12);
        grid.add(cancelBtn, 1, 12);

        grid.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(grid, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveToDatabase() {
        RadioButton selectedType = (RadioButton) accountTypeGroup.getSelectedToggle();
        if (selectedType == null) {
            showAlert(Alert.AlertType.ERROR, "Please select an account type.");
            return;
        }

        String accountType = selectedType.getText();

        Random ran = new Random();
        long first7 = (ran.nextLong() % 90000000L) + 5040936000000000L;
        String cardNumber = "" + Math.abs(first7);

        long first3 = (ran.nextLong() % 9000L) + 1000L;
        String pin = "" + Math.abs(first3);

        StringBuilder facility = new StringBuilder();
        if (atmCard.isSelected()) facility.append("ATM Card ");
        if (mobileBanking.isSelected()) facility.append("Mobile Banking ");
        if (emailAlerts.isSelected()) facility.append("EMAIL Alerts ");
        if (chequeBook.isSelected()) facility.append("Cheque Book ");

        try {
            Conn conn = new Conn();
            Connection c = conn.getConnection();

            // Inserta en signup3
            String q1 = "INSERT INTO signup3 (formno, account_type, card_number, pin, facility) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt1 = c.prepareStatement(q1);
            stmt1.setString(1, formno);
            stmt1.setString(2, accountType);
            stmt1.setString(3, cardNumber);
            stmt1.setString(4, pin);
            stmt1.setString(5, facility.toString().trim());
            stmt1.executeUpdate();

            // Inserta en login
            String q2 = "INSERT INTO login (formno, card_number, pin) VALUES (?, ?, ?)";
            PreparedStatement stmt2 = c.prepareStatement(q2);
            stmt2.setString(1, formno);
            stmt2.setString(2, cardNumber);
            stmt2.setString(3, pin);
            stmt2.executeUpdate();

            c.close();

            showAlert(Alert.AlertType.INFORMATION,
                    "✅ Account created successfully!\nCard Number: " + cardNumber + "\nPIN: " + pin);

            ((Stage) atmCard.getScene().getWindow()).close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Database error: " + ex.getMessage());
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
