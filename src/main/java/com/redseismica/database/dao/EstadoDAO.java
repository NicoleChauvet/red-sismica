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
		}

		return res;
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
