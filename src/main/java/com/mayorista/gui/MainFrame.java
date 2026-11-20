
package com.mayorista.gui;

import com.mayorista.dao.ArticuloDAO;
import com.mayorista.model.Articulo;
import com.mayorista.model.Proveedor;
import com.mayorista.model.Rubro;
import com.mayorista.observer.StockObserver;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame implements StockObserver {
    private ArticuloDAO articuloDAO;
    private JTable tablaArticulos;
    private DefaultTableModel modelTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextArea areaAlertas;
    private JTextField txtBuscar;

    // Métricas del Dashboard
    private JLabel lblTotalProductos;
    private JLabel lblStockCriticoCount;
    private JLabel lblValorTotalInventario;

    private final DecimalFormat formatoMonedaARS;

    // Paleta de Colores ERP Profesional
    private final Color AZUL_HEADER = Color.decode("#0A2540");
    private final Color AZUL_ACCENT = Color.decode("#635BFF");
    private final Color AZUL_BOTON = Color.decode("#0052CC");
    private final Color CELESTE_BORDE = Color.decode("#E2E8F0");
    private final Color BLANCO = Color.WHITE;
    private final Color FONDO_GENERAL = Color.decode("#F8FAFC");
    private final Color TEXTO_DARK = Color.decode("#1E293B");

    private final Color VERDE_BG = Color.decode("#DCFCE7");
    private final Color VERDE_TXT = Color.decode("#15803D");
    private final Color ROJO_BG = Color.decode("#FEE2E2");
    private final Color ROJO_TXT = Color.decode("#991B1B");

    public MainFrame() {
        DecimalFormatSymbols simbolosARS = new DecimalFormatSymbols(Locale.forLanguageTag("es-AR"));
        simbolosARS.setGroupingSeparator('.');
        simbolosARS.setDecimalSeparator(',');
        formatoMonedaARS = new DecimalFormat("$ #,##0.00", simbolosARS);

        articuloDAO = new ArticuloDAO();
        initUI();
        cargarArticulos();
    }

    private void initUI() {
        setTitle("MAYORISTA COMPUTACIÓN | Sistema de Gestión de Stock (UNSAdA 2026)");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(FONDO_GENERAL);
        setLayout(new BorderLayout());

        // Header Superior Institucional
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(AZUL_HEADER);
        panelHeader.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblBrand = new JLabel("<html><span style='color: #FFFFFF; font-size: 16px; font-weight: bold;'>TPI GESTIÓN DE STOCK</span> <span style='color: #94A3B8; font-size: 13px;'>| UNSAdA POO 2026</span></html>");
        JLabel lblUser = new JLabel("<html><span style='color: #E2E8F0; font-size: 12px;'>Equipo: <b>Bautista, Lázaro y Sol</b>");

        panelHeader.add(lblBrand, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Contenedor de Pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BLANCO);
        tabbedPane.setForeground(TEXTO_DARK);

        tabbedPane.addTab("  [ INVENTARIO & STOCK ]  ", createStockPanel());
        tabbedPane.addTab("  [ REGISTRAR VENTA ]  ", createVentasPanel());
        tabbedPane.addTab("  [ ALERTAS OBSERVER ]  ", createAlertasPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStockPanel() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBackground(FONDO_GENERAL);
        panelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Tarjetas KPI / Dashboard Superior
        JPanel panelKPIs = new JPanel(new GridLayout(1, 3, 15, 0));
        panelKPIs.setBackground(FONDO_GENERAL);

        lblTotalProductos = new JLabel("0", SwingConstants.CENTER);
        lblStockCriticoCount = new JLabel("0", SwingConstants.CENTER);
        lblValorTotalInventario = new JLabel("$ 0,00", SwingConstants.CENTER);

        panelKPIs.add(crearCardKPI("TOTAL PRODUCTOS EN CATÁLOGO", lblTotalProductos, AZUL_ACCENT));
        panelKPIs.add(crearCardKPI("PRODUCTOS EN STOCK CRÍTICO", lblStockCriticoCount, Color.decode("#DC2626")));
        panelKPIs.add(crearCardKPI("VALOR TOTAL INVENTARIO ($ ARS)", lblValorTotalInventario, Color.decode("#16A34A")));

        // 2. Barra de Búsqueda Rápida
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelBusqueda.setBackground(BLANCO);
        panelBusqueda.setBorder(new CompoundBorder(
                new LineBorder(CELESTE_BORDE, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblBuscar = new JLabel("Buscar Producto:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(TEXTO_DARK);

        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Escriba para filtrar por código o descripción...");

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
            private void filtrar() {
                String text = txtBuscar.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setBackground(FONDO_GENERAL);
        panelCentro.add(panelKPIs, BorderLayout.NORTH);
        panelCentro.add(panelBusqueda, BorderLayout.SOUTH);

        panelPrincipal.add(panelCentro, BorderLayout.NORTH);

        // 3. Tabla de Artículos
        String[] columnas = {"Código", "Descripción", "Precio Unitario ($ ARS)", "Stock Actual", "Stock Mínimo", "Estado"};
        modelTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        sorter = new TableRowSorter<>(modelTabla);
        tablaArticulos = new JTable(modelTabla);
        tablaArticulos.setRowSorter(sorter);
        tablaArticulos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaArticulos.setRowHeight(32);
        tablaArticulos.setShowGrid(true);
        tablaArticulos.setGridColor(CELESTE_BORDE);

        JTableHeader header = tablaArticulos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(AZUL_HEADER);
        header.setForeground(BLANCO);
        header.setPreferredSize(new Dimension(100, 36));

        StockBadgeRenderer customRenderer = new StockBadgeRenderer();
        for (int i = 0; i < tablaArticulos.getColumnCount(); i++) {
            tablaArticulos.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tablaArticulos);
        scrollPane.setBorder(new LineBorder(CELESTE_BORDE, 1));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // 4. Barra de Botones Inferior (con Agregar Producto, Reponer y Actualizar)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelBotones.setBackground(FONDO_GENERAL);

        JButton btnNuevoProducto = crearBoton("[+] Agregar Nuevo Producto", AZUL_ACCENT);
        btnNuevoProducto.addActionListener(e -> abrirDialogoNuevoProducto());
        panelBotones.add(btnNuevoProducto);

        JButton btnReponer = crearBoton("Reponer Stock", AZUL_BOTON);
        btnReponer.addActionListener(e -> reponerStockAccion());
        panelBotones.add(btnReponer);

        JButton btnRefrescar = crearBoton("Actualizar Catálogo", Color.decode("#475569"));
        btnRefrescar.addActionListener(e -> cargarArticulos());
        panelBotones.add(btnRefrescar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        return panelPrincipal;
    }

    private JPanel crearCardKPI(String titulo, JLabel lblValor, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(
                new LineBorder(CELESTE_BORDE, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(Color.decode("#64748B"));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(colorAcento);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private JPanel createVentasPanel() {
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setBackground(FONDO_GENERAL);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(BLANCO);
        tarjeta.setBorder(new CompoundBorder(
                new LineBorder(CELESTE_BORDE, 2, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTituloCard = new JLabel("<html><h3 style='color: #0A2540; margin: 0;'>Módulo de Facturación y Venta</h3><span style='color: #64748B; font-size: 11px;'>Descuenta existencias en H2 y dispara alertas Observer</span></html>");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        tarjeta.add(lblTituloCard, gbc);

        gbc.gridwidth = 1;
        JLabel lblArticulo = new JLabel("Código del Artículo:");
        lblArticulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtIdArticulo = new JTextField(15);
        txtIdArticulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblCantidad = new JLabel("Cantidad a Vender:");
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtCantidad = new JTextField(15);
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnVender = crearBoton("Confirmar y Emitir Factura ($ ARS)", AZUL_ACCENT);

        gbc.gridx = 0; gbc.gridy = 1; tarjeta.add(lblArticulo, gbc);
        gbc.gridx = 1; gbc.gridy = 1; tarjeta.add(txtIdArticulo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; tarjeta.add(lblCantidad, gbc);
        gbc.gridx = 1; gbc.gridy = 2; tarjeta.add(txtCantidad, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        tarjeta.add(btnVender, gbc);

        btnVender.addActionListener(e -> {
            try {
                int codigo = Integer.parseInt(txtIdArticulo.getText().trim());
                int cant = Integer.parseInt(txtCantidad.getText().trim());

                Articulo art = articuloDAO.buscarArticuloPorCodigo(codigo);
                if (art == null) {
                    JOptionPane.showMessageDialog(this, "El artículo especificado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (art.getStockActual() < cant) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible actual: " + art.getStockActual(), "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                art.descontarStock(cant);
                articuloDAO.actualizar(art);

                if (art.getStockActual() <= art.getStockMinimo()) {
                    enStockMinimoAlcanzado(art);
                }

                double totalVenta = cant * art.getPrecioUnitario();
                JOptionPane.showMessageDialog(this, "¡Venta realizada con éxito!\nProducto: " + art.getDescripcion() 
                        + "\nTotal Facturado: " + formatoMonedaARS.format(totalVenta) 
                        + "\nNuevo Stock Disponible: " + art.getStockActual());
                
                txtIdArticulo.setText("");
                txtCantidad.setText("");
                cargarArticulos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos ingresados. Ingrese valores numéricos válidos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelContenedor.add(tarjeta);
        return panelContenedor;
    }

    private JPanel createAlertasPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(FONDO_GENERAL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("<html><b style='color: #0A2540; font-size: 14px;'>Registro en Tiempo Real de Alertas de Stock (Patrón Observer)</b></html>");
        panel.add(title, BorderLayout.NORTH);

        areaAlertas = new JTextArea();
        areaAlertas.setEditable(false);
        areaAlertas.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaAlertas.setBackground(BLANCO);
        areaAlertas.setForeground(TEXTO_DARK);
        areaAlertas.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaAlertas);
        scroll.setBorder(new LineBorder(CELESTE_BORDE, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(colorFondo);
        btn.setForeground(BLANCO);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(colorFondo.darker(), 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
        return btn;
    }

    private void abrirDialogoNuevoProducto() {
        JDialog dialogo = new JDialog(this, "Alta de Nuevo Producto - Sistema Mayorista", true);
        dialogo.setSize(450, 420);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(BLANCO);
        panelForm.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtCod = new JTextField(15);
        JTextField txtDesc = new JTextField(15);
        JTextField txtPrecio = new JTextField(15);
        JTextField txtStockInic = new JTextField(15);
        JTextField txtStockMin = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("Código Único:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelForm.add(txtCod, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("Descripción / Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelForm.add(txtDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Precio Unitario ($ ARS):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panelForm.add(txtPrecio, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("Stock Inicial:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panelForm.add(txtStockInic, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panelForm.add(new JLabel("Stock Mínimo (Alerta):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panelForm.add(txtStockMin, gbc);

        JButton btnGuardar = crearBoton("Guardar Producto en H2", AZUL_ACCENT);
        btnGuardar.addActionListener(e -> {
            try {
                int cod = Integer.parseInt(txtCod.getText().trim());
                String desc = txtDesc.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
                int stInic = Integer.parseInt(txtStockInic.getText().trim());
                int stMin = Integer.parseInt(txtStockMin.getText().trim());

                if (desc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "La descripción no puede estar vacía.", "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (articuloDAO.buscarArticuloPorCodigo(cod) != null) {
                    JOptionPane.showMessageDialog(dialogo, "Ya existe un artículo registrado con el código " + cod, "Código Duplicado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Obtener o crear Rubro y Proveedor por defecto para la relación JPA
                List<Articulo> exist = articuloDAO.obtenerTodosArticulos();
                Rubro defaultRubro = (!exist.isEmpty() && exist.get(0).getRubro() != null) ? exist.get(0).getRubro() : new Rubro(1, "General", "Componentes");
                Proveedor defaultProv = (!exist.isEmpty() && exist.get(0).getProveedor() != null) ? exist.get(0).getProveedor() : new Proveedor(101, "30-71123456-8", "TechSupplier S.A.", "Av. Corrientes 1234", "011-45678900", "contacto@techsupplier.com");

                Articulo nuevoArt = new Articulo(cod, desc, defaultRubro, defaultProv, stInic, stMin, precio);
                articuloDAO.guardar(nuevoArt);

                JOptionPane.showMessageDialog(dialogo, "¡Producto registrado con éxito en la base de datos!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
                cargarArticulos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Por favor verifique que los valores numéricos ingresados sean válidos.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBtn.setBackground(BLANCO);
        panelBtn.add(btnGuardar);

        dialogo.add(panelForm, BorderLayout.CENTER);
        dialogo.add(panelBtn, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    private void reponerStockAccion() {
        String codStr = JOptionPane.showInputDialog(this, "Ingrese el Código del artículo:");
        if (codStr == null || codStr.trim().isEmpty()) return;
        String cantStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad a ingresar:");
        if (cantStr == null || cantStr.trim().isEmpty()) return;

        try {
            int codigo = Integer.parseInt(codStr.trim());
            int cant = Integer.parseInt(cantStr.trim());
            Articulo art = articuloDAO.buscarArticuloPorCodigo(codigo);
            if (art != null) {
                art.agregarStock(cant);
                articuloDAO.actualizar(art);
                JOptionPane.showMessageDialog(this, "Stock actualizado exitosamente.");
                cargarArticulos();
            } else {
                JOptionPane.showMessageDialog(this, "Artículo no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.");
        }
    }

    private void cargarArticulos() {
        modelTabla.setRowCount(0);
        List<Articulo> lista = articuloDAO.obtenerTodosArticulos();

        int totalProds = 0;
        int criticosCount = 0;
        double valorTotalInm = 0;

        if (lista != null) {
            totalProds = lista.size();
            for (Articulo a : lista) {
                boolean esCritico = a.getStockActual() <= a.getStockMinimo();
                if (esCritico) criticosCount++;
                valorTotalInm += (a.getStockActual() * a.getPrecioUnitario());

                String estado = esCritico ? "CRÍTICO" : "NORMAL";
                modelTabla.addRow(new Object[]{
                        a.getCodigo(),
                        a.getDescripcion(),
                        formatoMonedaARS.format(a.getPrecioUnitario()),
                        a.getStockActual(),
                        a.getStockMinimo(),
                        estado
                });
            }
        }

        lblTotalProductos.setText(String.valueOf(totalProds));
        lblStockCriticoCount.setText(String.valueOf(criticosCount));
        lblValorTotalInventario.setText(formatoMonedaARS.format(valorTotalInm));
    }

    @Override
    public void enStockMinimoAlcanzado(Articulo articulo) {
        String mensaje = "[ALERTA OBSERVER] " + java.time.LocalTime.now().withNano(0) 
                         + " -> El producto '" + articulo.getDescripcion() 
                         + "' (Código: " + articulo.getCodigo() + ") alcanzó nivel crítico. Stock actual: " 
                         + articulo.getStockActual() + " (Mínimo: " + articulo.getStockMinimo() + ")\n";
        if (areaAlertas != null) {
            areaAlertas.append(mensaje);
        }
    }

    // Custom Cell Renderer para mostrar etiquetas tipo Badge (Verde NORMAL vs Rojo CRÍTICO)
    private class StockBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String estado = (String) table.getValueAt(row, 5);
            boolean esCritico = "CRÍTICO".equals(estado);

            if (column == 0 || column == 3 || column == 4 || column == 5) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else if (column == 2) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            if (column == 5) {
                if (esCritico) {
                    c.setBackground(ROJO_BG);
                    c.setForeground(ROJO_TXT);
                } else {
                    c.setBackground(VERDE_BG);
                    c.setForeground(VERDE_TXT);
                }
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else {
                c.setBackground(isSelected ? table.getSelectionBackground() : BLANCO);
                c.setForeground(isSelected ? table.getSelectionForeground() : TEXTO_DARK);
            }

            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}