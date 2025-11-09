package com.redseismica.states;

import com.redseismica.model.Empleado;
import com.redseismica.model.MotivoFueraServicio;
import com.redseismica.model.Sismografo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface del patrón State para modelar el comportamiento de un sismógrafo en
 * diferentes situaciones. Cada estado concreto definirá qué acciones son
 * permitidas o no en función de su naturaleza. Por ejemplo, desde el estado
 * InhabilitadoPorInspeccion se puede pasar a FueraDeServicio o a OnLine, pero
 * desde FueraDeServicio sólo se puede volver a OnLine una vez reparado.
 */
public interface EstadoSismografo {

    /**
     * Devuelve el nombre legible del estado. Se utiliza para mostrar al
     * usuario y para registrar cambios de estado.
     *
     * @return nombre del estado
     */
    String getNombreEstado();

    /**
     * Envía el sismógrafo a reparar, pasando al estado FueraDeServicio. Cada
     * estado concreto decide si la transición es válida. Si la transición no
     * está permitida se puede lanzar una excepción o ignorar la llamada.
     *
     * @param sismografo instancia sobre la que opera el estado
     * @param fechaHora  fecha y hora de la operación
     * @param motivos    motivos seleccionados para el cambio de estado
     * @param comentario comentario del operador
     * @param responsable empleado que realiza la acción
     */
    void enviarAReparar(Sismografo sismografo,
                        LocalDateTime fechaHora,
                        List<MotivoFueraServicio> motivos,
                        String comentario,
                        Empleado responsable);

    /**
     * Pone el sismógrafo en línea. Esta operación termina el estado de
     * inhabilitación y marca el inicio de un nuevo estado OnLine. Cada estado
     * concreto valida si la transición es posible. Si no lo es se debe
     * gestionar adecuadamente.
     *
     * @param sismografo instancia sobre la que opera el estado
     * @param fechaHora  fecha y hora de la operación
     * @param responsable empleado que realiza la acción
     */
    void ponerOnLine(Sismografo sismografo,
                     LocalDateTime fechaHora,
                     Empleado responsable);
}