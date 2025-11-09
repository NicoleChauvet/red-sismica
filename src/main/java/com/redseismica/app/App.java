package com.redseismica.app;

import com.redseismica.controller.GestorAdmInspeccion;
import com.redseismica.model.*;
import com.redseismica.states.InhabilitadoPorInspeccion;
import com.redseismica.view.PantallaAdmInspecciones;
import com.redseismica.view.PantallaMenuPrincipal;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase principal de la aplicación. Construye un conjunto de datos de
 * demostración, inicializa el gestor de administración de inspecciones y
 * despliega la interfaz gráfica. Esta aplicación es auto‑contenida y no
 * requiere un servidor ni base de datos para funcionar.
 */
public class App {

    public static void main(String[] args) {
        // Crear roles
        Rol rolRI = new Rol("ResponsableInspeccion");
        Rol rolRR = new Rol("ResponsableReparacion");

        // Crear empleados
        Empleado responsableInspeccion = new Empleado("Juan", "Pérez",
                "juan.perez@empresa.com", "3511234567", rolRI);
        Empleado responsableReparacion1 = new Empleado("Ana", "García",
                "ana.garcia@empresa.com", "3512345678", rolRR);
        Empleado responsableReparacion2 = new Empleado("Luis", "Rodríguez",
                "luis.rodriguez@empresa.com", "3513456789", rolRR);

        // Crear usuario y sesión para el responsable de inspecciones
        Usuario usuarioRI = new Usuario("jperez", "password", responsableInspeccion);
        Sesion sesion = new Sesion(usuarioRI);

        // Crear sismógrafo y estación asociados
        LocalDateTime haceUnAnio = LocalDateTime.now().minusYears(1);
        Sismografo sismografo = new Sismografo(1, haceUnAnio, 1001);
        // Inhabilitarlo por inspección para el caso de uso
        LocalDateTime haceDosDias = LocalDateTime.now().minusDays(2);
        sismografo.setEstado(new InhabilitadoPorInspeccion(), haceDosDias, null,
                "Inhabilitado por inspección", responsableInspeccion);
        EstacionSismologica estacion = new EstacionSismologica(101, "Estación Central",
                -31.4167, -64.1833, sismografo);

        // Crear órdenes de inspección
        List<OrdenInspeccion> ordenes = new ArrayList<>();
        // Orden completada, puede cerrarse
        ordenes.add(new OrdenInspeccion(1,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                EstadoOrden.COMPLETAMENTE_REALIZADA,
                estacion,
                responsableInspeccion));
        // Orden en curso, no debe aparecer en la interfaz
        ordenes.add(new OrdenInspeccion(2,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                EstadoOrden.EN_CURSO,
                estacion,
                responsableInspeccion));

        // Crear lista de empleados para notificación
        List<Empleado> empleados = Arrays.asList(responsableInspeccion, responsableReparacion1, responsableReparacion2);

        // Crear motivos disponibles
        List<MotivoTipo> motivos = Arrays.asList(
                new MotivoTipo("Sensor dañado"),
                new MotivoTipo("Cable cortado"),
                new MotivoTipo("Pérdida de calibración"),
                new MotivoTipo("Batería agotada"));

        // Inicializar gestor sin pantalla (se asignará después)
        GestorAdmInspeccion gestor = new GestorAdmInspeccion(sesion, null, ordenes, empleados, motivos);

        // Ejecutar interfaz en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Mostrar el menú principal primero
            new PantallaMenuPrincipal(gestor);
        });
    }
}