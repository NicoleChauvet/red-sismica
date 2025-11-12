package com.redseismica.app;

import com.redseismica.controller.GestorAdmInspeccion;
import com.redseismica.database.DatabaseConfig;
import com.redseismica.database.DataLoader;
import com.redseismica.database.dao.EmpleadoDAO;
// orders and motivos are now loaded on demand by the controllers/DAOs
import com.redseismica.model.*;
import com.redseismica.view.PantallaMenuPrincipal;

import javax.swing.*;
import java.util.List;

/**
 * Clase principal de la aplicación. Inicializa la base de datos,
 * carga todos los datos desde la BD (órdenes, empleados, motivos),
 * inicializa el gestor de administración de inspecciones y despliega
 * la interfaz gráfica.
 */
public class App {

    public static void main(String[] args) {
        try {
            // 1. Crear tablas en la BD (si no existen)
            DatabaseConfig.createTables();

            // 2. Cargar datos iniciales en la BD (si las tablas están vacías)
            DataLoader.loadInitialData();

            // 3. Cargar sólo los empleados (órdenes y motivos se obtendrán bajo demanda)
            List<Empleado> empleados = EmpleadoDAO.findAll();
            if (empleados == null || empleados.isEmpty()) {
                System.err.println("Advertencia: No se cargaron empleados");
                empleados = List.of();
            }

            // 4. Obtener el primer empleado responsable de inspección para la sesión
            // (En un sistema real esto vendría de autenticación)
            Empleado responsableInspeccion = empleados.stream()
                    .filter(e -> e.getRol() != null && "ResponsableInspeccion".equalsIgnoreCase(e.getRol().getNombre()))
                    .findFirst()
                    .orElse(empleados.isEmpty() ? null : empleados.get(0));

            if (responsableInspeccion == null) {
                System.err.println("Error: No hay empleados con rol ResponsableInspeccion");
                System.exit(1);
            }

            // 5. Crear usuario y sesión con el empleado responsable
            Usuario usuarioRI = new Usuario("app_user", "password", responsableInspeccion);
            Sesion sesion = new Sesion(usuarioRI);

            // 6. Crear el gestor; Gestor carga órdenes y motivos bajo demanda desde la BD
            GestorAdmInspeccion gestor = new GestorAdmInspeccion(sesion, null, empleados);

            // 7. Mostrar la interfaz gráfica
            SwingUtilities.invokeLater(() -> {
                new PantallaMenuPrincipal(gestor);
            });

            System.out.println("✓ Aplicación inicializada exitosamente");
            System.out.println("  - Empleados cargados: " + empleados.size());
            System.out.println("  - Órdenes y motivos se cargarán bajo demanda desde la base de datos.");

        } catch (Exception ex) {
            System.err.println("Error fatal al inicializar la aplicación: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }
}