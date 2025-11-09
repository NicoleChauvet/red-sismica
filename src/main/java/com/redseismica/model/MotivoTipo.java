package com.redseismica.model;

/**
 * Representa un motivo genérico por el cual se puede poner un
 * sismógrafo fuera de servicio. Cada motivo tiene una descripción
 * legible que se mostrará en la interfaz de usuario. Esta clase
 * encapsula simplemente el texto y se utiliza dentro de MotivoFueraServicio
 * para asociar comentarios específicos al motivo.
 */
public class MotivoTipo {

    /** descripción corta del motivo */
    private final String descripcion;

    /**
     * Crea un tipo de motivo con la descripción indicada.
     *
     * @param descripcion texto que describe el motivo
     */
    public MotivoTipo(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve la descripción del motivo.
     *
     * @return descripción textual
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Para mostrar el motivo en controles como JList se usa la
     * descripción. Al sobrescribir toString() facilitamos que la
     * descripción se utilice automáticamente.
     */
    @Override
    public String toString() {
        return descripcion;
    }
}