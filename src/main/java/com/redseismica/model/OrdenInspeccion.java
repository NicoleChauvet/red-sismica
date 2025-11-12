package com.redseismica.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo de una orden de inspección sobre una estación sismológica. Contiene
 * las fechas de inicio, finalización y cierre, así como la observación
 * registrada al cerrarla. Una orden se asocia a un responsable de
 * inspecciones para permitir filtrar por usuario logueado.
 */
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

    /**
     * Indica si la orden pertenece al responsable logueado. Compara por nombre
     * y apellido para evitar problemas de referencias de objetos distintos
     * que representan el mismo empleado.
     *
     * @param ri empleado logueado
     * @return true si el responsable de la orden es el mismo empleado
     */
    public boolean esDeRILogueado(Empleado ri) {
        if (ri == null || responsableInspeccion == null) {
            return false;
        }
        return responsableInspeccion.getNombre().equalsIgnoreCase(ri.getNombre())
                && responsableInspeccion.getApellido().equalsIgnoreCase(ri.getApellido());
    }

    /**
     * Comprueba si la orden está completamente realizada, según se define en
     * la documentación. En este ejemplo se evalúa el estado.
     *
     * @return true si el estado es COMPLETAMENTE_REALIZADA
     */
    public boolean esCompletamenteRealizada() {
        return this.estado.sosCompletamenteRealizada();
    }

    /**
     * Cierra la orden asignándole la fecha/hora de cierre, la observación y
     * actualizando el estado a CERRADA.
     *
     * @param fechaCierre  instante de cierre
     * @param observacion texto ingresado por el usuario
     */
    public void cerrar(LocalDateTime fechaCierre, String observacion, Estado estado) {
        this.setFechaHoraCierre(fechaCierre);
        this.setObservacionCierre(observacion);
        this.setEstado(estado);
    }

    /**
     * Solicita a la estación asociada que ponga su sismógrafo fuera de
     * servicio con los datos especificados.
     *
     * @param fechaHora   instante de cambio de estado
     * @param motivos     lista de motivos seleccionados
     * @param comentario  comentario adicional
     * @param responsable empleado responsable
     */
    public void ponerSismografoFueraDeServicio(LocalDateTime fechaHora,
                                               List<MotivoFueraServicio> motivos,
                                               String comentario,
                                               Empleado responsable) {
        if (estacion != null) {
            estacion.ponerSismografoFueraDeServicio(fechaHora, motivos, comentario, responsable);
        }
    }

    @Override
    public String toString() {
        return "Orden " + nroOrden + " (finalización: " + fechaHoraFinalizacion + ")";
    }
}