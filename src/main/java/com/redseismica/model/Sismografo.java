package com.redseismica.model;

import com.redseismica.states.EstadoSismografo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import com.redseismica.database.dao.SismografoDAO;

public class Sismografo {
    private final int idSismografo;
    private final LocalDateTime fechaAdquisicion;
    private final int nroSerie;
    private EstacionSismologica estacionSismologica;
    private EstadoSismografo estadoActual;
    private final List<CambioEstadoSismografo> cambioEstado;

    public Sismografo(int idSismografo, LocalDateTime fechaAdquisicion, int nroSerie, EstacionSismologica estacionSismologica, EstadoSismografo estado) {
        this.idSismografo = idSismografo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.nroSerie = nroSerie;
        this.estacionSismologica = estacionSismologica;
        // estado inicial
        this.estadoActual = estado;
        this.cambioEstado = new java.util.ArrayList<CambioEstadoSismografo>();
    }

    public int getIdSismografo() {
        return idSismografo;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public int getNroSerie() {
        return nroSerie;
    }

    public EstadoSismografo getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoSismografo nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }

    public List<CambioEstadoSismografo> getCambioEstado() {
        return Collections.unmodifiableList(cambioEstado);
    }

    public void setCambioEstado(CambioEstadoSismografo cambioEstadoSismografo) {
        this.cambioEstado.add(cambioEstadoSismografo);
    }

    public List<CambioEstadoSismografo> getCambiosEstado() {
        return cambioEstado;
    }

    public void enviarAReparar(LocalDateTime fechaHora,
                               List<MotivoTipo> motivos,
                               List<String> comentarios,
                               Empleado responsable) {
        estadoActual.enviarAReparar(fechaHora, motivos, comentarios, responsable, this);
    }

    public Sismografo sosMiSismografo(int codigoEstacion) throws SQLException {
    List<Sismografo> listaSismografos = SismografoDAO.findAll();
    for (Sismografo sismografo : listaSismografos)
    {
        if (sismografo.estacionSismologica.getCodigoEstacion() == codigoEstacion)
        {
            return sismografo;
        }
    }
    return null;
    }
}