package com.redseismica.model;

public class Estado {
    private final String nombre;

    public Estado(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public Void setNombre(String nombre) {
        return null;
    }

    public boolean sosCompletamenteRealizada() {
        return this.nombre.equals("Completamente Realizada");
    }
    
    public boolean sosCerrada() {
        return this.nombre.equals("Cerrada");
    }
}