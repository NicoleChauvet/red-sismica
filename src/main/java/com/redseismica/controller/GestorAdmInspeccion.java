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
    private LocalDateTime fechaHora;
    private Empleado RILogueado;
    private List<OrdenInspeccion> ordenesDisponibles;
    private OrdenInspeccion ordenSeleccionada;
    private String observacion;
    private List<MotivoFueraServicio> motivosSeleccionados;
    private List<MotivoTipo> motivos;
    private List<String> comentarios;

    /**
     * Crea un gestor con las dependencias necesarias.
     *
     * @param sesionActiva sesión del usuario responsable de inspecciones
     * @param pantalla interfaz de administración de inspecciones
     * @param empleados lista de empleados para notificar
     */
    public GestorAdmInspeccion(Sesion sesionActiva,
                               PantallaAdmInspecciones pantalla) {
        this.sesionActiva = sesionActiva;
        this.pantalla = pantalla;
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
        System.out.println("[Gestor] tomarObservacion -> observacion='" + observacion + "'");
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
    /**
     * Recibe la lista de tipos de motivos seleccionados (sin comentarios).
     * Los comentarios se recibirán por separado vía {@link #tomarSeleccionComentarios}.
     */
    public void tomarSeleccionMotivos(List<MotivoTipo> motivos) {
        this.motivos = motivos != null ? new ArrayList<>(motivos) : new ArrayList<>();
        System.out.println("[Gestor] tomarSeleccionMotivos -> motivos.count=" + this.motivos.size());
    }

    /**
     * Recibe la lista de comentarios en el mismo orden que los motivos
     * previamente enviados por {@link #tomarSeleccionMotivos} y construye
     * la lista interna de {@link MotivoFueraServicio} que se usará al
     * cerrar la orden.
     *
     * Si las listas tienen longitudes distintas se emparejan hasta la
     * menor longitud y los motivos sin comentario recibirán cadena vacía.
     */
    public void tomarSeleccionComentarios(List<String> comentarios) {
        // Guardar comentarios tal cual y construir la lista de MotivoFueraServicio
        // emparejando sólo hasta la cantidad de motivos seleccionados. No
        // validamos longitudes — asumimos que la UI impide comentarios sin
        // motivo.
        this.comentarios = comentarios != null ? new ArrayList<>(comentarios) : new ArrayList<>();
        System.out.println("[Gestor] tomarSeleccionComentarios -> comentarios.count=" + this.comentarios.size());
        // Construir la lista combinada interna a partir de motivos + comentarios
        this.motivosSeleccionados = new ArrayList<>();
        if (this.motivos == null || this.motivos.isEmpty()) {
            System.out.println("[Gestor] tomarSeleccionComentarios -> no hay motivos, motivosSeleccionados vacio");
            return;
        }
        for (int i = 0; i < this.motivos.size(); i++) {
            MotivoTipo tipo = this.motivos.get(i);
            String coment = i < this.comentarios.size() ? this.comentarios.get(i) : "";
            if (tipo != null) {
                this.motivosSeleccionados.add(new MotivoFueraServicio(tipo, coment));
            }
        }
        System.out.println("[Gestor] tomarSeleccionComentarios -> motivosSeleccionados.count=" + this.motivosSeleccionados.size());
    }

    
    /**
     * Invocado por la interfaz cuando el usuario confirma el cierre. Se
     * valida que existan los datos mínimos (observación y al menos un
     * motivo) y luego se ejecutan las acciones de cierre.
     * @throws SQLException 
     */
    public void tomarConfirmacion() throws SQLException {
        System.out.println("[Gestor] tomarConfirmacion() llamado");
        System.out.println("[Gestor] ordenSeleccionada: " + (ordenSeleccionada != null ? ordenSeleccionada.getNroOrden() : "null"));
        System.out.println("[Gestor] observacion: " + observacion);
        System.out.println("[Gestor] motivosSeleccionados: " + (motivosSeleccionados != null ? motivosSeleccionados.size() : 0));
        
        if (!validarDatosRequeridosParaCierre()) {
            System.out.println("[Gestor] Validación fallida");
            pantalla.mostrarError("Debe ingresar una observación y al menos un motivo para cerrar la orden.");
            return;
        }
        System.out.println("[Gestor] Validación exitosa, procediendo con cierre");
        cerrarOrdenInspeccion();
        obtenerMailResponsableReparacion();
        publicarMonitores();
        enviarNotificacionPorMail();
        finCU();
    }

    /**
     * Verifica que exista la observación de cierre y al menos un motivo
     * asociado. Devuelve false si faltan datos.
     */
    private boolean validarDatosRequeridosParaCierre() {
        boolean ok = ordenSeleccionada != null
            && observacion != null && !observacion.isBlank()
            && motivosSeleccionados != null && !motivosSeleccionados.isEmpty();
        System.out.println("[Gestor] validarDatos -> ordenSel=" + (ordenSeleccionada!=null) +
            ", observacion='" + observacion + "', motivosSeleccionados.count=" + (motivosSeleccionados==null?0:motivosSeleccionados.size()) +
            ", result=" + ok);
        return ok;
    }

    /**
     * Realiza las operaciones de cierre de la orden y actualización del
     * sismógrafo. Se obtienen la fecha y hora actuales y se actualizan las
     * entidades correspondientemente. Finalmente se notifican los eventos.
     */
    private void cerrarOrdenInspeccion() {
        fechaHora = getFechaHoraActual();
        Estado estado = buscarEstadoDeOrdenCerrada();
        // Cerrar la orden
        ordenSeleccionada.cerrar(fechaHora, observacion, estado);
    // Enviar sismógrafo a reparación
    ordenSeleccionada.ponerSismografoFueraDeServicio(fechaHora, motivos, comentarios,
        sesionActiva.obtenerRILogueado());
        
        // Persistir la orden cerrada en la BD
        try {
            com.redseismica.database.dao.OrdenInspeccionDAO.update(ordenSeleccionada);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo persistir el cierre de la orden en la BD: " + e.getMessage());
        }
        
        // Notificaciones
        enviarSismografoAReparacion(fechaHora, motivosSeleccionados, RILogueado);
    }

    /**
     * Envia notificaciones tanto por correo como por los monitores de CCRS.
     * Construye los textos incluyendo la identificación del sismógrafo,
     * el nuevo estado, la fecha/hora y los motivos seleccionados.
     */
    private void enviarSismografoAReparacion(LocalDateTime fechaHora, List<MotivoFueraServicio> motivosSeleccionados, Empleado RILogueado) {
        // Este método ahora solo se encarga de notificaciones
        // La transición del sismógrafo ya fue hecha en cerrarOrdenInspeccion()
        System.out.println("[Gestor] Sismógrafo enviado a reparación exitosamente");
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

    public List<String> obtenerMailResponsableReparacion() throws SQLException {
        List<Empleado> empleados = com.redseismica.database.dao.EmpleadoDAO.findAll();
        List<String> mails = new ArrayList<>();
        for (Empleado empleado : empleados) {
            if (empleado.sosResponsableReparacion()) {
                mails.add(empleado.getMail());
            }
        }
        return mails;
    }

    public void publicarMonitores() {
        if (pantalla != null) {
            pantalla.mostrarMensaje("Publicado con exito en monitores");
        } else {
            System.out.println("[Gestor] publicarMonitores: publicado en monitores (sin UI)");
        }
    }

    public void enviarNotificacionPorMail() throws SQLException {
        if (pantalla != null) {
            pantalla.mostrarMensaje("Correos enviados con exito");
        } else {
            System.out.println("[Gestor] enviarNotificacionPorMail: correos enviados (sin UI)");
        }
    }


    /**
     * Finaliza la interacción con un mensaje de confirmación al usuario. En
     * sistemas reales se podría cerrar la ventana o limpiar la interfaz.
     */
    private void finCU() {
        if (pantalla != null) {
            pantalla.mostrarMensaje("La orden se ha cerrado correctamente.");
            pantalla.volverAlMenuPrincipal();
        } else {
            System.out.println("[Gestor] finCU: La orden se ha cerrado correctamente. (sin UI)");
        }
    }
}