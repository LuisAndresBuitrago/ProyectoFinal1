package com.example.proyectofinal;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;

public class BalanceFX extends Application {

    private String pin;
    private Label balanceLabel;

    // Constructor vacío para JavaFX
    public BalanceFX() {}

    // Constructor con parámetro PIN (para cuando se invoca desde otra clase)
    public BalanceFX(String pin) {
        this.pin = pin;
    }

    @Override
    public void start(Stage stage) {
        // Imagen de fondo (ajusta la ruta a tu carpeta resources)
        Image image = new Image(getClass().getResourceAsStream("/com/example/proyectofinal/atm.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(960);
        imageView.setFitHeight(1080);
        imageView.setPreserveRatio(false);

        // Label para mostrar el saldo
        balanceLabel = new Label("Consultando saldo...");
        balanceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Botón de volver
        Button backButton = new Button("Volver");
        backButton.setStyle("-fx-font-size: 16px; -fx-background-color: #3498db; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            stage.close();
            // Si tienes una ventana TransactionsFX, puedes abrirla aquí
            try {
                TransactionsFX transactions = new TransactionsFX(pin);
                transactions.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Contenedor principal
        VBox vbox = new VBox(30, balanceLabel, backButton);
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(imageView, vbox);
        Scene scene = new Scene(root, 960, 1080);

        // Ejecutar la consulta de saldo
        consultarBalance();

        stage.setTitle("Consulta de Saldo");
        stage.setScene(scene);
        stage.show();
    }

    // Método para consultar el balance en la base de datos
    private void consultarBalance() {
        int balance = 0;
        Conn conn = null;
        Statement stmt = null;
        try {
            conn = new Conn();
            stmt = conn.getStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM bank WHERE pin = '" + pin + "'");

            while (rs.next()) {
                String mode = rs.getString("mode");
                int amount = Integer.parseInt(rs.getString("amount"));
                if ("Deposit".equalsIgnoreCase(mode)) {
                    balance += amount;
                } else {
                    balance -= amount;
                }
            }

            balanceLabel.setText("Tu saldo actual es: $" + balance);
        } catch (Exception e) {
            e.printStackTrace();
            balanceLabel.setText("⚠️ Error al consultar el saldo.");
        } finally {
            if (conn != null) conn.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
