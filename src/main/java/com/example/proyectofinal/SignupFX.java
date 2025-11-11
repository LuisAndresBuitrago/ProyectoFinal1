package com.example.proyectofinal;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class SignupFX extends Application {

    private TextField nameField, emailField, addressField, cityField;
    private DatePicker dobPicker;
    private ToggleGroup genderGroup, maritalGroup;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NEW ACCOUNT APPLICATION FORM");

        // Generar número de formulario
        Random ran = new Random();
        long formNum = (ran.nextLong() % 9000L) + 1000L;
        String formno = "" + Math.abs(formNum);

        Label title = new Label("APPLICATION FORM NO. " + formno);
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitle = new Label("Page 1: Personal Details");
        subtitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Campos
        Label nameLabel = new Label("Name:");
        nameField = new TextField();

        Label dobLabel = new Label("Date of Birth:");
        dobPicker = new DatePicker();

        Label genderLabel = new Label("Gender:");
        RadioButton male = new RadioButton("Male");
        RadioButton female = new RadioButton("Female");
        genderGroup = new ToggleGroup();
        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);
        HBox genderBox = new HBox(10, male, female);

        Label emailLabel = new Label("Email Address:");
        emailField = new TextField();

        Label maritalLabel = new Label("Marital Status:");
        RadioButton married = new RadioButton("Married");
        RadioButton unmarried = new RadioButton("Unmarried");
        RadioButton other = new RadioButton("Other");
        maritalGroup = new ToggleGroup();
        married.setToggleGroup(maritalGroup);
        unmarried.setToggleGroup(maritalGroup);
        other.setToggleGroup(maritalGroup);
        HBox maritalBox = new HBox(10, married, unmarried, other);

        Label addressLabel = new Label("Address:");
        addressField = new TextField();

        Label cityLabel = new Label("City:");
        cityField = new TextField();

        Button nextBtn = new Button("Next");
        nextBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        nextBtn.setOnAction(e -> saveToDatabase(formno));

        // Diseño
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 50, 25, 50));

        grid.add(title, 0, 0, 2, 1);
        grid.add(subtitle, 0, 1, 2, 1);
        grid.add(nameLabel, 0, 2); grid.add(nameField, 1, 2);
        grid.add(dobLabel, 0, 3); grid.add(dobPicker, 1, 3);
        grid.add(genderLabel, 0, 4); grid.add(genderBox, 1, 4);
        grid.add(emailLabel, 0, 5); grid.add(emailField, 1, 5);
        grid.add(maritalLabel, 0, 6); grid.add(maritalBox, 1, 6);
        grid.add(addressLabel, 0, 7); grid.add(addressField, 1, 7);
        grid.add(cityLabel, 0, 8); grid.add(cityField, 1, 8);
        grid.add(nextBtn, 1, 9);

        grid.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(grid, 650, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveToDatabase(String formno) {
        String name = nameField.getText();
        LocalDate dob = dobPicker.getValue();
        RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
        RadioButton selectedMarital = (RadioButton) maritalGroup.getSelectedToggle();
        String email = emailField.getText();
        String address = addressField.getText();
        String city = cityField.getText();

        // Validación
        if (name.isEmpty() || dob == null || selectedGender == null || email.isEmpty() ||
                selectedMarital == null || address.isEmpty() || city.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields.");
            return;
        }

        String gender = selectedGender.getText();
        String marital = selectedMarital.getText();

        try {
            Conn conn = new Conn();
            Connection c = conn.getConnection();
            String query = "INSERT INTO signup (formno, name, dob, gender, email, marital, address, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = c.prepareStatement(query);

            stmt.setString(1, formno);
            stmt.setString(2, name);
            stmt.setDate(3, Date.valueOf(dob));
            stmt.setString(4, gender);
            stmt.setString(5, email);
            stmt.setString(6, marital);
            stmt.setString(7, address);
            stmt.setString(8, city);

            stmt.executeUpdate();
            conn.close();

            showAlert(Alert.AlertType.INFORMATION, "✅ Page 1 saved successfully!");

            // 👉 Abrir Signup2FX
            Signup2FX signup2 = new Signup2FX(formno);
            Stage stage2 = new Stage();
            signup2.start(stage2);

            // Cerrar la ventana actual
            Stage currentStage = (Stage) nameField.getScene().getWindow();
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

    public static void main(String[] args) {
        launch(args);
    }
}
