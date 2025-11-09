package com.redseismica.model;

/**
 * Esta clase representa una selección de motivo realizada por el
 * responsable de inspecciones al cerrar una orden. Incluye el tipo
 * predefinido y un comentario libre que complementa la información.
 */
public class MotivoFueraServicio {

    /** Tipo de motivo seleccionado */
    private final MotivoTipo tipo;

    /** Comentario adicional asociado al motivo */
    private final String comentario;

    /**
     * Construye un motivo fuera de servicio a partir del tipo y un
     * comentario opcional. Es responsabilidad del controlador asegurar
     * que el comentario no sea nulo cuando corresponda.
     *
     * @param tipo       tipo de motivo predefinido
     * @param comentario comentario adicional del usuario
     */
    public MotivoFueraServicio(MotivoTipo tipo, String comentario) {
        this.tipo = tipo;
        this.comentario = comentario;
    }

    /**
     * Obtiene el tipo de motivo seleccionado.
     *
     * @return motivo seleccionado
     */
    public MotivoTipo getTipo() {
        return tipo;
    }

    /**
     * Obtiene el comentario asociado al motivo.
     *
     * @return texto ingresado por el usuario
     */
    public String getComentario() {
        return comentario;
    }
}