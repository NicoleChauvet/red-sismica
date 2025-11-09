package com.redseismica.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa una estación sismológica que aloja un único
 * sismógrafo. Para simplificar el ejemplo se asume una relación 1:1. En
 * sistemas reales podrían existir estaciones con múltiples dispositivos.
 */
public class EstacionSismologica {
    private final int codigoEstacion;
    private final String nombre;
    private final double latitud;
    private final double longitud;
    private final Sismografo sismografo;

    /**
     * Crea una estación sismológica con su sismógrafo asociado.
     *
     * @param codigoEstacion identificador de la estación
     * @param nombre nombre descriptivo
     * @param latitud coordenada geográfica
     * @param longitud coordenada geográfica
     * @param sismografo dispositivo asociado a la estación
     */
    public EstacionSismologica(int codigoEstacion, String nombre,
                               double latitud, double longitud,
                               Sismografo sismografo) {
        this.codigoEstacion = codigoEstacion;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.sismografo = sismografo;
    }

    public int getCodigoEstacion() {
        return codigoEstacion;
    }

    public String getNombre() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public Sismografo getSismografo() {
        return sismografo;
    }

    /**
     * Retorna la identificación del sismógrafo asociado. Se utiliza en la
     * interfaz para mostrar referencias a los dispositivos.
     */
    public int obtenerIDSismografo() {
        return sismografo.getIdSismografo();
    }

    /**
     * Envía el sismógrafo a reparación con los datos especificados. Este
     * método delega directamente en el sismógrafo para cambiar su estado.
     *
     * @param fechaHora   fecha y hora de la transición
     * @param motivos     lista de motivos seleccionados
     * @param comentario  comentario asociado
     * @param responsable empleado responsable
     */
    public void ponerSismografoFueraDeServicio(LocalDateTime fechaHora,
                                               List<MotivoFueraServicio> motivos,
                                               String comentario,
                                               Empleado responsable) {
        sismografo.enviarAReparar(fechaHora, motivos, comentario, responsable);
    }
}