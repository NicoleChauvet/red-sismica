package com.redseismica.model;

import java.util.List;

/**
 * Servicio simulado de envío de correo electrónico. En esta implementación
 * se limita a imprimir por consola el cuerpo del mensaje y los
 * destinatarios para fines de demostración.
 */
public class InterfazMail {

    /**
     * Envía un correo a los destinatarios indicados con el cuerpo
     * especificado. No realiza verificación de conectividad ni formato.
     *
     * @param cuerpo        texto del correo
     * @param destinatarios lista de direcciones de correo electrónico
     */
    public void enviarMail(String cuerpo, List<String> destinatarios) {
        System.out.println("---- Enviando correo electrónico ----");
        System.out.println("Destinatarios: " + String.join(", ", destinatarios));
        System.out.println("Cuerpo:\n" + cuerpo);
        System.out.println("-------------------------------------");
    }
}