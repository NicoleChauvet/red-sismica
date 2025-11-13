package com.redseismica.database.dao;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.model.Sismografo;
import com.redseismica.model.EstacionSismologica;
import com.redseismica.model.CambioEstadoSismografo;
import com.redseismica.states.EstadoSismografo;
import com.redseismica.states.Online;
import com.redseismica.states.FueraDeServicio;
import com.redseismica.states.InhabilitadoPorInspeccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para acceder a los sismógrafos desde la base de datos.
 * Permite actualizar el estado del sismógrafo cuando cambia.
 */
public class SismografoDAO {

    /**
     * Actualiza el estado actual de un sismógrafo en la base de datos.
     *
     * @param sismografo sismógrafo con el estado actualizado
     * @throws SQLException si hay error en la actualización
     */
    public static void updateEstado(Sismografo sismografo) throws SQLException {
        String sql = """
            UPDATE sismografos 
            SET estado_actual = ?, fecha_hora_estado = ?
            WHERE id = ?
        """;

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, sismografo.getEstadoActual().getNombreEstado());
        // Buscar el cambio de estado actual en el sismógrafo (si existe)
        java.time.LocalDateTime ts = java.time.LocalDateTime.now();
        if (sismografo.getCambiosEstado() != null) {
            for (com.redseismica.model.CambioEstadoSismografo ce : sismografo.getCambiosEstado()) {
                if (ce != null && ce.sosActual()) {
                    if (ce.getFechaHoraInicio() != null) ts = ce.getFechaHoraInicio();
                    break;
                }
            }
        }
        pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(ts));
        pstmt.setInt(3, sismografo.getIdSismografo());
        
        pstmt.executeUpdate();
        pstmt.close();
    }

    /**
     * Inserta un nuevo cambio de estado en el historial de cambios del sismógrafo.
     *
     * @param sismografoId ID del sismógrafo
     * @param estado nombre del nuevo estado
     * @param fechaHora fecha y hora del cambio
     * @param observacion observación del cambio
     * @param empleadoId ID del empleado responsable (puede ser null)
     * @throws SQLException si hay error en la inserción
     */
    public static void insertCambioEstado(int sismografoId, String estado, 
                                         java.time.LocalDateTime fechaHora, 
                                         String observacion, Integer empleadoId) throws SQLException {
        String sql = """
            INSERT INTO cambios_estado_sismografo 
            (sismografo_id, fecha_hora, estado, observacion, empleado_id) 
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sismografoId);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(fechaHora));
            pstmt.setString(3, estado);
            pstmt.setString(4, observacion);
            
            if (empleadoId != null) {
                pstmt.setInt(5, empleadoId);
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Obtiene todos los sismógrafos junto a su estación y estado.
     *
     * @return lista de sismógrafos
     * @throws SQLException si hay error en la consulta
     */
    public static List<Sismografo> findAll() throws SQLException {
        List<Sismografo> lista = new ArrayList<>();
        String sql = """
            SELECT s.id, s.numero_serie, s.fecha_instalacion, s.modelo, s.estado_actual, s.fecha_hora_estado,
                   e.codigo, e.nombre as estacion_nombre, e.latitud, e.longitud
            FROM sismografos s
            LEFT JOIN estaciones e ON s.estacion_id = e.id
            ORDER BY s.numero_serie
        """;

        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            EstadoSismografo estado = getEstadoFromString(rs.getString("estado_actual"));

            Sismografo s = new Sismografo(
                rs.getInt("id"),
                rs.getTimestamp("fecha_instalacion").toLocalDateTime(),
                rs.getInt("numero_serie"),
                null,
                estado
            );

            java.sql.Timestamp fh = rs.getTimestamp("fecha_hora_estado");
            if (fh != null) {
                CambioEstadoSismografo ce = new CambioEstadoSismografo(fh.toLocalDateTime());
                s.setCambioEstado(ce);
            }

            new EstacionSismologica(
                rs.getInt("codigo"),
                rs.getString("estacion_nombre"),
                rs.getDouble("latitud"),
                rs.getDouble("longitud"),
                s
            );

            // Nota: el modelo actualmente no provee un setter explícito para la
            // estación dentro de Sismografo, por compatibilidad reproducimos el
            // patrón usado en otros DAOs (se crea la Estación con referencia al
            // sismógrafo). Si se necesita que `s.getEstacionSismologica()` no sea
            // null, agregar un setter en la clase `Sismografo`.

            lista.add(s);
        }

        rs.close();
        stmt.close();
        return lista;
    }

    private static EstadoSismografo getEstadoFromString(String estado) {
        if (estado == null) return new Online("Online");
        return switch (estado) {
            case "Online" -> new Online("Online");
            case "FueraDeServicio" -> new FueraDeServicio("Fuera de Servicio");
            case "InhabilitadoPorInspeccion" -> new InhabilitadoPorInspeccion("Inhabilitado por inspección");
            default -> new Online(estado);
        };
    }
}
