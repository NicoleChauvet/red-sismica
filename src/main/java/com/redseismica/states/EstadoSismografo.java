package com.redseismica.states;

import java.time.LocalDateTime;
import java.util.List;

import com.redseismica.model.CambioEstadoSismografo;
import com.redseismica.model.Empleado;
import com.redseismica.model.MotivoTipo;
import com.redseismica.model.Sismografo;

public abstract class EstadoSismografo {
    private String nombreEstado;

    public EstadoSismografo(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public void enviarAReparar(LocalDateTime fechaHora, List<MotivoTipo> motivos, List<String> comentarios, Empleado empleado, Sismografo sismografo) {
        throw new UnsupportedOperationException("Operación no permitida en el estado actual.");   
    }

    public CambioEstadoSismografo obtenerCEActual(Sismografo sismografos) {
        throw new UnsupportedOperationException("Operación no permitida en el estado actual.");
    }

    public void crearCE(LocalDateTime fechaHora, List<MotivoTipo> motivos, List<String> comentarios, Empleado responsable) {
        CambioEstadoSismografo ce = new CambioEstadoSismografo(fechaHora);
        for (String string : comentarios) {
            for (MotivoTipo motivo : motivos) {
                ce.crearMotivoFueraServicio(motivo, string);
            }
        }
    }
}
