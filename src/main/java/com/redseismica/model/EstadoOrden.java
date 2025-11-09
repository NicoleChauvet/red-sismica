package com.redseismica.model;

/**
 * Enumeración que representa los distintos estados de una orden de inspección.
 * Se utilizan para verificar si la orden está completamente realizada, si
 * todavía está en ejecución o si ya ha sido cerrada. Los valores se basan
 * en la descripción del caso de uso:
 *
 *  - EN_CURSO: la inspección está en proceso y aún no se registraron
 *    resultados para todas las tareas.
 *  - COMPLETAMENTE_REALIZADA: todas las tareas fueron registradas. Este es
 *    el estado necesario para habilitar el cierre de la orden.
 *  - CERRADA: la orden se cerró y no se puede modificar.
 */
public enum EstadoOrden {
    EN_CURSO,
    COMPLETAMENTE_REALIZADA,
    CERRADA
}