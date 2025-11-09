package com.redseismica.test;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.database.DataLoader;

import java.sql.SQLException;

/**
 * Clase de prueba para verificar la conexión y carga de datos en la base de datos.
 */
public class TestDatabase {
    public static void main(String[] args) {
        try {
            System.out.println("=== Probando Base de Datos ===");
            DatabaseConfig.createTables();
            DataLoader.loadInitialData();
            System.out.println("\n=== Prueba exitosa ===");
            DatabaseConfig.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
