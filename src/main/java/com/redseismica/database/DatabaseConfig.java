package com.redseismica.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuración y gestión de la conexión a la base de datos H2 en memoria.
 * Proporciona métodos para obtener conexiones y crear las tablas necesarias.
 */
public class DatabaseConfig {
    // Usar archivo en disco para persistencia entre ejecuciones
    private static final String DB_URL = "jdbc:h2:file:./data/redseismica;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection;

    /**
     * Obtiene una conexión a la base de datos. Si no existe, la crea.
     *
     * @return conexión activa a la base de datos
     * @throws SQLException si hay un error al conectar
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    /**
     * Crea todas las tablas necesarias para el sistema.
     *
     * @throws SQLException si hay un error al crear las tablas
     */
    public static void createTables() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        // Tabla de Roles
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS roles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL UNIQUE
            )
        """);

        // Tabla de Empleados
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS empleados (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                apellido VARCHAR(100) NOT NULL,
                mail VARCHAR(150) NOT NULL,
                telefono VARCHAR(20),
                rol_id INT NOT NULL,
                FOREIGN KEY (rol_id) REFERENCES roles(id)
            )
        """);

        // Tabla de Usuarios
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(100) NOT NULL,
                empleado_id INT NOT NULL,
                FOREIGN KEY (empleado_id) REFERENCES empleados(id)
            )
        """);

        // Tabla de Sismógrafos
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS sismografos (
                id INT AUTO_INCREMENT PRIMARY KEY,
                numero_serie INT NOT NULL UNIQUE,
                fecha_instalacion TIMESTAMP NOT NULL,
                modelo INT NOT NULL,
                estado_actual VARCHAR(50) NOT NULL,
                fecha_hora_estado TIMESTAMP NOT NULL
            )
        """);

        // Tabla de Estaciones Sismológicas
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS estaciones (
                id INT AUTO_INCREMENT PRIMARY KEY,
                codigo INT NOT NULL UNIQUE,
                nombre VARCHAR(150) NOT NULL,
                latitud DOUBLE NOT NULL,
                longitud DOUBLE NOT NULL,
                sismografo_id INT NOT NULL,
                FOREIGN KEY (sismografo_id) REFERENCES sismografos(id)
            )
        """);

        // Tabla de Órdenes de Inspección
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS ordenes_inspeccion (
                id INT AUTO_INCREMENT PRIMARY KEY,
                numero_orden INT NOT NULL UNIQUE,
                fecha_hora_emision TIMESTAMP NOT NULL,
                fecha_hora_finalizacion TIMESTAMP,
                fecha_hora_cierre TIMESTAMP,
                estado VARCHAR(50) NOT NULL,
                observacion_cierre TEXT,
                estacion_id INT NOT NULL,
                responsable_id INT NOT NULL,
                FOREIGN KEY (estacion_id) REFERENCES estaciones(id),
                FOREIGN KEY (responsable_id) REFERENCES empleados(id)
            )
        """);

        // Tabla de Motivos Tipo
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS motivos_tipo (
                id INT AUTO_INCREMENT PRIMARY KEY,
                descripcion VARCHAR(200) NOT NULL UNIQUE
            )
        """);

        // Tabla de Motivos Fuera de Servicio
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS motivos_fuera_servicio (
                id INT AUTO_INCREMENT PRIMARY KEY,
                motivo_tipo_id INT NOT NULL,
                comentario TEXT,
                orden_id INT NOT NULL,
                FOREIGN KEY (motivo_tipo_id) REFERENCES motivos_tipo(id),
                FOREIGN KEY (orden_id) REFERENCES ordenes_inspeccion(id)
            )
        """);

        // Tabla de Cambios de Estado del Sismógrafo
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS cambios_estado_sismografo (
                id INT AUTO_INCREMENT PRIMARY KEY,
                sismografo_id INT NOT NULL,
                fecha_hora TIMESTAMP NOT NULL,
                estado VARCHAR(50) NOT NULL,
                observacion TEXT,
                empleado_id INT,
                FOREIGN KEY (sismografo_id) REFERENCES sismografos(id),
                FOREIGN KEY (empleado_id) REFERENCES empleados(id)
            )
        """);

        stmt.close();
        System.out.println("✓ Tablas creadas exitosamente");
    }

    /**
     * Cierra la conexión a la base de datos.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
