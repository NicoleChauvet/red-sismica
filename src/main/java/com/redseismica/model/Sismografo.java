package com.redseismica.model;

import com.redseismica.states.EstadoSismografo;
import com.redseismica.states.Online;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sismografo {
    private final int idSismografo;
    private final LocalDateTime fechaAdquisicion;
    private final int nroSerie;
    private EstacionSismologica estacionSismologica;
    private EstadoSismografo estadoSismografo;
    private final List<CambioEstadoSismografo> historialEstados = new ArrayList<>();

    public Sismografo(int idSismografo, LocalDateTime fechaAdquisicion, int nroSerie, EstacionSismologica estacionSismologica) {
        this.idSismografo = idSismografo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.nroSerie = nroSerie;
        this.estacionSismologica = estacionSismologica;
        // estado inicial
        this.estadoSismografo = new Online();
        // registrar cambio inicial
        this.historialEstados.add(new CambioEstadoSismografo(estadoSismografo, fechaAdquisicion,
                null, "Estado inicial", null));
    }

    /**
     * Devuelve el identificador del sismógrafo.
     */
    public int getIdSismografo() {
        return idSismografo;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public int getNroSerie() {
        return nroSerie;
    }

    /**
     * Acceso al estado actual. Se utiliza principalmente por la interfaz de
     * usuario para mostrar el estado al operador.
     */
    public EstadoSismografo getEstadoSismografo() {
        return estadoSismografo;
    }

    /**
     * Devuelve una copia del historial de cambios para evitar su modificación
     * externa.
     */
    public List<CambioEstadoSismografo> getHistorialEstados() {
        return Collections.unmodifiableList(historialEstados);
    }

    /**
     * Crea o cierra un cambio de estado. Este método es interno y se utiliza
     * desde los estados concretos para registrar la transición de forma
     * consistente. Cuando se establece un nuevo estado, se cierra el cambio
     * actual (fecha de fin) y se crea un nuevo registro.
     *
     * @param nuevoEstado nuevo estado a asignar
     * @param fechaHora   instante de la transición
     * @param motivos     lista de motivos (puede ser null)
     * @param comentario  comentario libre (puede ser null)
     * @param responsable responsable de la transición (puede ser null)
     */
    public void setEstado(EstadoSismografo nuevoEstado,
                          LocalDateTime fechaHora,
                          List<MotivoFueraServicio> motivos,
                          String comentario,
                          Empleado responsable) {
        // cerrar el cambio vigente
        CambioEstadoSismografo actual = obtenerCEActual();
        if (actual != null) {
            actual.setFechaHoraFin(fechaHora);
        }
        this.estadoSismografo = nuevoEstado;
        // registrar nuevo cambio
        CambioEstadoSismografo nuevoambio = new CambioEstadoSismografo(nuevoEstado, fechaHora, motivos, comentario, responsable);
        historialEstados.add(nuevoambio);
        
        // Persistir el cambio en la BD (solo actualizar el estado del sismógrafo)
        try {
            com.redseismica.database.dao.SismografoDAO.updateEstado(this);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo persistir el cambio de estado en la BD: " + e.getMessage());
            // No lanzamos excepción para no interrumpir el flujo de la aplicación
        }
    }

    /**
     * Devuelve el cambio de estado vigente (fecha de fin es null). Si no
     * existe un cambio vigente devolverá null, aunque en la práctica siempre
     * debería haber uno.
     */
    public CambioEstadoSismografo obtenerCEActual() {
        for (int i = historialEstados.size() - 1; i >= 0; i--) {
            CambioEstadoSismografo ce = historialEstados.get(i);
            if (ce.sosActual()) {
                return ce;
            }
        }
        return null;
    }

    /**
     * Opera con el estado actual para enviar el sismógrafo a reparación.
     * Delegamos la lógica concreta en el estado actual para respetar el
     * patrón State. Los argumentos se propagan sin modificación.
     */
    public void enviarAReparar(LocalDateTime fechaHora,
                               List<MotivoFueraServicio> motivos,
                               String comentario,
                               Empleado responsable) {
        estadoSismografo.enviarAReparar(this, fechaHora, motivos, comentario, responsable);
    }

    /**
     * Opera con el estado actual para poner el sismógrafo en línea.
     *
     * @param fechaHora   instante de la transición
     * @param responsable responsable de la operación
     */
    public void ponerOnLine(LocalDateTime fechaHora, Empleado responsable) {
        estadoSismografo.ponerOnLine(this, fechaHora, responsable);
    }
}