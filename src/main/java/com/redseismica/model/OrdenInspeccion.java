package com.redseismica.model;

import java.time.LocalDateTime;
import java.util.List;

public class OrdenInspeccion {
    private final int nroOrden;
    private final LocalDateTime fechaHoraInicio;
    private final LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraCierre;
    private String observacionCierre;
    private Estado estado;
    private final EstacionSismologica estacion;
    private final Empleado responsableInspeccion;

    public OrdenInspeccion(int nroOrden,
                           LocalDateTime fechaHoraInicio,
                           LocalDateTime fechaHoraFinalizacion,
                           Estado estado,
                           EstacionSismologica estacion,
                           Empleado responsableInspeccion) {
        this.nroOrden = nroOrden;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.estado = estado;
        this.estacion = estacion;
        this.responsableInspeccion = responsableInspeccion;
    }

    public int getNroOrden() {
        return nroOrden;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFinalizacion() {
        return fechaHoraFinalizacion;
    }

    public LocalDateTime getFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public EstacionSismologica getEstacion() {
        return estacion;
    }

    public boolean esDeRILogueado(Empleado ri) {
        if (ri == null || responsableInspeccion == null) {
            return false;
        }
        return responsableInspeccion.getNombre().equalsIgnoreCase(ri.getNombre())
                && responsableInspeccion.getApellido().equalsIgnoreCase(ri.getApellido());
    }

    public boolean esCompletamenteRealizada() {
        return this.estado.sosCompletamenteRealizada();
    }

    public void cerrar(LocalDateTime fechaCierre, String observacion, Estado estado) {
        this.setFechaHoraCierre(fechaCierre);
        this.setObservacionCierre(observacion);
        this.setEstado(estado);
    }

    public void ponerSismografoFueraDeServicio(LocalDateTime fechaHora,
                                               List<MotivoTipo> motivos,
                                               List<String> comentarios,
                                               Empleado responsable) {
        if (estacion != null) {
            estacion.ponerSismografoFueraDeServicio(fechaHora, motivos, comentarios, responsable);
        }
    }

    public List<String> buscarDatosOrdenInspeccion() {
        List<String> datos = new java.util.ArrayList<>();
        
        // Número de orden
        datos.add(String.valueOf(this.getNroOrden()));
        
        // Fecha de finalización
        datos.add(this.getFechaHoraFinalizacion() != null ? this.getFechaHoraFinalizacion().toString() : "");
        
        // Nombre de la estación
        datos.add(this.estacion != null ? this.estacion.getNombre() : "");
        
        // ID del sismógrafo
        if (this.estacion != null) {
            try {
                int idSismografo = this.estacion.obtenerIDSismografo();
                datos.add(idSismografo != -1 ? String.valueOf(idSismografo) : "");
            } catch (Exception e) {
                datos.add("");
            }
        } else {
            datos.add("");
        }
        
        return datos;
    }

    @Override
    public String toString() {
        return "Orden " + nroOrden + " (finalización: " + fechaHoraFinalizacion + ")";
    }
}