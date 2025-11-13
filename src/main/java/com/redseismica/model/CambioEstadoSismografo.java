package com.redseismica.model;

import com.redseismica.states.EstadoSismografo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa un período durante el cual el sismógrafo se encuentra en un
 * determinado estado. Contiene la fecha/hora de inicio y fin de la vigencia,
 * el estado concreto, los motivos asociados al cambio (en caso de ser una
 * transición a Fuera de Servicio), un comentario y el empleado responsable
 * del registro. Cuando la fecha de finalización es nula indica que el
 * cambio está vigente.
 */
public class CambioEstadoSismografo {
    private final LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private List<MotivoFueraServicio> motivos;

    public CambioEstadoSismografo(LocalDateTime inicio) {
        this.fechaHoraInicio = inicio;
        this.motivos = new java.util.ArrayList<>();
    }
    public boolean sosActual() {
        return fechaHoraFin == null;
    }

    public void setFechaHoraFin(LocalDateTime fin) {
        this.fechaHoraFin = fin;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public List<MotivoFueraServicio> getMotivos() {
        return motivos;
    }

    public void crearMotivoFueraServicio(MotivoTipo motivo, String comentario) {
        MotivoFueraServicio mfs = new MotivoFueraServicio(motivo, comentario);
        motivos.add(mfs);
    }
}