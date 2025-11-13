package com.example.proyectofinal;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PinFX extends Stage {

    private final String pin;

    public PinFX(String pin) {
        this.pin = pin;

        ImageView background = new ImageView(
                new Image(getClass().getResourceAsStream("/com/example/proyectofinal/atm.jpg"))
        );
        background.setFitWidth(960);
        background.setFitHeight(1080);

        Label title = new Label("CHANGE YOUR PIN");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        PasswordField newPinField = new PasswordField();
        newPinField.setPromptText("Enter New PIN");
        PasswordField rePinField = new PasswordField();
        rePinField.setPromptText("Re-enter New PIN");

        Button changeBtn = new Button("CHANGE");
        Button backBtn = new Button("BACK");

        VBox vbox = new VBox(20, title, newPinField, rePinField, changeBtn, backBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setLayoutX(340);
        vbox.setLayoutY(380);

        AnchorPane root = new AnchorPane(background, vbox);
        Scene scene = new Scene(root, 960, 1080);

        changeBtn.setOnAction(e -> {
            String newPin = newPinField.getText();
            String rePin = rePinField.getText();

            if (newPin.isEmpty() || rePin.isEmpty()) {
                showAlert("Error", "Please fill in both PIN fields.");
                return;
            }
            if (!newPin.equals(rePin)) {
                showAlert("Error", "Entered PINs do not match.");
                return;
            }

            try {
                Conn conn = new Conn();
                conn.getStatement().executeUpdate("UPDATE bank SET pin = '" + newPin + "' WHERE pin = '" + pin + "'");
                conn.getStatement().executeUpdate("UPDATE login SET pin = '" + newPin + "' WHERE pin = '" + pin + "'");
                conn.getStatement().executeUpdate("UPDATE signup3 SET pin = '" + newPin + "' WHERE pin = '" + pin + "'");
                conn.close();
                showAlert("Success", "PIN changed successfully!");
                close();
                try {
                    TransactionsFX transactions = new TransactionsFX(pin);
                    transactions.start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Database Error", "Error updating your PIN.");
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
        setTitle("Change PIN");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
