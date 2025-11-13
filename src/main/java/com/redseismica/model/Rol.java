package com.redseismica.model;

/**
 * Rol de un empleado dentro del sistema. Se utiliza para identificar a
 * aquellos responsables de reparación y otros perfiles. En este ejemplo
 * sólo se comprueba el nombre textual.
 */
public class Rol {
    private final String nombre;

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean sosResponsableReparacion() {
        return this.nombre.equals("ResponsableReparacion");
    }
}