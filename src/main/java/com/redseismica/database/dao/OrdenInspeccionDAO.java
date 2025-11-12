package com.redseismica.database.dao;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.model.*;
import com.redseismica.states.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para acceder a las órdenes de inspección desde la base de datos.
 */
public class OrdenInspeccionDAO {

    /**
     * Obtiene todas las órdenes de inspección de un responsable.
     *
     * @param empleadoId ID del empleado responsable
     * @return lista de órdenes de inspección
     * @throws SQLException si hay error en la consulta
     */
    public static List<OrdenInspeccion> findByResponsable(int empleadoId) throws SQLException {
        List<OrdenInspeccion> ordenes = new ArrayList<>();
        String sql = """
            SELECT oi.*, e.codigo, e.nombre as estacion_nombre, e.latitud, e.longitud,
                   s.id as sisId, s.numero_serie, s.fecha_instalacion, s.modelo, 
                   s.estado_actual, s.fecha_hora_estado,
                   emp.id as empId, emp.nombre as empNombre, emp.apellido, emp.mail, emp.telefono,
                   r.nombre as rol_nombre
            FROM ordenes_inspeccion oi
            JOIN estaciones e ON oi.estacion_id = e.id
            JOIN sismografos s ON e.sismografo_id = s.id
            JOIN empleados emp ON oi.responsable_id = emp.id
            JOIN roles r ON emp.rol_id = r.id
            WHERE oi.responsable_id = ?
            ORDER BY oi.fecha_hora_finalizacion ASC
        """;

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, empleadoId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            // Crear Rol
            Rol rol = new Rol(rs.getString("rol_nombre"));

            // Crear Empleado
            Empleado empleado = new Empleado(
                rs.getString("empNombre"),
                rs.getString("apellido"),
                rs.getString("mail"),
                rs.getString("telefono"),
                rol
            );

            // Crear Sismógrafo (estación asignada después)
            Sismografo sismografo = new Sismografo(
                rs.getInt("sisId"),
                rs.getTimestamp("fecha_instalacion").toLocalDateTime(),
                rs.getInt("numero_serie"),
                null
            );

            // Establecer estado del sismógrafo
            EstadoSismografo estado = getEstadoFromString(rs.getString("estado_actual"));
            sismografo.setEstado(
                estado,
                rs.getTimestamp("fecha_hora_estado").toLocalDateTime(),
                null,
                "Estado inicial",
                empleado
            );

            // Crear Estación
            EstacionSismologica estacion = new EstacionSismologica(
                rs.getInt("codigo"),
                rs.getString("estacion_nombre"),
                rs.getDouble("latitud"),
                rs.getDouble("longitud"),
                sismografo
            );

            // Crear Orden de Inspección
            com.redseismica.model.Estado estadoOrden = estadoFromCodigo(rs.getString("estado"));
            OrdenInspeccion orden = new OrdenInspeccion(
                rs.getInt("numero_orden"),
                rs.getTimestamp("fecha_hora_emision").toLocalDateTime(),
                rs.getTimestamp("fecha_hora_finalizacion") != null ? 
                    rs.getTimestamp("fecha_hora_finalizacion").toLocalDateTime() : null,
                estadoOrden,
                estacion,
                empleado
            );

            // Si está cerrada, establecer datos de cierre
            Timestamp fechaCierre = rs.getTimestamp("fecha_hora_cierre");
            if (fechaCierre != null) {
                String observacion = rs.getString("observacion_cierre");
                orden.cerrar(fechaCierre.toLocalDateTime(), observacion, estadoOrden);
            }

            ordenes.add(orden);
        }

        rs.close();
        pstmt.close();
        return ordenes;
    }

    /**
     * Obtiene todas las órdenes de inspección del sistema.
     *
     * @return lista de todas las órdenes
     * @throws SQLException si hay error en la consulta
     */
    public static List<OrdenInspeccion> findAll() throws SQLException {
        List<OrdenInspeccion> ordenes = new ArrayList<>();
        String sql = """
            SELECT oi.*, e.codigo, e.nombre as estacion_nombre, e.latitud, e.longitud,
                   s.id as sisId, s.numero_serie, s.fecha_instalacion, s.modelo, 
                   s.estado_actual, s.fecha_hora_estado,
                   emp.id as empId, emp.nombre as empNombre, emp.apellido, emp.mail, emp.telefono,
                   r.nombre as rol_nombre
            FROM ordenes_inspeccion oi
            JOIN estaciones e ON oi.estacion_id = e.id
            JOIN sismografos s ON e.sismografo_id = s.id
            JOIN empleados emp ON oi.responsable_id = emp.id
            JOIN roles r ON emp.rol_id = r.id
            ORDER BY oi.fecha_hora_finalizacion ASC
        """;

        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            // Crear Rol
            Rol rol = new Rol(rs.getString("rol_nombre"));

            // Crear Empleado
            Empleado empleado = new Empleado(
                rs.getString("empNombre"),
                rs.getString("apellido"),
                rs.getString("mail"),
                rs.getString("telefono"),
                rol
            );

            // Crear Sismógrafo (estación asignada después)
            Sismografo sismografo = new Sismografo(
                rs.getInt("sisId"),
                rs.getTimestamp("fecha_instalacion").toLocalDateTime(),
                rs.getInt("numero_serie"),
                null
            );

            // Establecer estado del sismógrafo
            EstadoSismografo estado = getEstadoFromString(rs.getString("estado_actual"));
            sismografo.setEstado(
                estado,
                rs.getTimestamp("fecha_hora_estado").toLocalDateTime(),
                null,
                "Estado inicial",
                empleado
            );

            // Crear Estación
            EstacionSismologica estacion = new EstacionSismologica(
                rs.getInt("codigo"),
                rs.getString("estacion_nombre"),
                rs.getDouble("latitud"),
                rs.getDouble("longitud"),
                sismografo
            );

            // Crear Orden de Inspección
            com.redseismica.model.Estado estadoOrden = estadoFromCodigo(rs.getString("estado"));
            OrdenInspeccion orden = new OrdenInspeccion(
                rs.getInt("numero_orden"),
                rs.getTimestamp("fecha_hora_emision").toLocalDateTime(),
                rs.getTimestamp("fecha_hora_finalizacion") != null ? 
                    rs.getTimestamp("fecha_hora_finalizacion").toLocalDateTime() : null,
                estadoOrden,
                estacion,
                empleado
            );

            // Si la orden ya tiene datos de cierre en la BD, reflejarlos
            Timestamp fechaCierreAll = rs.getTimestamp("fecha_hora_cierre");
            if (fechaCierreAll != null) {
                String observacion = rs.getString("observacion_cierre");
                orden.cerrar(fechaCierreAll.toLocalDateTime(), observacion, estadoOrden);
            }

            ordenes.add(orden);
        }

        rs.close();
        stmt.close();
        return ordenes;
    }

    private static EstadoSismografo getEstadoFromString(String estado) {
        return switch (estado) {
            case "Online" -> new Online();
            case "FueraDeServicio" -> new FueraDeServicio();
            case "InhabilitadoPorInspeccion" -> new InhabilitadoPorInspeccion();
            default -> new Online();
        };
    }

    /**
     * Actualiza una orden de inspección en la base de datos.
     *
     * @param orden orden a actualizar
     * @throws SQLException si hay error en la actualización
     */
    public static void update(OrdenInspeccion orden) throws SQLException {
        String sql = """
            UPDATE ordenes_inspeccion 
            SET estado = ?, fecha_hora_cierre = ?, observacion_cierre = ?
            WHERE numero_orden = ?
        """;

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
    pstmt.setString(1, codigoFromEstado(orden.getEstado()));
        pstmt.setTimestamp(2, orden.getFechaHoraCierre() != null ? 
            Timestamp.valueOf(orden.getFechaHoraCierre()) : null);
        pstmt.setString(3, orden.getObservacionCierre());
        pstmt.setInt(4, orden.getNroOrden());
        
        pstmt.executeUpdate();
        pstmt.close();
    }

    /**
     * Convierte el código almacenado en la BD a una instancia de Estado
     * que usa la aplicación (la clase Estado del modelo almacena el nombre
     * legible, por eso hacemos el mapeo).
     */
    private static com.redseismica.model.Estado estadoFromCodigo(String codigo) {
        if (codigo == null) return null;
        return switch (codigo) {
            case "CERRADA" -> new com.redseismica.model.Estado("Cerrada");
            case "EN_CURSO" -> new com.redseismica.model.Estado("En curso");
            case "COMPLETAMENTE_REALIZADA" -> new com.redseismica.model.Estado("Completamente Realizada");
            default -> new com.redseismica.model.Estado(codigo);
        };
    }

    /**
     * Convierte una instancia de Estado (con nombre legible) al código que
     * se persiste en la BD. Se intenta mapear los nombres conocidos; si no
     * se reconoce, se genera un código a partir del nombre legible.
     */
    private static String codigoFromEstado(com.redseismica.model.Estado estado) {
        if (estado == null) return null;
        String nombre = estado.getNombre();
        if (nombre == null) return null;
        return switch (nombre) {
            case "Cerrada" -> "CERRADA";
            case "En curso" -> "EN_CURSO";
            case "Completamente Realizada" -> "COMPLETAMENTE_REALIZADA";
            default -> nombre.toUpperCase().replace(' ', '_');
        };
    }
}
