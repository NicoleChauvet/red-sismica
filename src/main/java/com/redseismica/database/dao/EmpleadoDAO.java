package com.redseismica.database.dao;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.model.Empleado;
import com.redseismica.model.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para acceder a los empleados desde la base de datos.
 */
public class EmpleadoDAO {

    /**
     * Obtiene un empleado por su ID.
     *
     * @param id ID del empleado
     * @return empleado encontrado o null
     * @throws SQLException si hay error en la consulta
     */
    public static Empleado findById(int id) throws SQLException {
        String sql = """
            SELECT e.*, r.nombre as rol_nombre
            FROM empleados e
            JOIN roles r ON e.rol_id = r.id
            WHERE e.id = ?
        """;

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        Empleado empleado = null;
        if (rs.next()) {
            Rol rol = new Rol(rs.getString("rol_nombre"));
            empleado = new Empleado(
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("mail"),
                rs.getString("telefono"),
                rol
            );
        }

        rs.close();
        pstmt.close();
        return empleado;
    }

    /**
     * Obtiene todos los empleados del sistema.
     *
     * @return lista de empleados
     * @throws SQLException si hay error en la consulta
     */
    public static List<Empleado> findAll() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = """
            SELECT e.*, r.nombre as rol_nombre
            FROM empleados e
            JOIN roles r ON e.rol_id = r.id
        """;

        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Rol rol = new Rol(rs.getString("rol_nombre"));
            Empleado empleado = new Empleado(
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("mail"),
                rs.getString("telefono"),
                rol
            );
            empleados.add(empleado);
        }

        rs.close();
        stmt.close();
        return empleados;
    }

    /**
     * Obtiene todos los responsables de reparación.
     *
     * @return lista de responsables de reparación
     * @throws SQLException si hay error en la consulta
     */
    public static List<Empleado> findResponsablesReparacion() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = """
            SELECT e.*, r.nombre as rol_nombre
            FROM empleados e
            JOIN roles r ON e.rol_id = r.id
            WHERE r.nombre = 'ResponsableReparacion'
        """;

        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Rol rol = new Rol(rs.getString("rol_nombre"));
            Empleado empleado = new Empleado(
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("mail"),
                rs.getString("telefono"),
                rol
            );
            empleados.add(empleado);
        }

        rs.close();
        stmt.close();
        return empleados;
    }
}
