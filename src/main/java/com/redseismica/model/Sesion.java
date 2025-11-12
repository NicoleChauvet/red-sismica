package com.redseismica.model;

import java.time.LocalDateTime;

/**
 * Sesión de un usuario autenticado en el sistema. Se registra la fecha de
 * inicio y, opcionalmente, la fecha de fin. La sesión permite recuperar
 * rápidamente el responsable de inspecciones logueado para filtrar sus
 * órdenes.
 */
public class Sesion {
    private final LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private final Usuario usuario;

    public Sesion(Usuario usuario) {
        this.fechaHoraInicio = LocalDateTime.now();
        this.usuario = usuario;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void cerrarSesion() {
        this.fechaHoraFin = LocalDateTime.now();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Obtiene el responsable de inspecciones logueado. Devuelve el empleado
     * asociado al usuario de la sesión.
     */
    public Empleado obtenerRILogueado() {
        return usuario != null ? usuario.getRILogueado() : null;
    }
}