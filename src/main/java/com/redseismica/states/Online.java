package com.redseismica.states;

import java.time.LocalDateTime;
import java.util.List;

import com.redseismica.model.CambioEstadoSismografo;
import com.redseismica.model.Empleado;
import com.redseismica.model.MotivoTipo;
import com.redseismica.model.Sismografo;

public class Online extends EstadoSismografo {

    public Online(String nombreEstado) {
        super(nombreEstado);
    }
    
    @Override
    public void enviarAReparar(LocalDateTime fechaHora, List<MotivoTipo> motivos, List<String> comentarios, Empleado empleado, Sismografo sismografo) {
        // Crear cambio de estado para este sismógrafo
        CambioEstadoSismografo ceNuevo = new CambioEstadoSismografo(fechaHora);
        
        // Agregar los motivos fuera de servicio
        for (int i = 0; i < motivos.size(); i++) {
            MotivoTipo motivo = motivos.get(i);
            String comentario = i < comentarios.size() ? comentarios.get(i) : "";
            ceNuevo.crearMotivoFueraServicio(motivo, comentario);
        }
        
        // Cambiar estado del sismógrafo a "Fuera de Servicio"
        EstadoSismografo estadoNuevo = new FueraDeServicio("Fuera de Servicio");
        sismografo.setEstadoActual(estadoNuevo);
        sismografo.setCambioEstado(ceNuevo);
    }

    @Override
    public CambioEstadoSismografo obtenerCEActual(Sismografo sismografo) {
        List<CambioEstadoSismografo> cambiosEstado = sismografo.getCambiosEstado();
        if (cambiosEstado != null && !cambiosEstado.isEmpty()) {
            for (CambioEstadoSismografo cambioEstadoSismografo : cambiosEstado) {
                if (cambioEstadoSismografo.sosActual()) {
                    return cambioEstadoSismografo;
                }
            }
        }
        return null;
    }
}