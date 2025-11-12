package com.redseismica.database.dao;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.model.Sismografo;
import java.sql.*;

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
        
        pstmt.setString(1, sismografo.getEstadoSismografo().getNombreEstado());
        pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(sismografo.obtenerCEActual() != null ? 
            sismografo.obtenerCEActual().getFechaHoraInicio() : java.time.LocalDateTime.now()));
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
}
