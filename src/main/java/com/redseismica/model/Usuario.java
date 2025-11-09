package com.redseismica.model;

/**
 * Usuario del sistema de red sísmica. Está asociado a un empleado que
 * representa su identidad en las operaciones. Sólo el usuario
 * autenticado puede ejecutar casos de uso como cerrar una orden de
 * inspección.
 */
public class Usuario {
    private final String nombreUsuario;
    private final String password;
    private final Empleado empleado;

    public Usuario(String nombreUsuario, String password, Empleado empleado) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.empleado = empleado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public Empleado getEmpleado() {
        return empleado;
    }
}