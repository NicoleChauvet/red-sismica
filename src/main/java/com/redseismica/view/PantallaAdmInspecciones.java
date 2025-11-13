package com.redseismica.view;

import com.redseismica.controller.GestorAdmInspeccion;
import com.redseismica.model.MotivoTipo;
import com.redseismica.model.MotivoFueraServicio;
import com.redseismica.model.OrdenInspeccion;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Interfaz de usuario para administrar el cierre de órdenes de inspección.
 * Presenta al responsable una lista de órdenes, permite ingresar la
 * observación de cierre y seleccionar los motivos por los cuales el
 * sismógrafo se pondrá fuera de servicio. Finalmente invoca al
 * controlador para realizar la operación.
 */
public class PantallaAdmInspecciones {
    private final GestorAdmInspeccion gestor;
    private JFrame frame;
    private JComboBox<OrdenInspeccion> ordenesComboBox;
    private JTextField observacionField;
    
    private JComboBox<MotivoTipo> motivosComboBox;
    private JTextField comentarioField;
    // Lista dinámica de motivos agregados por el usuario
    private DefaultListModel<MotivoFueraServicio> motivosListModel;
    private JList<MotivoFueraServicio> motivosList;
    private JButton addMotivoButton;
    private JButton removeMotivoButton;
    private JButton registrarObservacionButton;
    private JButton cerrarButton;
    private JLabel mensajeLabel;
    // Datos adicionales por orden: matriz de filas (cada fila = lista de columnas)
    private java.util.List<java.util.List<String>> ordenesDatos;
    
    // Paleta de colores
    private static final Color AZUL_OSCURO = new Color(25, 55, 109);
    private static final Color AZUL_MEDIO = new Color(87, 108, 188);
    private static final Color CELESTE = new Color(160, 196, 231);
    private static final Color CELESTE_CLARO = new Color(224, 238, 249);
    private static final Color NARANJA = new Color(255, 127, 80);
    private static final Color BLANCO = Color.WHITE;
    private static final Color GRIS_CLARO = new Color(245, 245, 245);

    public PantallaAdmInspecciones(GestorAdmInspeccion gestor) {
        this.gestor = gestor;
        initUI();
    }

    /**
     * Inicializa los componentes de la interfaz gráfica y muestra la
     * ventana. Se utiliza un BorderLayout para organizar los controles y un
     * panel central con GridBagLayout para mayor flexibilidad.
     */
    // este es el metodo "habilitarPantalla()" del diagrama de clase/secuencia
    private void initUI() {
        frame = new JFrame("Administración de Inspecciones");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(GRIS_CLARO);

        // Panel de encabezado
        JPanel headerPanel = createHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Panel central con formularios
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(GRIS_CLARO);
        panelCentral.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Sección de órdenes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel ordenesLabel = createSectionLabel("📋 Seleccionar Orden de Inspección");
        panelCentral.add(ordenesLabel, gbc);
        
        gbc.gridy++;
        gbc.weighty = 0;
        ordenesComboBox = new JComboBox<>();
        ordenesComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        ordenesComboBox.setBackground(BLANCO);
        ordenesComboBox.setForeground(AZUL_OSCURO);
        ordenesComboBox.setPreferredSize(new Dimension(0, 50));
        ordenesComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CELESTE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        ordenesComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof OrdenInspeccion) {
                    OrdenInspeccion orden = (OrdenInspeccion) value;
                    String fecha = orden.getFechaHoraFinalizacion() != null ?
                        orden.getFechaHoraFinalizacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
                    String estacion = orden.getEstacion() != null ? orden.getEstacion().getNombre() : "";
                    String sismId = "";
                    if (ordenesDatos != null) {
                        String nro = String.valueOf(orden.getNroOrden());
                        for (java.util.List<String> row : ordenesDatos) {
                            if (row != null && !row.isEmpty() && nro.equals(row.get(0))) {
                                if (row.size() > 3) sismId = row.get(3);
                                break;
                            }
                        }
                    }
                    String extra = sismId.isEmpty() ? "" : (" - Sismógrafo ID:" + sismId);
                    setText(String.format("  Orden #%d - %s - Estación: %s%s",
                        orden.getNroOrden(), fecha, estacion, extra));
                } else if (value == null || value.toString().isEmpty()) {
                    setText("  Seleccione una orden...");
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                
                if (isSelected) {
                    setBackground(CELESTE);
                    setForeground(AZUL_OSCURO);
                } else {
                    setBackground(BLANCO);
                    setForeground(AZUL_OSCURO);
                }
                
                return this;
            }
        });
        panelCentral.add(ordenesComboBox, gbc);

        // Sección de observación
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.ipady = 0;
        JLabel obsLabel = createSectionLabel("📝 Observación de Cierre");
        panelCentral.add(obsLabel, gbc);
        
        gbc.gridy++;
    gbc.gridy++;
    // Campo de observación: mismo control/estilo que comentarioField
        observacionField = new JTextField();
        observacionField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        observacionField.setBackground(BLANCO);
        observacionField.setForeground(AZUL_OSCURO);
        observacionField.setCaretColor(AZUL_OSCURO);
        observacionField.setPreferredSize(new Dimension(0, 60));
        observacionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CELESTE, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        gbc.weighty = 0;
        panelCentral.add(observacionField, gbc);

        // Botón para registrar explícitamente la observación (opcional)
        gbc.gridy++;
        JPanel obsBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        obsBtnPanel.setBackground(GRIS_CLARO);
        registrarObservacionButton = new JButton("Registrar observación");
        registrarObservacionButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registrarObservacionButton.addActionListener(evt -> {
            String obsText = observacionField.getText();
            if (obsText == null || obsText.isBlank()) {
                mostrarError("Ingrese una observación antes de registrarla");
                return;
            }
            if (gestor != null) {
                gestor.tomarObservacion(obsText);
                mostrarMensaje("Observación registrada");
                // Enfocar en selección de motivos
                motivosComboBox.requestFocusInWindow();
            }
        });
        obsBtnPanel.add(registrarObservacionButton);
        panelCentral.add(obsBtnPanel, gbc);

        // Sección de motivos
        gbc.gridy++;
        gbc.weighty = 0;
        JLabel motivosLabel = createSectionLabel("⚠️ Motivos para Fuera de Servicio");
        panelCentral.add(motivosLabel, gbc);
        
        gbc.gridy++;
        motivosComboBox = new JComboBox<>();
        motivosComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        motivosComboBox.setBackground(BLANCO);
        motivosComboBox.setForeground(AZUL_OSCURO);
        motivosComboBox.setPreferredSize(new Dimension(0, 50));
        motivosComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CELESTE, 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        // Renderer para mostrar la descripción del motivo
        motivosComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MotivoTipo) {
                    setText(((MotivoTipo) value).getDescripcion());
                } else if (value == null) {
                    setText("Seleccione un motivo...");
                }
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                if (isSelected) {
                    setBackground(CELESTE);
                    setForeground(AZUL_OSCURO);
                } else {
                    setBackground(BLANCO);
                    setForeground(AZUL_OSCURO);
                }
                return this;
            }
        });
        gbc.weighty = 0;
        panelCentral.add(motivosComboBox, gbc);

    // Sección de comentario
        gbc.gridy++;
        gbc.weighty = 0;
        JLabel comentarioLabel = createSectionLabel("💬 Comentario");
        panelCentral.add(comentarioLabel, gbc);
        
    gbc.gridy++;
    comentarioField = new JTextField();
        comentarioField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comentarioField.setBackground(BLANCO);
        comentarioField.setForeground(AZUL_OSCURO);
        comentarioField.setCaretColor(AZUL_OSCURO);
        comentarioField.setPreferredSize(new Dimension(0, 60));
        comentarioField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CELESTE, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        // Comentario inicialmente deshabilitado hasta que se seleccione un motivo
        comentarioField.setEnabled(false);
        panelCentral.add(comentarioField, gbc);

        // Panel para agregar/remover motivos y mostrar la lista
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel motivosPanel = new JPanel(new BorderLayout(10, 10));
        motivosPanel.setBackground(GRIS_CLARO);

        // Botones para agregar y remover motivos
        JPanel motivosBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        motivosBtnPanel.setBackground(GRIS_CLARO);
    addMotivoButton = new JButton("Agregar motivo");
    removeMotivoButton = new JButton("Quitar motivo");
    // Agregar motivo no disponible hasta seleccionar un motivo en el combo
    addMotivoButton.setEnabled(false);
        addMotivoButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        removeMotivoButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        motivosBtnPanel.add(addMotivoButton);
        motivosBtnPanel.add(removeMotivoButton);

        // Lista que muestra los motivos agregados
        motivosListModel = new DefaultListModel<>();
        motivosList = new JList<>(motivosListModel);
        motivosList.setVisibleRowCount(5);
        motivosList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MotivoFueraServicio) {
                    MotivoFueraServicio m = (MotivoFueraServicio) value;
                    String comentario = m.getComentario() == null || m.getComentario().isBlank() ? "(sin comentario)" : m.getComentario();
                    setText(m.getTipo().getDescripcion() + " — " + comentario);
                }
                setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return this;
            }
        });

        motivosPanel.add(motivosBtnPanel, BorderLayout.NORTH);
        motivosPanel.add(new JScrollPane(motivosList), BorderLayout.CENTER);
        panelCentral.add(motivosPanel, gbc);

        // Acciones de los botones agregar/quitar
        addMotivoButton.addActionListener(evt -> {
            MotivoTipo seleccionado = (MotivoTipo) motivosComboBox.getSelectedItem();
            String comentarioText = comentarioField.getText();
            
            if (seleccionado == null) {
                mostrarError("Seleccione un motivo antes de agregar");
                return;
            }
            
            // Validar que el comentario no esté vacío
            if (comentarioText == null || comentarioText.trim().isEmpty()) {
                mostrarError("Por favor ingrese un comentario para el motivo antes de agregarlo");
                comentarioField.requestFocusInWindow();
                return;
            }
            
            MotivoFueraServicio nuevo = new MotivoFueraServicio(seleccionado, comentarioText);
            motivosListModel.addElement(nuevo);
            comentarioField.setText("");
            // Después de agregar, dejar campo comentario deshabilitado hasta nueva selección
            comentarioField.setEnabled(false);
            addMotivoButton.setEnabled(false);
            mostrarMensaje("Motivo agregado: " + seleccionado.getDescripcion());
        });

        removeMotivoButton.addActionListener(evt -> {
            MotivoFueraServicio sel = motivosList.getSelectedValue();
            if (sel != null) {
                motivosListModel.removeElement(sel);
            } else {
                mostrarError("Seleccione un motivo agregado para quitarlo");
            }
        });

        // Habilitar/deshabilitar campo comentario y botón Agregar según selección
        motivosComboBox.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                Object sel = motivosComboBox.getSelectedItem();
                boolean has = sel != null;
                comentarioField.setEnabled(has);
                addMotivoButton.setEnabled(has);
                if (has) comentarioField.requestFocusInWindow();
            } else if (e.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
                // si quedó sin selección
                if (motivosComboBox.getSelectedItem() == null) {
                    comentarioField.setEnabled(false);
                    addMotivoButton.setEnabled(false);
                }
            }
        });

        // Panel de botones y mensaje
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(GRIS_CLARO);
        
        cerrarButton = createStyledButton("🔒 Cerrar Orden de Inspección");
        cerrarButton.addActionListener(this::cerrarOrdenAction);
        buttonPanel.add(cerrarButton);
        
        JButton cancelarButton = createStyledCancelButton("❌ Cancelar");
        cancelarButton.addActionListener(this::cancelarAction);
        buttonPanel.add(cancelarButton);
        
        panelCentral.add(buttonPanel, gbc);

        // Mensaje de estado
        gbc.gridy++;
        mensajeLabel = new JLabel(" ");
        mensajeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        mensajeLabel.setForeground(AZUL_MEDIO);
        mensajeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelCentral.add(mensajeLabel, gbc);

        frame.add(panelCentral, BorderLayout.CENTER);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    // Asegurar que los campos estén habilitados y solicitar foco al campo de observación
    observacionField.setEditable(true);
    observacionField.setEnabled(true);
    comentarioField.setEditable(true);
    comentarioField.setEnabled(true);
    javax.swing.SwingUtilities.invokeLater(() -> observacionField.requestFocusInWindow());
    }



    /**
     * Crea el panel de encabezado.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AZUL_MEDIO);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel tituloLabel = new JLabel("Administración de Inspecciones");
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloLabel.setForeground(BLANCO);
        
        JLabel subtituloLabel = new JLabel("Gestión y cierre de órdenes de inspección");
        subtituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtituloLabel.setForeground(CELESTE_CLARO);
        
        JPanel textoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textoPanel.setBackground(AZUL_MEDIO);
        textoPanel.add(tituloLabel);
        textoPanel.add(subtituloLabel);
        
        headerPanel.add(textoPanel, BorderLayout.WEST);
        
        return headerPanel;
    }

    /**
     * Crea una etiqueta de sección con estilo.
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(AZUL_OSCURO);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    /**
     * Crea un botón con estilo personalizado.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(300, 45));
        button.setBackground(NARANJA);
        button.setForeground(BLANCO);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 140, 105));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NARANJA);
            }
        });
        
        return button;
    }

    /**
     * Carga los datos iniciales. Debe llamarse después de que el gestor
     * tenga la referencia a esta pantalla.
     */
    // este es el metodo "tomarOpCerrarOrdenInspeccion()" del diagrama de clase/secuencia
    public void cargarDatos() {
        gestor.opCerrarOrdenInspeccion();
        // Cargar también los motivos disponibles desde el inicio
        mostrarMotivos(gestor.buscarMotivoFueraLinea());
    }

    /**
     * Acción asociada al botón "Cerrar Orden". Recoge los datos de la
     * interfaz, los pasa al gestor y espera a que éste concluya el caso de
     * uso. Si falta información se mostrará un mensaje de error a través
     * del gestor.
     */
    private void cerrarOrdenAction(ActionEvent e) {
        System.out.println("[PantallaAdmInspecciones] cerrarOrdenAction iniciado");
        
        OrdenInspeccion seleccion = (OrdenInspeccion) ordenesComboBox.getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una orden de inspección");
            return;
        }
        System.out.println("[PantallaAdmInspecciones] Orden seleccionada: " + seleccion.getNroOrden());
        
        // Capturar la observación
        String obs = observacionField.getText();
        if (obs == null || obs.isBlank()) {
            mostrarError("Ingrese una observación de cierre");
            return;
        }
        System.out.println("[PantallaAdmInspecciones] Observación: " + obs);
        
        // Recolectar todos los motivos agregados en la lista dinámica
        java.util.List<MotivoTipo> motivosTipos = new java.util.ArrayList<>();
        java.util.List<String> comentarios = new java.util.ArrayList<>();

        for (int i = 0; i < motivosListModel.getSize(); i++) {
            MotivoFueraServicio mfs = motivosListModel.getElementAt(i);
            if (mfs != null) {
                motivosTipos.add(mfs.getTipo());
                comentarios.add(mfs.getComentario() == null ? "" : mfs.getComentario());
                System.out.println("[PantallaAdmInspecciones] Motivo agregado: " + mfs.getTipo().getDescripcion());
            }
        }

        // Si el usuario no pulsó "Agregar motivo" pero dejó un motivo
        // seleccionado con comentario, tratarlo como implícito y añadirlo
        // a las listas siempre que no esté ya presente.
        MotivoTipo motivoActual = (MotivoTipo) motivosComboBox.getSelectedItem();
        String comentarioActual = comentarioField.getText() == null ? "" : comentarioField.getText();
        if (motivoActual != null && !comentarioActual.isBlank()) {
            boolean existe = false;
            for (int i = 0; i < motivosTipos.size(); i++) {
                MotivoTipo t = motivosTipos.get(i);
                String c = comentarios.get(i);
                if (t != null && t.getDescripcion().equals(motivoActual.getDescripcion()) && c.equals(comentarioActual)) {
                    existe = true;
                    break;
                }
            }
            if (!existe && motivosTipos.size() < 5) { // Limitar a 5 motivos máximo
                motivosTipos.add(motivoActual);
                comentarios.add(comentarioActual);
                System.out.println("[PantallaAdmInspecciones] Motivo implícito agregado: " + motivoActual.getDescripcion());
            }
        }

        if (motivosTipos.isEmpty()) {
            mostrarError("Debe agregar al menos un motivo para fuera de servicio");
            return;
        }
        
        System.out.println("[PantallaAdmInspecciones] Total motivos a enviar: " + motivosTipos.size());

        // Invocar al gestor con los datos recopilados
        // aca se encuentran los metodos de tomar y pedir del diagrama de clase/secuencia (orden, observacion, motivos y comentarios)
        gestor.tomarSeleccionOrden(seleccion);
        gestor.tomarObservacion(obs);
        gestor.tomarSeleccionMotivos(motivosTipos);
        gestor.tomarSeleccionComentarios(comentarios);
        
        // este es el metodo "tomarConfirmacion()" del diagrama de clase/secuencia
        try {
            System.out.println("[PantallaAdmInspecciones] Llamando a tomarConfirmacion()");
            gestor.tomarConfirmacion();
            System.out.println("[PantallaAdmInspecciones] tomarConfirmacion() completado");
        } catch (java.sql.SQLException ex) {
            mostrarError("Error al confirmar el cierre: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Presenta las órdenes de inspección en el combo box. Se
     * invoca automáticamente por el gestor después de calcular las
     * órdenes disponibles.
     */
    public void mostrarOrdenesInspeccion(java.util.List<OrdenInspeccion> ordenes, java.util.List<java.util.List<String>> datos) {
        this.ordenesDatos = datos;
        ordenesComboBox.removeAllItems();
        if (ordenes != null && !ordenes.isEmpty()) {
            for (OrdenInspeccion orden : ordenes) {
                ordenesComboBox.addItem(orden);
            }
        }
    }

    /**
     * Se llama cuando el gestor necesita que el usuario ingrese la
     * observación de cierre. Aquí simplemente se enfoca el campo de texto.
     */
    public void pedirObservacion() {
        observacionField.requestFocusInWindow();
    }

    /**
     * Muestra la lista de motivos disponibles en la interfaz. Esta
     * actualización se realiza cada vez que el usuario ingresa la
     * observación.
     */
    public void mostrarMotivos(java.util.List<MotivoTipo> motivos) {
        motivosComboBox.removeAllItems();
        if (motivos != null) {
            for (MotivoTipo motivo : motivos) {
                motivosComboBox.addItem(motivo);
            }
        }
        motivosComboBox.revalidate();
        motivosComboBox.repaint();
    }

    /**
     * Solicita la confirmación del usuario para cerrar la orden. Muestra un
     * diálogo modal y, si el usuario cancela, aborta la operación. La
     * confirmación positiva continúa con la llamada al gestor.
     */
    // este es el metodo "solicitarConfCierreOrdenInspeccion()" del diagrama de clase/secuencia
    public void solicitarConfirmacion() {
        int option = JOptionPane.showConfirmDialog(frame,
                "¿Está seguro de que desea cerrar la orden de inspección?",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            mostrarMensaje("Operación cancelada por el usuario.");
        }
    }

    /**
     * Crea un botón con estilo personalizado para cancelar.
     */
    private JButton createStyledCancelButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(150, 45));
        button.setBackground(new Color(200, 200, 200)); // Gris para cancelar
        button.setForeground(AZUL_OSCURO);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 200, 200));
            }
        });
        
        return button;
    }

    /**
     * Acción del botón cancelar. Cierra la ventana y vuelve al menú principal.
     */
    private void cancelarAction(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(frame,
                "¿Está seguro de que desea cancelar? Se perderán los cambios sin guardar.",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            mostrarMensaje("Operación cancelada.");
            volverAlMenuPrincipal();
        }
    }

    /**
     * Vuelve a la pantalla del menú principal cerrando esta ventana.
     */
    public void volverAlMenuPrincipal() {
        // Limpiar los campos
        observacionField.setText("");
        comentarioField.setText("");
        motivosListModel.clear();
        motivosComboBox.removeAllItems();
        // Cerrar la pantalla y volver al menú principal
        frame.dispose();
        SwingUtilities.invokeLater(() -> {
            gestor.setPantalla(null);
            new PantallaMenuPrincipal(gestor);
        });
    }

    /**
     * Muestra un mensaje de error en la interfaz utilizando un diálogo.
     *
     * @param mensaje texto del error
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(frame, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje informativo al usuario, tanto en un diálogo como
     * en una etiqueta en la parte inferior del formulario.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(frame, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
        mensajeLabel.setText(mensaje);
    }
}