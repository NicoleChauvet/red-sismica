package com.redseismica.controller;

import com.redseismica.model.*;
import com.redseismica.states.InhabilitadoPorInspeccion;
import com.redseismica.view.PantallaAdmInspecciones;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador que orquesta la ejecución del caso de uso “Cerrar Orden de
 * Inspección”. Sus responsabilidades incluyen buscar las órdenes del
 * responsable logueado, gestionar la selección de la orden, recoger la
 * observación y los motivos, validar los datos y persistir los cambios en
 * las entidades. También se encarga de notificar a los actores interesados
 * mediante correo electrónico y la pantalla del CCRS.
 */
public class GestorAdmInspeccion {
    private final Sesion sesionActiva;
    private PantallaAdmInspecciones pantalla;
    private final PantallaCCRS pantallaCCRS = new PantallaCCRS();
    private final InterfazMail servicioMail = new InterfazMail();

    /**
     * Lista completa de órdenes disponibles en el sistema. En un sistema real
     * se recuperaría desde una base de datos. Para la demostración se
     * proporciona por el constructor.
     */
    private final List<OrdenInspeccion> todasLasOrdenes;

    /**
     * Lista de empleados para determinar los responsables de reparación a los
     * que se enviarán notificaciones por correo electrónico.
     */
    private final List<Empleado> empleados;

    /**
     * Lista de motivos disponibles para seleccionar al poner el sismógrafo
     * fuera de servicio. Podrían recuperarse desde una tabla de
     * configuración.
     */
    private final List<MotivoTipo> motivosDisponibles;

    // Estado interno de la interacción con la interfaz.
    private List<OrdenInspeccion> ordenesDisponibles;
    private OrdenInspeccion ordenSeleccionada;
    private String observacion;
    private List<MotivoFueraServicio> motivosSeleccionados;

    /**
     * Crea un gestor con las dependencias necesarias.
     *
     * @param sesionActiva sesión del usuario responsable de inspecciones
     * @param pantalla interfaz de administración de inspecciones
     * @param todasLasOrdenes conjunto de órdenes precargadas
     * @param empleados lista de empleados para notificar
     * @param motivosDisponibles lista de motivos disponibles
     */
    public GestorAdmInspeccion(Sesion sesionActiva,
                               PantallaAdmInspecciones pantalla,
                               List<OrdenInspeccion> todasLasOrdenes,
                               List<Empleado> empleados,
                               List<MotivoTipo> motivosDisponibles) {
        this.sesionActiva = sesionActiva;
        this.pantalla = pantalla;
        this.todasLasOrdenes = todasLasOrdenes;
        this.empleados = empleados;
        this.motivosDisponibles = motivosDisponibles;
    }

    /**
     * Permite asignar la pantalla después de construir el gestor. Esto
     * facilita la creación de la vista cuando existe una dependencia circular
     * entre el gestor y la pantalla. Si se establece null no se realizarán
     * llamadas a la interfaz.
     */
    public void setPantalla(PantallaAdmInspecciones pantalla) {
        this.pantalla = pantalla;
    }

    /**
     * Obtiene los motivos disponibles para seleccionar.
     * 
     * @return lista de motivos disponibles
     */
    public List<MotivoTipo> obtenerMotivosDisponibles() {
        return motivosDisponibles;
    }

    /**
     * Inicia el cierre de una orden de inspección. Recupera el responsable
     * logueado, filtra sus órdenes completamente realizadas y las ordena por
     * fecha de finalización. Finalmente solicita a la pantalla que las
     * muestre al usuario.
     */
    public void opCerrarOrdenInspeccion() {
        Empleado ri = sesionActiva.obtenerRILogueado();
        this.ordenesDisponibles = buscarOrdenesInspeccion(ri);
        ordenarPorFechaFinalizacionOI();
        pantalla.mostrarOrdenesInspeccion(ordenesDisponibles);
    }

    /**
     * Filtra las órdenes del responsable logueado que estén completamente
     * realizadas y no se encuentren cerradas.
     */
    private List<OrdenInspeccion> buscarOrdenesInspeccion(Empleado ri) {
        return todasLasOrdenes.stream()
                .filter(oi -> oi.esDeRILogueado(ri))
                .filter(OrdenInspeccion::esCompletamenteRealizada)
                .filter(oi -> oi.getEstado() != EstadoOrden.CERRADA)
                .collect(Collectors.toList());
    }

    /**
     * Ordena las órdenes disponibles por la fecha de finalización de forma
     * ascendente (más antigua primero). Se modifica la lista interna.
     */
    private void ordenarPorFechaFinalizacionOI() {
        if (ordenesDisponibles != null) {
            ordenesDisponibles.sort(Comparator.comparing(OrdenInspeccion::getFechaHoraFinalizacion));
        }
    }

    /**
     * Invocado por la pantalla cuando el usuario selecciona una orden. Se
     * actualiza el estado interno y se solicita a la interfaz que pida la
     * observación de cierre.
     *
     * @param orden orden seleccionada
     */
    public void tomarSeleccionOrden(OrdenInspeccion orden) {
        this.ordenSeleccionada = orden;
        pantalla.pedirObservacion();
    }

    /**
     * Almacena la observación ingresada por el usuario y solicita a la
     * pantalla que muestre los motivos disponibles. La observación debe
     * persistirse hasta el final del caso de uso.
     *
     * @param observacion texto ingresado por el usuario
     */
    public void tomarObservacion(String observacion) {
        this.observacion = observacion;
        pantalla.mostrarMotivos(motivosDisponibles);
    }

    /**
     * Recibe la selección de motivos y los comentarios correspondientes.
     * Construye la lista de MotivoFueraServicio para utilizar al cerrar la
     * orden.
     *
     * @param motivos  lista de tipos seleccionados
     * @param comentarios lista de comentarios asociados, debe tener la misma
     *                    longitud que motivos
     */
    public void tomarSeleccionMotivos(List<MotivoTipo> motivos, List<String> comentarios) {
        List<MotivoFueraServicio> seleccion = new ArrayList<>();
        if (motivos != null && comentarios != null) {
            for (int i = 0; i < motivos.size() && i < comentarios.size(); i++) {
                seleccion.add(new MotivoFueraServicio(motivos.get(i), comentarios.get(i)));
            }
        }
        this.motivosSeleccionados = seleccion;
        pantalla.solicitarConfirmacion();
    }

    /**
     * Invocado por la interfaz cuando el usuario confirma el cierre. Se
     * valida que existan los datos mínimos (observación y al menos un
     * motivo) y luego se ejecutan las acciones de cierre.
     */
    public void tomarConfirmacion() {
        if (!validarDatosRequeridosParaCierre()) {
            pantalla.mostrarError("Debe ingresar una observación y al menos un motivo para cerrar la orden.");
            return;
        }
        cerrarOrdenInspeccion();
        finCU();
    }

    /**
     * Verifica que exista la observación de cierre y al menos un motivo
     * asociado. Devuelve false si faltan datos.
     */
    private boolean validarDatosRequeridosParaCierre() {
        return ordenSeleccionada != null
                && observacion != null && !observacion.isBlank()
                && motivosSeleccionados != null && !motivosSeleccionados.isEmpty();
    }

    /**
     * Realiza las operaciones de cierre de la orden y actualización del
     * sismógrafo. Se obtienen la fecha y hora actuales y se actualizan las
     * entidades correspondientemente. Finalmente se notifican los eventos.
     */
    private void cerrarOrdenInspeccion() {
        LocalDateTime ahora = getFechaHoraActual();
        // Cerrar la orden
        ordenSeleccionada.cerrar(ahora, observacion);
        // Enviar sismógrafo a reparación
        ordenSeleccionada.ponerSismografoFueraDeServicio(ahora, motivosSeleccionados, observacion,
                sesionActiva.obtenerRILogueado());
        // Notificaciones
        enviarSismografoAReparacion();
    }

    /**
     * Envia notificaciones tanto por correo como por los monitores de CCRS.
     * Construye los textos incluyendo la identificación del sismógrafo,
     * el nuevo estado, la fecha/hora y los motivos seleccionados.
     */
    private void enviarSismografoAReparacion() {
        // Construir mensaje
        int idSismografo = ordenSeleccionada.getEstacion().obtenerIDSismografo();
        String nombreEstado = new InhabilitadoPorInspeccion().getNombreEstado();
        // Realmente se cambia a FueraDeServicio, pero la especificación
        // solicita incluir el nombre del estado Fuera de Servicio. Para ello
        // recuperamos el nombre del nuevo estado desde la lista de cambios.
        String estadoReal = ordenSeleccionada.getEstacion().getSismografo().getEstadoActual().getNombreEstado();
        LocalDateTime fechaHora = ordenSeleccionada.getFechaHoraCierre();
        StringBuilder motivosStr = new StringBuilder();
        for (MotivoFueraServicio m : motivosSeleccionados) {
            motivosStr.append("- ").append(m.getTipo().getDescripcion()).append(": ").append(m.getComentario()).append("\n");
        }
        String cuerpo = "Sismógrafo ID: " + idSismografo + "\n"
                + "Nuevo estado: " + estadoReal + "\n"
                + "Fecha y hora de registro: " + fechaHora + "\n"
                + "Motivos:\n" + motivosStr;
        // Correos a responsables de reparación
        List<String> destinatarios = empleados.stream()
                .filter(Empleado::sosResponsableReparacion)
                .map(Empleado::getMail)
                .collect(Collectors.toList());
        servicioMail.enviarMail(cuerpo, destinatarios);
        // Publicar en CCRS
        pantallaCCRS.publicar(cuerpo);
    }

    /**
     * Devuelve la fecha y hora actuales. Se extrae a un método separado para
     * facilitar pruebas unitarias en el futuro.
     */
    public LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now();
    }

    /**
     * Finaliza la interacción con un mensaje de confirmación al usuario. En
     * sistemas reales se podría cerrar la ventana o limpiar la interfaz.
     */
    private void finCU() {
        pantalla.mostrarMensaje("La orden se ha cerrado correctamente.");
    }
}