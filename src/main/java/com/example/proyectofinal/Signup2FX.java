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

public class Signup2FX extends Application {
    private String formno;
    private TextField incomeField, educationField, occupationField;
    private ComboBox<String> religionBox;

    public Signup2FX() {
        // Constructor sin parámetros para JavaFX
        this.formno = "0000"; // valor por defecto o temporal
    }

    public Signup2FX(String formno) {
        this.formno = formno;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NEW ACCOUNT APPLICATION FORM - PAGE 2");

        Label title = new Label("Page 2: Additional Details");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label religionLabel = new Label("Religion:");
        religionBox = new ComboBox<>();
        religionBox.getItems().addAll("Hindu", "Muslim", "Christian", "Other");

        Label incomeLabel = new Label("Income:");
        incomeField = new TextField();

        Label educationLabel = new Label("Education:");
        educationField = new TextField();

        Label occupationLabel = new Label("Occupation:");
        occupationField = new TextField();

        Button nextBtn = new Button("Next");
        nextBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        nextBtn.setOnAction(e -> saveToDatabase());

        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 50, 25, 50));

        grid.add(title, 0, 0, 2, 1);
        grid.add(religionLabel, 0, 1); grid.add(religionBox, 1, 1);
        grid.add(incomeLabel, 0, 2); grid.add(incomeField, 1, 2);
        grid.add(educationLabel, 0, 3); grid.add(educationField, 1, 3);
        grid.add(occupationLabel, 0, 4); grid.add(occupationField, 1, 4);
        grid.add(nextBtn, 1, 5);

        grid.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(grid, 650, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveToDatabase() {
        String religion = religionBox.getValue();
        String income = incomeField.getText();
        String education = educationField.getText();
        String occupation = occupationField.getText();

        if (religion == null || income.isEmpty() || education.isEmpty() || occupation.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields.");
            return;
        }

        try {
            Conn conn = new Conn();
            Connection c = conn.getConnection();
            String query = "INSERT INTO signup2 (formno, religion, income, education, occupation) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setString(1, formno);
            stmt.setString(2, religion);
            stmt.setString(3, income);
            stmt.setString(4, education);
            stmt.setString(5, occupation);
            stmt.executeUpdate();
            conn.close();

            showAlert(Alert.AlertType.INFORMATION, "✅ Page 2 saved successfully!");

            // 👉 Ir a la página 3
            Signup3FX signup3 = new Signup3FX(formno);
            Stage stage3 = new Stage();
            signup3.start(stage3);

            // Cerrar esta ventana
            Stage currentStage = (Stage) incomeField.getScene().getWindow();
            currentStage.close();

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
}
