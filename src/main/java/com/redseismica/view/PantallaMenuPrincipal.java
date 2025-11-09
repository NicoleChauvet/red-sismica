package com.redseismica.view;

import com.redseismica.controller.GestorAdmInspeccion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pantalla principal del sistema que presenta las opciones disponibles
 * para el usuario. Actúa como punto de entrada a los diferentes casos
 * de uso de la aplicación.
 */
public class PantallaMenuPrincipal {
    private final GestorAdmInspeccion gestor;
    private JFrame frame;
    
    // Paleta de colores
    private static final Color AZUL_OSCURO = new Color(25, 55, 109);      // #19376D
    private static final Color AZUL_MEDIO = new Color(87, 108, 188);      // #576CBC
    private static final Color CELESTE = new Color(160, 196, 231);        // #A0C4E7
    private static final Color NARANJA = new Color(255, 127, 80);         // #FF7F50
    private static final Color BLANCO = Color.WHITE;

    public PantallaMenuPrincipal(GestorAdmInspeccion gestor) {
        this.gestor = gestor;
        initUI();
    }

    /**
     * Inicializa los componentes de la interfaz gráfica del menú principal.
     */
    private void initUI() {
        frame = new JFrame("Sistema de Red Sísmica");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(AZUL_OSCURO);

        // Panel superior con el encabezado
        JPanel headerPanel = createHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Panel central con el botón
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(AZUL_OSCURO);
        panelCentral.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Icono o texto decorativo
        JLabel iconoLabel = new JLabel("🌊 📊 🔧");
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconoLabel.setForeground(CELESTE);
        panelCentral.add(iconoLabel, gbc);

        // Subtítulo
        gbc.gridy = 1;
        JLabel subtituloLabel = new JLabel("Gestión de Inspecciones");
        subtituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtituloLabel.setForeground(CELESTE);
        panelCentral.add(subtituloLabel, gbc);

        // Botón principal con estilo mejorado
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 20, 20, 20);
        JButton cerrarOrdenButton = createStyledButton("Cerrar Orden de Inspección");
        panelCentral.add(cerrarOrdenButton, gbc);

        // Acción del botón
        cerrarOrdenButton.addActionListener(e -> abrirPantallaAdmInspecciones());

        frame.add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel footerPanel = createFooterPanel();
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Crea el panel de encabezado con el título principal.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(AZUL_MEDIO);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel tituloLabel = new JLabel("Sistema de Red Sísmica");
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        tituloLabel.setForeground(BLANCO);
        
        headerPanel.add(tituloLabel);
        return headerPanel;
    }

    /**
     * Crea el panel de pie de página con información adicional.
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(AZUL_MEDIO);
        footerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        JLabel infoLabel = new JLabel("Monitoreo y Control de Estaciones Sismológicas");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(CELESTE);
        
        footerPanel.add(infoLabel);
        return footerPanel;
    }

    /**
     * Crea un botón con estilo personalizado.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(320, 50));
        button.setBackground(NARANJA);
        button.setForeground(BLANCO);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 140, 105)); // Naranja más claro
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NARANJA);
            }
        });
        
        return button;
    }

    /**
     * Abre la pantalla de administración de inspecciones y cierra el menú principal.
     */
    private void abrirPantallaAdmInspecciones() {
        // Crear la pantalla de administración de inspecciones
        PantallaAdmInspecciones pantalla = new PantallaAdmInspecciones(gestor);
        gestor.setPantalla(pantalla);
        pantalla.cargarDatos();
        
        // Cerrar la ventana del menú principal
        frame.dispose();
    }
}
