package com.redseismica.model;

public class MotivoFueraServicio {
    private  String comentario;
    private MotivoTipo motivoTipo;

    public MotivoFueraServicio(MotivoTipo motivoTipo, String comentario) {
        this.motivoTipo = motivoTipo;
        this.comentario = comentario;
    }

    public String getComentario() {
        return comentario;
    }

    public MotivoTipo getTipo() {
        return motivoTipo;
    }

}