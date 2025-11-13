package com.redseismica.app;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.database.DataLoader;
import java.sql.*;

/**
 * Utilidad para consultar el estado de los sismógrafos después de cerrar
 * una orden de inspección. Muestra los cambios de estado.
 */
public class ConsultarSismografo {

    public static void main(String[] args) {
        try {
            System.out.println("=== INICIALIZANDO BASE DE DATOS ===\n");
            
            // Crear tablas
            DatabaseConfig.createTables();
            
            // Cargar datos iniciales
            DataLoader.loadInitialData();
            
            System.out.println("\n=== CONSULTA DE SISMÓGRAFOS ===\n");

            // 1. Consultar todas las órdenes cerradas
            System.out.println("1. Órdenes de inspección CERRADAS (recientemente cerradas):\n");
            consultarOrdenesCerradas();

            // 2. Consultar estado de todos los sismógrafos
            System.out.println("\n2. Estado actual de todos los sismógrafos:\n");
            consultarSismografos();

            // 3. Mostrar cambios de estado del último sismógrafo que fue puesto fuera de servicio
            System.out.println("\n3. Historial de cambios de estado:\n");
            consultarCambiosEstado();

        } catch (SQLException e) {
            System.err.println("Error al consultar la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void consultarOrdenesCerradas() throws SQLException {
        String sql = """
            SELECT oi.numero_orden, oi.fecha_hora_cierre, oi.observacion_cierre,
                   e.nombre as estacion_nombre, s.numero_serie,
                   emp.nombre, emp.apellido
            FROM ordenes_inspeccion oi
            JOIN estaciones e ON oi.estacion_id = e.id
            JOIN sismografos s ON s.estacion_id = e.id
            JOIN empleados emp ON oi.responsable_id = emp.id
            WHERE oi.estado = 'CERRADA'
            ORDER BY oi.fecha_hora_cierre DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("  (No hay órdenes cerradas)");
                return;
            }

            while (rs.next()) {
                System.out.println("  📋 Orden: " + rs.getInt("numero_orden"));
                System.out.println("    Estación: " + rs.getString("estacion_nombre"));
                System.out.println("    Sismógrafo (serie): " + rs.getInt("numero_serie"));
                System.out.println("    Responsable: " + rs.getString("nombre") + " " + rs.getString("apellido"));
                System.out.println("    Fecha de cierre: " + rs.getTimestamp("fecha_hora_cierre"));
                System.out.println("    Observación: " + rs.getString("observacion_cierre"));
                System.out.println();
            }
        }
    }

    private static void consultarSismografos() throws SQLException {
        String sql = """
            SELECT s.id, s.numero_serie, s.modelo, s.estado_actual, s.fecha_hora_estado,
                   e.codigo, e.nombre as estacion_nombre
            FROM sismografos s
            JOIN estaciones e ON s.estacion_id = e.id
            ORDER BY s.numero_serie
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("  🔧 Sismógrafo ID=" + rs.getInt("id"));
                System.out.println("    Número de serie: " + rs.getInt("numero_serie"));
                System.out.println("    Modelo: " + rs.getInt("modelo"));
                System.out.println("    Estado: " + rs.getString("estado_actual"));
                System.out.println("    Fecha/hora del estado: " + rs.getTimestamp("fecha_hora_estado"));
                System.out.println("    Estación: [" + rs.getInt("codigo") + "] " + rs.getString("estacion_nombre"));
                System.out.println();
            }
        }
    }

    private static void consultarCambiosEstado() throws SQLException {
        String sql = """
            SELECT ces.id, ces.sismografo_id, ces.fecha_hora, ces.estado, ces.observacion,
                   emp.nombre, emp.apellido, s.numero_serie
            FROM cambios_estado_sismografo ces
            JOIN sismografos s ON ces.sismografo_id = s.id
            LEFT JOIN empleados emp ON ces.empleado_id = emp.id
            ORDER BY ces.fecha_hora DESC
            LIMIT 10
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("  (No hay cambios de estado registrados)");
                return;
            }

            while (rs.next()) {
                System.out.println("  📝 ID cambio: " + rs.getInt("id"));
                System.out.println("    Sismógrafo (serie): " + rs.getInt("numero_serie"));
                System.out.println("    Nuevo estado: " + rs.getString("estado"));
                System.out.println("    Fecha/hora: " + rs.getTimestamp("fecha_hora"));
                String empleado = rs.getString("nombre");
                if (empleado != null) {
                    System.out.println("    Por: " + empleado + " " + rs.getString("apellido"));
                }
                System.out.println("    Observación: " + rs.getString("observacion"));
                System.out.println();
            }
        }
    }
}
