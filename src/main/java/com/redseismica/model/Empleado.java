package com.redseismica.model;

/**
 * Representa a un empleado del sistema. Se almacena su nombre, apellido,
 * dirección de correo electrónico, teléfono y rol. El rol define qué
 * acciones puede realizar, como por ejemplo ser responsable de reparaciones.
 */
public class Empleado {
    private final String nombre;
    private final String apellido;
    private final String mail;
    private final String telefono;
    private final Rol rol;

    public Empleado(String nombre, String apellido, String mail, String telefono, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.telefono = telefono;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getMail() {
        return mail;
    }

    public String getTelefono() {
        return telefono;
    }

    public Rol getRol() {
        return rol;
    }

    /**
     * Devuelve true si el empleado es responsable de reparaciones. En este
     * ejemplo se comprueba el nombre del rol de forma simple, pero en un
     * sistema real se podrían tener permisos explícitos.
     */
    public boolean sosResponsableReparacion() {
        return rol != null && "ResponsableReparacion".equalsIgnoreCase(rol.getNombre());
    }
}