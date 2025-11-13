package com.redseismica.database.dao;

import com.redseismica.database.DatabaseConfig;
import com.redseismica.model.Estado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO dedicado a operaciones relacionadas con los códigos/definiciones
 * de estados persistidos en la base de datos. Aquí centralizamos la
 * lógica de consulta para que los controladores no usen SQL directo.
 */
public class EstadoDAO {

	public static List<Estado> findAll() throws SQLException {
		String sql = "SELECT DISTINCT estado FROM ordenes_inspeccion WHERE estado IS NOT NULL "
				+ "UNION SELECT DISTINCT estado FROM cambios_estado_sismografo WHERE estado IS NOT NULL "
				+ "UNION SELECT DISTINCT estado FROM sismografos WHERE estado_actual IS NOT NULL";
		List<Estado> res = new ArrayList<>();

		try (Connection conn = DatabaseConfig.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				String codigo = rs.getString(1);
				Estado estado = mapCodigoToEstado(codigo);
				if (estado != null) res.add(estado);
			}
			return res;
		} catch (SQLException ex) {
			// Si la consulta falla por esquema o por versiones de la BD,
			// devolvemos una lista por defecto para mantener la aplicación
			// usable y sugerir al usuario revisar/migrar la base.
			System.err.println("EstadoDAO.findAll: error al consultar estados en la BD: " + ex.getMessage());
			System.err.println("EstadoDAO.findAll: devolviendo lista por defecto. Recomendado: revisar script de migración/poblado.");
			List<Estado> defaults = new ArrayList<>();
			defaults.add(new Estado("Cerrada"));
			defaults.add(new Estado("En curso"));
			defaults.add(new Estado("Completamente Realizada"));
			defaults.add(new Estado("Online"));
			defaults.add(new Estado("Fuera de Servicio"));
			defaults.add(new Estado("Inhabilitado por inspección"));
			return defaults;
		}
	}

	private static Estado mapCodigoToEstado(String codigo) {
		if (codigo == null) return null;
		return switch (codigo) {
			case "CERRADA" -> new Estado("Cerrada");
			case "EN_CURSO" -> new Estado("En curso");
			case "COMPLETAMENTE_REALIZADA" -> new Estado("Completamente Realizada");
			default -> new Estado(codigo);
		};
	}
}
