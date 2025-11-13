package com.example.proyectofinal;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TransactionsFX extends Application {

    private String pin;

    // Constructor vacío requerido por JavaFX
    public TransactionsFX() {}

    // Constructor para pasar el PIN desde Login o Signup
    public TransactionsFX(String pin) {
        this.pin = pin;
    }

    @Override
    public void start(Stage stage) {
        // Imagen de fondo (asegúrate de tenerla en /resources/com/example/proyectofinal/icons/atm.jpg)
        Image image = new Image(getClass().getResourceAsStream("/com/example/proyectofinal/atm.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(960);
        imageView.setFitHeight(1080);
        imageView.setPreserveRatio(false);

        // Botones principales
        Button depositButton   = new Button("Depósito");
        Button withdrawButton  = new Button("Retiro");
        Button fastCashButton  = new Button("Retiros Rápidos");
        Button balanceButton   = new Button("Consultar Saldo");
        Button miniStateButton = new Button("Mini Statement");
        Button pinChangeButton = new Button("Cambiar PIN");
        Button exitButton      = new Button("Salir");

        // Estilo general
        String buttonStyle = "-fx-font-size: 18px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;";
        depositButton.setStyle(buttonStyle);
        withdrawButton.setStyle(buttonStyle);
        fastCashButton.setStyle(buttonStyle);
        balanceButton.setStyle(buttonStyle);
        miniStateButton.setStyle(buttonStyle);
        pinChangeButton.setStyle(buttonStyle);
        exitButton.setStyle("-fx-font-size: 18px; -fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");

        // Acciones
        depositButton.setOnAction(e -> {
            try {
                new DepositFX(pin).start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        withdrawButton.setOnAction(e -> {
            try {
                new WithdrawFX(pin).show();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fastCashButton.setOnAction(e -> {
            try {
                new FastCashFX(pin).show();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        balanceButton.setOnAction(e -> {
            try {
                new BalanceFX(pin).start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        miniStateButton.setOnAction(e -> {
            try {
                new MiniStatementFX(pin).show();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        pinChangeButton.setOnAction(e -> {
            try {
                new PinFX(pin).show();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> stage.close());

        // Contenedor
        VBox vbox = new VBox(15,
                depositButton,
                withdrawButton,
                fastCashButton,
                balanceButton,
                miniStateButton,
                pinChangeButton,
                exitButton
        );
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(imageView, vbox);
        Scene scene = new Scene(root, 960, 1080);

        stage.setTitle("Transacciones - Sistema de Gestión Bancaria");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
