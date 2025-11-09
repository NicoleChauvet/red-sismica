package com.redseismica.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Clase encargada de cargar datos iniciales en la base de datos.
 * Incluye roles, empleados, usuarios, sismógrafos, estaciones,
 * órdenes de inspección y motivos tipo.
 */
public class DataLoader {

    /**
     * Carga todos los datos iniciales en la base de datos.
     *
     * @throws SQLException si hay un error al insertar los datos
     */
    public static void loadInitialData() throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        // Deshabilitar auto-commit para transacción
        conn.setAutoCommit(false);
        
        try {
            insertRoles(conn);
            insertEmpleados(conn);
            insertUsuarios(conn);
            insertSismografos(conn);
            insertEstaciones(conn);
            insertOrdenesInspeccion(conn);
            insertMotivosTipo(conn);
            
            conn.commit();
            System.out.println("✓ Datos iniciales cargados exitosamente");
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error al cargar datos: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static void insertRoles(Connection conn) throws SQLException {
        String sql = "INSERT INTO roles (nombre) VALUES (?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, "ResponsableInspeccion");
        pstmt.executeUpdate();
        
        pstmt.setString(1, "ResponsableReparacion");
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Roles insertados");
    }

    private static void insertEmpleados(Connection conn) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, apellido, mail, telefono, rol_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Responsable de Inspección
        pstmt.setString(1, "Juan");
        pstmt.setString(2, "Pérez");
        pstmt.setString(3, "juan.perez@empresa.com");
        pstmt.setString(4, "3511234567");
        pstmt.setInt(5, 1); // rol ResponsableInspeccion
        pstmt.executeUpdate();
        
        // Responsables de Reparación
        pstmt.setString(1, "Ana");
        pstmt.setString(2, "García");
        pstmt.setString(3, "ana.garcia@empresa.com");
        pstmt.setString(4, "3512345678");
        pstmt.setInt(5, 2); // rol ResponsableReparacion
        pstmt.executeUpdate();
        
        pstmt.setString(1, "Luis");
        pstmt.setString(2, "Rodríguez");
        pstmt.setString(3, "luis.rodriguez@empresa.com");
        pstmt.setString(4, "3513456789");
        pstmt.setInt(5, 2); // rol ResponsableReparacion
        pstmt.executeUpdate();
        
        pstmt.setString(1, "María");
        pstmt.setString(2, "López");
        pstmt.setString(3, "maria.lopez@empresa.com");
        pstmt.setString(4, "3514567890");
        pstmt.setInt(5, 1); // rol ResponsableInspeccion
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Empleados insertados");
    }

    private static void insertUsuarios(Connection conn) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password, empleado_id) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, "jperez");
        pstmt.setString(2, "password123");
        pstmt.setInt(3, 1); // Juan Pérez
        pstmt.executeUpdate();
        
        pstmt.setString(1, "agarcia");
        pstmt.setString(2, "password123");
        pstmt.setInt(3, 2); // Ana García
        pstmt.executeUpdate();
        
        pstmt.setString(1, "lrodriguez");
        pstmt.setString(2, "password123");
        pstmt.setInt(3, 3); // Luis Rodríguez
        pstmt.executeUpdate();
        
        pstmt.setString(1, "mlopez");
        pstmt.setString(2, "password123");
        pstmt.setInt(3, 4); // María López
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Usuarios insertados");
    }

    private static void insertSismografos(Connection conn) throws SQLException {
        String sql = "INSERT INTO sismografos (numero_serie, fecha_instalacion, modelo, estado_actual, fecha_hora_estado) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        LocalDateTime haceUnAnio = LocalDateTime.now().minusYears(1);
        LocalDateTime haceDosDias = LocalDateTime.now().minusDays(2);
        LocalDateTime haceSeisMeses = LocalDateTime.now().minusMonths(6);
        LocalDateTime haceTresMeses = LocalDateTime.now().minusMonths(3);
        
        // Sismógrafo 1 - Inhabilitado por inspección
        pstmt.setInt(1, 1001);
        pstmt.setTimestamp(2, Timestamp.valueOf(haceUnAnio));
        pstmt.setInt(3, 1);
        pstmt.setString(4, "InhabilitadoPorInspeccion");
        pstmt.setTimestamp(5, Timestamp.valueOf(haceDosDias));
        pstmt.executeUpdate();
        
        // Sismógrafo 2 - Online
        pstmt.setInt(1, 1002);
        pstmt.setTimestamp(2, Timestamp.valueOf(haceSeisMeses));
        pstmt.setInt(3, 2);
        pstmt.setString(4, "Online");
        pstmt.setTimestamp(5, Timestamp.valueOf(haceSeisMeses));
        pstmt.executeUpdate();
        
        // Sismógrafo 3 - Fuera de servicio
        pstmt.setInt(1, 1003);
        pstmt.setTimestamp(2, Timestamp.valueOf(haceTresMeses));
        pstmt.setInt(3, 1);
        pstmt.setString(4, "FueraDeServicio");
        pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().minusDays(10)));
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Sismógrafos insertados");
    }

    private static void insertEstaciones(Connection conn) throws SQLException {
        String sql = "INSERT INTO estaciones (codigo, nombre, latitud, longitud, sismografo_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setInt(1, 101);
        pstmt.setString(2, "Estación Central");
        pstmt.setDouble(3, -31.4167);
        pstmt.setDouble(4, -64.1833);
        pstmt.setInt(5, 1);
        pstmt.executeUpdate();
        
        pstmt.setInt(1, 102);
        pstmt.setString(2, "Estación Norte");
        pstmt.setDouble(3, -31.3500);
        pstmt.setDouble(4, -64.2000);
        pstmt.setInt(5, 2);
        pstmt.executeUpdate();
        
        pstmt.setInt(1, 103);
        pstmt.setString(2, "Estación Sur");
        pstmt.setDouble(3, -31.5000);
        pstmt.setDouble(4, -64.1500);
        pstmt.setInt(5, 3);
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Estaciones insertadas");
    }

    private static void insertOrdenesInspeccion(Connection conn) throws SQLException {
        String sql = "INSERT INTO ordenes_inspeccion (numero_orden, fecha_hora_emision, fecha_hora_finalizacion, fecha_hora_cierre, estado, observacion_cierre, estacion_id, responsable_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Orden 1 - Completamente realizada (puede cerrarse)
        pstmt.setInt(1, 1);
        pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays(5)));
        pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().minusDays(3)));
        pstmt.setTimestamp(4, null);
        pstmt.setString(5, "COMPLETAMENTE_REALIZADA");
        pstmt.setString(6, null);
        pstmt.setInt(7, 1); // Estación Central
        pstmt.setInt(8, 1); // Juan Pérez
        pstmt.executeUpdate();
        
        // Orden 2 - En curso (no debe aparecer para cerrar)
        pstmt.setInt(1, 2);
        pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays(4)));
        pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().minusDays(2)));
        pstmt.setTimestamp(4, null);
        pstmt.setString(5, "EN_CURSO");
        pstmt.setString(6, null);
        pstmt.setInt(7, 1); // Estación Central
        pstmt.setInt(8, 1); // Juan Pérez
        pstmt.executeUpdate();
        
        // Orden 3 - Completamente realizada (otra orden que puede cerrarse)
        pstmt.setInt(1, 3);
        pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays(7)));
        pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().minusDays(4)));
        pstmt.setTimestamp(4, null);
        pstmt.setString(5, "COMPLETAMENTE_REALIZADA");
        pstmt.setString(6, null);
        pstmt.setInt(7, 2); // Estación Norte
        pstmt.setInt(8, 1); // Juan Pérez
        pstmt.executeUpdate();
        
        // Orden 4 - Cerrada (ya procesada anteriormente)
        pstmt.setInt(1, 4);
        pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays(15)));
        pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().minusDays(13)));
        pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().minusDays(12)));
        pstmt.setString(5, "CERRADA");
        pstmt.setString(6, "Inspección completada sin novedades");
        pstmt.setInt(7, 3); // Estación Sur
        pstmt.setInt(8, 4); // María López
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Órdenes de inspección insertadas");
    }

    private static void insertMotivosTipo(Connection conn) throws SQLException {
        String sql = "INSERT INTO motivos_tipo (descripcion) VALUES (?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, "Sensor dañado");
        pstmt.executeUpdate();
        
        pstmt.setString(1, "Cable cortado");
        pstmt.executeUpdate();
        
        pstmt.setString(1, "Pérdida de calibración");
        pstmt.executeUpdate();
        
        pstmt.setString(1, "Batería agotada");
        pstmt.executeUpdate();
        
        pstmt.setString(1, "Interferencia electromagnética");
        pstmt.executeUpdate();
        
        pstmt.setString(1, "Problema de conectividad");
        pstmt.executeUpdate();
        
        pstmt.close();
        System.out.println("  - Motivos tipo insertados");
    }
}
