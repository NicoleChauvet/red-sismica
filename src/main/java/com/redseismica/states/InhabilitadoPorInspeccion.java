package com.redseismica.states;

import com.redseismica.model.Empleado;
import com.redseismica.model.MotivoFueraServicio;
import com.redseismica.model.Sismografo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Estado que representa un sismógrafo inhabilitado temporalmente debido a una
 * inspección. A partir de este estado, al cerrar la orden de inspección, el
 * responsable puede optar por poner el sismógrafo fuera de servicio (para
 * enviarlo a reparación) o bien devolverlo a operación (Online) si no se
 * detectaron fallas.
 */
public class InhabilitadoPorInspeccion implements EstadoSismografo {

    @Override
    public String getNombreEstado() {
        return "Inhabilitado por Inspección";
    }

    @Override
    public void enviarAReparar(Sismografo sismografo, LocalDateTime fechaHora,
                               List<MotivoFueraServicio> motivos, String comentario,
                               Empleado responsable) {
        // Para pasar a FueraDeServicio delegamos en el sismógrafo que creará un
        // nuevo cambio de estado y cerrará el anterior. Se registran los
        // motivos y comentarios proporcionados por el usuario.
        sismografo.setEstado(new FueraDeServicio(), fechaHora, motivos, comentario, responsable);
    }

    @Override
    public void ponerOnLine(Sismografo sismografo, LocalDateTime fechaHora, Empleado responsable) {
        // Si no se requieren reparaciones, el sismógrafo vuelve al estado Online.
        sismografo.setEstado(new Online(), fechaHora, null,
                "Se habilita nuevamente tras la inspección", responsable);
    }
}