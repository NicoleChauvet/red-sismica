package com.redseismica.database.dao;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.model.MotivoTipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para acceder a los motivos tipo desde la base de datos.
 */
public class MotivoTipoDAO {

    /**
     * Obtiene todos los motivos tipo disponibles.
     *
     * @return lista de motivos tipo
     * @throws SQLException si hay error en la consulta
     */
    public static List<MotivoTipo> findAll() throws SQLException {
        List<MotivoTipo> motivos = new ArrayList<>();
        String sql = "SELECT * FROM motivos_tipo ORDER BY descripcion";

        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            MotivoTipo motivo = new MotivoTipo(rs.getString("descripcion"));
            motivos.add(motivo);
        }

        rs.close();
        stmt.close();
        return motivos;
    }
}
