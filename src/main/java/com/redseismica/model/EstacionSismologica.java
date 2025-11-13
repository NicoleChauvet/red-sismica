package com.redseismica.model;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import com.redseismica.database.dao.SismografoDAO;

public class EstacionSismologica {
    private final int codigoEstacion;
    private final String nombre;
    private final double latitud;
    private final double longitud;
    private final Sismografo sismografo;

    public EstacionSismologica(int codigoEstacion, String nombre,
                               double latitud, double longitud,
                               Sismografo sismografo) {
        this.codigoEstacion = codigoEstacion;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.sismografo = sismografo;
        if (this.sismografo != null) {
            this.sismografo.setEstacionSismologica(this);
        }
    }

    public int getCodigoEstacion() {
        return codigoEstacion;
    }

    public String getNombre() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public Sismografo getSismografo() {
        return sismografo;
    }

    public int obtenerIDSismografo() throws SQLException {
        List<Sismografo> sismografos = SismografoDAO.findAll();
        for (Sismografo s : sismografos) {
            if (s.sosMiSismografo(codigoEstacion)) {
                return s.getIdSismografo();
            }
        }
        return -1;
    }

    public void ponerSismografoFueraDeServicio(LocalDateTime fechaHora,
                                               List<MotivoTipo> motivos,
                                               List<String> comentarios,
                                               Empleado responsable) {
        sismografo.enviarAReparar(fechaHora, motivos, comentarios, responsable);
    }
}