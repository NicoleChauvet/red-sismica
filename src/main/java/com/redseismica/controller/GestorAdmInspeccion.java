package com.redseismica.controller;

import com.redseismica.model.*;
import com.redseismica.view.PantallaAdmInspecciones;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

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
    private LocalDateTime ahora;
    private Empleado RILogueado;

    /**
     * (Removed) Previously this field stored an in-memory copy of all orders.
     * We now always read from the database and therefore this field was removed.
     */

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
    // motivos now loaded from DB on demand via MotivoTipoDAO

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
     * @param empleados lista de empleados para notificar
     */
    public GestorAdmInspeccion(Sesion sesionActiva,
                               PantallaAdmInspecciones pantalla,
                               List<Empleado> empleados) {
        this.sesionActiva = sesionActiva;
        this.pantalla = pantalla;
        this.empleados = empleados;
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
    public List<MotivoTipo> buscarMotivoFueraLinea() {
        try {
            return com.redseismica.database.dao.MotivoTipoDAO.findAll();
        } catch (Exception e) {
            System.err.println("Error al leer motivos desde la BD: " + e.getMessage());
            if (pantalla != null) {
                pantalla.mostrarError("No se pudieron cargar los motivos desde la base de datos. Intente nuevamente más tarde.");
            }
            return List.of();
        }
    }

    /**
     * Inicia el cierre de una orden de inspección. Recupera el responsable
     * logueado, filtra sus órdenes completamente realizadas y las ordena por
     * fecha de finalización. Finalmente solicita a la pantalla que las
     * muestre al usuario.
     */
    public void opCerrarOrdenInspeccion() {
        RILogueado = sesionActiva.obtenerRILogueado();
        this.ordenesDisponibles = buscarOrdenesInspeccion(RILogueado);
        ordenarPorFechaFinalizacionOI();
        pantalla.mostrarOrdenesInspeccion(ordenesDisponibles);
    }

    /**
     * Filtra las órdenes del responsable logueado que estén completamente
     * realizadas y no se encuentren cerradas.
     */
    private List<OrdenInspeccion> buscarOrdenesInspeccion(Empleado RILogueado) {
        // Cargar las órdenes directamente desde la base de datos y filtrar.
        try {
            List<OrdenInspeccion> ordenesBD = com.redseismica.database.dao.OrdenInspeccionDAO.findAll();
            return ordenesBD.stream()
                    .filter(oi -> oi.esDeRILogueado(RILogueado))
                    .filter(OrdenInspeccion::esCompletamenteRealizada)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // No caer en datos en memoria: informar a la UI y devolver lista vacía.
            System.err.println("Error al leer órdenes desde la BD: " + e.getMessage());
            if (pantalla != null) {
                pantalla.mostrarError("No se pudo conectar a la base de datos. Intente nuevamente más tarde.");
            }
            return List.of();
        }
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
        // Mostrar motivos cargados desde la base de datos
        List<MotivoTipo> motivosDisponibles = buscarMotivoFueraLinea();
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
    public void tomarSeleccionMotivos(List<MotivoFueraServicio> motivos) {
        // Recibe la lista ya construida en la UI (cada elemento incluye su comentario)
        this.motivosSeleccionados = motivos != null ? new ArrayList<>(motivos) : new ArrayList<>();
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
        ahora = getFechaHoraActual();
        Estado estado = buscarEstadoDeOrdenCerrada();
        // Cerrar la orden
        ordenSeleccionada.cerrar(ahora, observacion, estado);
        // Enviar sismógrafo a reparación
        ordenSeleccionada.ponerSismografoFueraDeServicio(ahora, motivosSeleccionados, observacion,
                sesionActiva.obtenerRILogueado());
        
        // Persistir la orden cerrada en la BD
        try {
            com.redseismica.database.dao.OrdenInspeccionDAO.update(ordenSeleccionada);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo persistir el cierre de la orden en la BD: " + e.getMessage());
        }
        
        // Notificaciones
        enviarSismografoAReparacion(ahora, motivosSeleccionados, RILogueado);
    }

    /**
     * Envia notificaciones tanto por correo como por los monitores de CCRS.
     * Construye los textos incluyendo la identificación del sismógrafo,
     * el nuevo estado, la fecha/hora y los motivos seleccionados.
     */
    private void enviarSismografoAReparacion(LocalDateTime ahora, List<MotivoFueraServicio> motivosSeleccionados, Empleado RILogueado) {
        ordenSeleccionada.ponerSismografoFueraDeServicio(ahora, motivosSeleccionados, observacion, RILogueado);
    }

    /**
     * Devuelve la fecha y hora actuales. Se extrae a un método separado para
     * facilitar pruebas unitarias en el futuro.
     */
    public LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now();
    }

    public Estado buscarEstadoDeOrdenCerrada() {
        try {
            List<Estado> estados = com.redseismica.database.dao.EstadoDAO.findAll();
            Estado estadoCerrada = null;
            for (Estado estado : estados) {
                if (estado.sosCerrada()) {
                    estadoCerrada = estado;
                }
            }
            return estadoCerrada;

        } catch (SQLException e) {
            System.err.println("Error al leer estados desde la BD: " + e.getMessage());
            if (pantalla != null) {
                pantalla.mostrarError("No se pudieron cargar los estados desde la base de datos. Intente nuevamente más tarde.");
            }
            return null;
        }
    }


    /**
     * Finaliza la interacción con un mensaje de confirmación al usuario. En
     * sistemas reales se podría cerrar la ventana o limpiar la interfaz.
     */
    private void finCU() {
        pantalla.mostrarMensaje("La orden se ha cerrado correctamente.");
    }
}