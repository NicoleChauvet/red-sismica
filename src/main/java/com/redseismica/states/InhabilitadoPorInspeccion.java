package com.redseismica.states;

import java.util.List;
import java.time.LocalDateTime;

import com.redseismica.model.CambioEstadoSismografo;
import com.redseismica.model.Empleado;
import com.redseismica.model.Sismografo;
import com.redseismica.model.MotivoTipo;

public class InhabilitadoPorInspeccion extends EstadoSismografo {
    public InhabilitadoPorInspeccion(String nombreEstado) {
        super(nombreEstado);
    }
    
    @Override
    public void enviarAReparar(LocalDateTime fechaHora, List<MotivoTipo> motivos, List<String> comentarios, Empleado empleado, Sismografo sismografo) {
        CambioEstadoSismografo ceActual = obtenerCEActual(sismografo);
        ceActual.setFechaHoraFin(fechaHora);
        crearCE(fechaHora, motivos, comentarios, empleado);
        EstadoSismografo estadoNuevo = new FueraDeServicio("Fuera de Servicio");
        sismografo.setEstadoActual(estadoNuevo);
        sismografo.setCambioEstado(ceActual);
    }

    @Override
    public CambioEstadoSismografo obtenerCEActual(Sismografo sismografo) {
        List<CambioEstadoSismografo> cambiosEstado = sismografo.getCambiosEstado();
        for (CambioEstadoSismografo cambioEstadoSismografo : cambiosEstado) {
            if (cambioEstadoSismografo.sosActual()) {
                return cambioEstadoSismografo;
            }
        }
        return null;
    }

}