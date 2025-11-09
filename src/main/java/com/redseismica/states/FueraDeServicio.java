package com.redseismica.states;

import com.redseismica.model.Empleado;
import com.redseismica.model.MotivoFueraServicio;
import com.redseismica.model.Sismografo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Estado en el que el sismógrafo se encuentra fuera de servicio y a la
 * espera de reparación. Desde aquí únicamente se puede regresar a Online
 * una vez finalizadas las reparaciones; intentar enviarlo nuevamente a
 * reparar no tendrá efecto. De este modo se evita registrar duplicados
 * innecesarios de cambios de estado.
 */
public class FueraDeServicio implements EstadoSismografo {

    @Override
    public String getNombreEstado() {
        return "Fuera de Servicio";
    }

    @Override
    public void enviarAReparar(Sismografo sismografo, LocalDateTime fechaHora,
                               List<MotivoFueraServicio> motivos, String comentario,
                               Empleado responsable) {
        // Ya está fuera de servicio; no se realiza ninguna acción adicional.
        // En una aplicación real se podría notificar que la operación es redundante.
    }

    @Override
    public void ponerOnLine(Sismografo sismografo, LocalDateTime fechaHora, Empleado responsable) {
        // Al completarse la reparación el sismógrafo vuelve a estar en línea.
        sismografo.setEstado(new Online(), fechaHora, null,
                "Reparación completada, vuelve a estar en línea", responsable);
    }
}