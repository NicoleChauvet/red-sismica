package com.redseismica.states;

import com.redseismica.model.Empleado;
import com.redseismica.model.MotivoFueraServicio;
import com.redseismica.model.Sismografo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Estado en el que el sismógrafo está operativo y habilitado para medir.
 * Desde este estado se puede enviar a reparación (cambiando a FueraDeServicio)
 * cuando se detecta un problema, aunque en nuestro caso particular no
 * utilizaremos esta transición directamente. También se puede mantener en
 * línea si la operación solicitada no cambia su estado.
 */
public class Online implements EstadoSismografo {

    @Override
    public String getNombreEstado() {
        return "Online";
    }

    @Override
    public void enviarAReparar(Sismografo sismografo, LocalDateTime fechaHora,
                               List<MotivoFueraServicio> motivos, String comentario,
                               Empleado responsable) {
        // Al enviar un sismógrafo en línea a reparar se pasa a FueraDeServicio.
        // Se delega en el método setEstado del sismógrafo para actualizar el
        // historial de cambios y el estado actual.
        sismografo.setEstado(new FueraDeServicio(), fechaHora, motivos, comentario, responsable);
    }

    @Override
    public void ponerOnLine(Sismografo sismografo, LocalDateTime fechaHora, Empleado responsable) {
        // Ya se encuentra en línea, por lo que no se realiza ningún cambio. En una
        // implementación más completa se podría notificar al usuario.
    }
}