package com.redseismica.model;

/**
 * Simulación de la pantalla de notificación del Centro de Control de la Red
 * Sísmica (CCRS). Publica mensajes en un panel centralizado. Aquí se
 * emulan las notificaciones escribiendo en la salida estándar.
 */
public class PantallaCCRS {

    /**
     * Publica un mensaje en los monitores del CCRS. Para este ejemplo se
     * utiliza simplemente un println. En una aplicación real se mostraría
     * información en una interfaz gráfica externa.
     *
     * @param mensaje texto de la notificación
     */
    public void publicar(String mensaje) {
        System.out.println("[Pantalla CCRS] " + mensaje);
    }
}