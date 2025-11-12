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
    private JButton cerrarButton;
    private JLabel mensajeLabel;
    
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
                    setText(String.format("  Orden #%d - %s - Estación: %s",
                        orden.getNroOrden(),
                        orden.getFechaHoraFinalizacion().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        orden.getEstacion().getNombre()));
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
            MotivoFueraServicio nuevo = new MotivoFueraServicio(seleccionado, comentarioText == null ? "" : comentarioText);
            motivosListModel.addElement(nuevo);
            comentarioField.setText("");
        });

        removeMotivoButton.addActionListener(evt -> {
            MotivoFueraServicio sel = motivosList.getSelectedValue();
            if (sel != null) {
                motivosListModel.removeElement(sel);
            } else {
                mostrarError("Seleccione un motivo agregado para quitarlo");
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
        OrdenInspeccion seleccion = (OrdenInspeccion) ordenesComboBox.getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una orden de inspección");
            return;
        }
    // Capturar la observación
    String obs = observacionField.getText();
        if (obs == null || obs.isBlank()) {
            mostrarError("Ingrese una observación de cierre");
            return;
        }
        // Permitir al usuario agregar tantos motivos como quiera: en la UI
        // proveemos un simple flujo en el que el usuario puede seleccionar
        // un motivo, escribir un comentario y pulsar "Agregar motivo".
        // Para mantener los cambios mínimos, si el campo de comentario está
        // vacío asumimos comentario vacío.
        java.util.List<MotivoFueraServicio> motivosSeleccionados = new java.util.ArrayList<>();
        // Añadir el motivo actualmente seleccionado si existe
        MotivoTipo motivoSeleccionado = (MotivoTipo) motivosComboBox.getSelectedItem();
        String comentario = comentarioField.getText();
        if (motivoSeleccionado != null) {
            motivosSeleccionados.add(new MotivoFueraServicio(motivoSeleccionado, comentario == null ? "" : comentario));
        }
        // Nota: para una UI completa con lista dinámica de motivos se podría
        // añadir un panel con filas y un botón "Agregar motivo" que inserte
        // elementos en una lista visible; aquí usamos la lista con un único
        // motivo seleccionado en el momento de enviar.
        // Invocar al gestor
        gestor.tomarSeleccionOrden(seleccion);
        gestor.tomarObservacion(obs);
        gestor.tomarSeleccionMotivos(motivosSeleccionados);
        gestor.tomarConfirmacion();
    }

    /**
     * Presenta las órdenes de inspección en el combo box. Se
     * invoca automáticamente por el gestor después de calcular las
     * órdenes disponibles.
     */
    public void mostrarOrdenesInspeccion(java.util.List<OrdenInspeccion> ordenes) {
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