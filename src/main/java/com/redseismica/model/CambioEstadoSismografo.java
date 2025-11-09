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

    /** Estado que cobra vigencia con este registro */
    private final EstadoSismografo estado;
    private final LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private final List<MotivoFueraServicio> motivos;
    private final String comentario;
    private final Empleado responsable;

    /**
     * Crea un nuevo cambio de estado. Para estados que no requieren motivos
     * puede pasarse null o una lista vacía.
     *
     * @param estado     estado que inicia
     * @param inicio     fecha/hora de inicio de vigencia
     * @param motivos    motivos asociados (pueden ser null)
     * @param comentario comentario libre (puede ser null)
     * @param responsable empleado responsable del cambio
     */
    public CambioEstadoSismografo(EstadoSismografo estado,
                                  LocalDateTime inicio,
                                  List<MotivoFueraServicio> motivos,
                                  String comentario,
                                  Empleado responsable) {
        this.estado = estado;
        this.fechaHoraInicio = inicio;
        this.motivos = motivos;
        this.comentario = comentario;
        this.responsable = responsable;
    }

    /**
     * Indica si este cambio es el vigente (no tiene fecha de fin).
     *
     * @return true si la fecha de fin es null
     */
    public boolean sosActual() {
        return fechaHoraFin == null;
    }

    /**
     * Fija la fecha/hora de finalización de vigencia. Se utiliza al cerrar un
     * cambio de estado previo antes de registrar uno nuevo.
     *
     * @param fin fecha/hora de fin
     */
    public void setFechaHoraFin(LocalDateTime fin) {
        this.fechaHoraFin = fin;
    }

    public EstadoSismografo getEstado() {
        return estado;
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

    public String getComentario() {
        return comentario;
    }

    public Empleado getResponsable() {
        return responsable;
    }
}