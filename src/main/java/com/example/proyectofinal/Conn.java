package com.example.proyectofinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conn {

    private Connection c;
    private Statement s;

    public Conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sistemadegestiondebanco",
                    "root",
                    "Urbanperro17"
            );
            s = c.createStatement();
            System.out.println("✅ Conexión exitosa a la base de datos MySQL.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: No se encontró el driver JDBC de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos:");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return c;
    }

    public Statement getStatement() {
        return s;
    }

    public void close() {
        try {
            if (s != null) s.close();
            if (c != null) c.close();
            System.out.println("🔒 Conexión cerrada correctamente.");
        } catch (SQLException e) {
            System.err.println("⚠️ Error al cerrar la conexión:");
            e.printStackTrace();
        }
    }
}
