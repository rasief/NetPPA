package co.edu.unab.ppa;

import co.edu.unab.FrmPrincipal;
import co.edu.unab.db.DbEventos;
import co.edu.unab.db.DbFormatosTexto;
import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.Evento;
import co.edu.unab.entidad.FormatoTexto;
import co.edu.unab.entidad.Red;
import co.edu.unab.procesos.PrCargaEventos;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Formulario de carga de archivos KML de eventos
 * @author Feisar Moreno
 * @date 04/03/2014
 */
public class FrmCargarEventos extends javax.swing.JInternalFrame {
    PrCargaEventos prCargaEventos = null;
    private long idEventoTmp = 0L;
    private final FrmPrincipal frmPrincipal;
    
    private class AvanceCarga implements Runnable {
        private boolean controlCorrer = true;
        private int cantPuntosAvance = 0;
        
        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            while (this.controlCorrer) {
                try {
                    Thread.sleep(500);
                    if (idEventoTmp > 0L) {
                        this.mostrarAvance();
                    }
                } catch (InterruptedException e) {}
            }
        }
        
        public void setControlCorrer(boolean controlCorrer) {
            this.controlCorrer = controlCorrer;
        }
        
        private void mostrarAvance() {
            boolean indInicioCreacionEvento = false;
            String mensajeAvance;
            
            if (prCargaEventos != null) {
                indInicioCreacionEvento = prCargaEventos.getIndInicioCreacionEvento();
            }
            
            if (indInicioCreacionEvento) {
                mensajeAvance = "Finishing points creation";
            } else {
                //Se obtiene la información de avance de la carga
                long cantPuntos = PrCargaEventos.getCantidadTmpPuntos(idEventoTmp);
                long cantPuntosAtributos = PrCargaEventos.getCantidadTmpPuntosAtributos(idEventoTmp);
                
                if (cantPuntosAtributos > 0) {
                    mensajeAvance = "Creating attributes (" + cantPuntosAtributos + ")";
                } else if (cantPuntos > 0) {
                    mensajeAvance = "Creating points (" + cantPuntos + ")";
                } else {
                    mensajeAvance = "Processing";
                }
            }
            
            this.cantPuntosAvance++;
            this.cantPuntosAvance = this.cantPuntosAvance % 3;
            mensajeAvance += "....".substring(0, this.cantPuntosAvance + 1);
            lblAvance.setText(mensajeAvance);
        }
    }
    
    /**
     * Creates new form FrmCargarEventos
     * @param frmPrincipal Formulario que contiene a este formulario
     */
    public FrmCargarEventos(FrmPrincipal frmPrincipal) {
        initComponents();
        
        this.frmPrincipal = frmPrincipal;
        
        //Se limpia el texto de avance de carga
        this.lblAvance.setVisible(false);
        this.lblAvance.setText(" ");
        
        //Se carga el combo de redes
        DbRedes dbRedes = new DbRedes();
        ArrayList<Red> listaRedes = dbRedes.getListaRedes(false);
        
        this.cmbRed.removeAllItems();
        for (Red redAux : listaRedes) {
            this.cmbRed.addItem(redAux);
        }
        this.cmbRed.setSelectedIndex(-1);
        
        //Se carga el listado de eventos en la tabla
        this.cargarTablaEventos();
        
        //Se carga el combo de formatos de fecha
        FormatoTexto formatoVacio = new FormatoTexto(0, "(None)", "", "", 0, 1);
        DbFormatosTexto dbFormatosTexto = new DbFormatosTexto();
        ArrayList<FormatoTexto> listaFormatosFecha = dbFormatosTexto.getListaFormatosTexto(false, "date", 1);
        
        this.cmbFormatoFecha.removeAllItems();
        this.cmbFormatoFecha.addItem(formatoVacio);
        for (FormatoTexto formatoTextoAux : listaFormatosFecha) {
            this.cmbFormatoFecha.addItem(formatoTextoAux);
        }
        this.cmbFormatoFecha.setSelectedIndex(0);
        this.cmbFormatoFecha.setEnabled(false);
        
        //Se carga el combo de formatos de hora
        ArrayList<FormatoTexto> listaFormatosHora = dbFormatosTexto.getListaFormatosTexto(false, "time", 1);
        
        this.cmbFormatoHora.removeAllItems();
        this.cmbFormatoHora.addItem(formatoVacio);
        for (FormatoTexto formatoTextoAux : listaFormatosHora) {
            this.cmbFormatoHora.addItem(formatoTextoAux);
        }
        this.cmbFormatoHora.setSelectedIndex(0);
        this.cmbFormatoHora.setEnabled(false);
    }
    
    public FrmPrincipal getFrmPrincipal() {
        return frmPrincipal;
    }
    
    /**
     * Método que valida si se han seleccionado todos los campos requeridos
     * @return <code>true</code> si se seleccionaron todos los campos requeridos,
     * de lo contrario <code>false</code>.
     */
    private boolean validarCampos() {
        //Se valida que se hayan diligenciado todos los campos
        if (this.txtDescEvento.getText().length() <= 5) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type a valid event description.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDescEvento.requestFocusInWindow();
            return false;
        }
        
        if (this.cmbRed.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a network.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbRed.requestFocusInWindow();
            return false;
        }
        
        if (this.txtArchEventos.getText().equals("")) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a points KML file.", "Error", JOptionPane.ERROR_MESSAGE);
            this.btnBuscarArchivo.requestFocusInWindow();
            return false;
        }
        
        if (this.cmbAtributoFecha.getSelectedIndex() > 0 && this.cmbFormatoFecha.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select the date format.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbFormatoFecha.requestFocusInWindow();
            return false;
        }
        
        if (this.cmbAtributoHora.getSelectedIndex() > 0 && this.cmbFormatoHora.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select the time format.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbFormatoHora.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
    
    public void habilitarComponentes(boolean habilitar) {
        this.txtDescEvento.setEnabled(habilitar);
        this.cmbRed.setEnabled(habilitar);
        this.btnBuscarArchivo.setEnabled(habilitar);
        this.btnCargar.setEnabled(habilitar);
        this.tblEventos.setEnabled(habilitar);
        this.cmbAtributoFecha.setEnabled(habilitar);
        this.cmbAtributoHora.setEnabled(habilitar);
        if (habilitar) {
            this.cmbFormatoFecha.setEnabled(this.cmbAtributoFecha.getSelectedIndex() > 0);
            this.cmbFormatoHora.setEnabled(this.cmbAtributoHora.getSelectedIndex() > 0);
        } else {
            this.cmbFormatoFecha.setEnabled(habilitar);
            this.cmbFormatoHora.setEnabled(habilitar);
        }
    }
    
    private void cargarTablaEventos() {
        //Nombres de las columnas
        String[] nombCols = new String[6];
        nombCols[0] = "Event";
        nombCols[1] = "Network";
        nombCols[2] = "Attributes";
        nombCols[3] = "Points";
        nombCols[4] = "Delete";
        nombCols[5] = "View";
        
        //Se obtienen los registros de eventos
        DbEventos dbEventos = new DbEventos();
        ArrayList<Evento> listaEventos = dbEventos.getListaEventos(false);
        long[] arrIdRed = new long[listaEventos.size()];
        long[] arrIdEvento = new long[listaEventos.size()];
        String [][] cuerpoTabla = new String[listaEventos.size()][0];
        for (int i = 0; i < listaEventos.size(); i++) {
            Evento eventoAux = listaEventos.get(i);
            
            arrIdRed[i] = eventoAux.getRed().getIdRed();
            arrIdEvento[i] = eventoAux.getIdEvento();
            
            String[] registroAux = new String[6];
            registroAux[0] = eventoAux.getDescEvento();
            registroAux[1] = eventoAux.getRed().getDescRed();
            registroAux[2] = "" + eventoAux.getCantAtributos();
            registroAux[3] = "" + eventoAux.getCantPuntos();
            registroAux[4] = " ... ";
            registroAux[5] = " ... ";
            
            cuerpoTabla[i] = registroAux;
        }

        DefaultTableModel tablaEventos = new DefaultTableModel(cuerpoTabla, nombCols) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 4 || columnIndex == 5;
            }
        };

        this.tblEventos.setModel(tablaEventos);
        TableColumnModel columnas = this.tblEventos.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(220);
        columnas.getColumn(1).setPreferredWidth(160);
        columnas.getColumn(2).setPreferredWidth(75);
        columnas.getColumn(3).setPreferredWidth(75);
        columnas.getColumn(4).setPreferredWidth(45);
        columnas.getColumn(5).setPreferredWidth(45);
        
        this.tblEventos.getColumn("Delete").setCellRenderer(new FrmCargarEventos.ButtonRendererCargaEventos());
        this.tblEventos.getColumn("View").setCellRenderer(new FrmCargarEventos.ButtonRendererCargaEventos());
        this.tblEventos.getColumn("Delete").setCellEditor(
            new FrmCargarEventos.ButtonEditorCargaEventos(new JCheckBox(), this.tblEventos, this, arrIdRed, arrIdEvento, 'B')
        );
        this.tblEventos.getColumn("View").setCellEditor(
            new FrmCargarEventos.ButtonEditorCargaEventos(new JCheckBox(), this.tblEventos, this, arrIdRed, arrIdEvento, 'V')
        );
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbRed = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        btnBuscarArchivo = new javax.swing.JButton();
        txtArchEventos = new javax.swing.JTextField();
        txtDescEvento = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cmbAtributoFecha = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        cmbFormatoFecha = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        cmbAtributoHora = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        cmbFormatoHora = new javax.swing.JComboBox();
        lblAvance = new javax.swing.JLabel();
        btnCargar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEventos = new javax.swing.JTable();

        setClosable(true);
        setTitle("Event (Points) Files");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("EVENT (POINTS) FILES");

        jLabel2.setText("Event description");

        jLabel3.setText("Network");

        jLabel6.setText("KML file");

        btnBuscarArchivo.setText("...");
        btnBuscarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarArchivoActionPerformed(evt);
            }
        });

        txtArchEventos.setEnabled(false);

        txtDescEvento.setToolTipText("");

        jLabel7.setText("Date attribute");

        cmbAtributoFecha.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "(None)" }));
        cmbAtributoFecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAtributoFechaItemStateChanged(evt);
            }
        });

        jLabel8.setText("Date format");

        jLabel9.setText("Time attribute");

        cmbAtributoHora.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "(None)" }));
        cmbAtributoHora.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAtributoHoraItemStateChanged(evt);
            }
        });

        jLabel10.setText("Time format");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDescEvento)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtArchEventos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarArchivo))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6)
                            .addComponent(cmbRed, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cmbAtributoFecha, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbFormatoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cmbAtributoHora, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbFormatoHora, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscarArchivo)
                    .addComponent(txtArchEventos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbAtributoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFormatoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbAtributoHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFormatoHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblAvance.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAvance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAvance.setText(".");

        btnCargar.setText("Upload");
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActionPerformed(evt);
            }
        });

        tblEventos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblEventos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblEventos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCargar)
                    .addComponent(lblAvance, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCargar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAvance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void btnBuscarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarArchivoActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int retornoSel = fileChooser.showOpenDialog(this);
        if (retornoSel == JFileChooser.APPROVE_OPTION) {
            this.txtArchEventos.setText(fileChooser.getSelectedFile().getAbsolutePath());
            
            //Se obtienen los atributos del archivo cargado
            PrCargaEventos pce = new PrCargaEventos(this.txtArchEventos.getText());
            ArrayList<String> listaAtributos = pce.obtenerListaAtributos();
            
            //Se cargan los atributos al combo de fechas
            this.cmbAtributoFecha.removeAllItems();
            this.cmbAtributoFecha.addItem("(None)");
            for (String atributoAux : listaAtributos) {
                this.cmbAtributoFecha.addItem(atributoAux);
            }
            this.cmbAtributoFecha.setSelectedIndex(0);
            
            //Se cargan los atributos al combo de horas
            this.cmbAtributoHora.removeAllItems();
            this.cmbAtributoHora.addItem("(None)");
            for (String atributoAux : listaAtributos) {
                this.cmbAtributoHora.addItem(atributoAux);
            }
            this.cmbAtributoHora.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnBuscarArchivoActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        //Se valida que se hayan diligenciado todos los campos
        if (this.validarCampos()) {
            //Se inhabilitan los componentes
            this.habilitarComponentes(false);
            
            //Se inicia el hilo que muestra el avance del proceso
            final AvanceCarga tareaCarga = new AvanceCarga();
            Thread hiloProcesar = new Thread(tareaCarga, "Procesando");
            hiloProcesar.start();
            
            Runnable procesoCarga = new Runnable() {
                @Override
                public void run() {
                    boolean resultado = true;
                    try {
                        lblAvance.setVisible(true);
                        String descEvento = txtDescEvento.getText();
                        Red red = (Red)cmbRed.getSelectedItem();
                        String nombreArchivo = txtArchEventos.getText();
                        
                        //Se llama a la clase que realiza la carga de archivos
                        prCargaEventos = new PrCargaEventos(descEvento, red, nombreArchivo, (String)cmbAtributoFecha.getSelectedItem(), ((FormatoTexto)cmbFormatoFecha.getSelectedItem()).getFormato(), (String)cmbAtributoHora.getSelectedItem(), ((FormatoTexto)cmbFormatoHora.getSelectedItem()).getFormato(), frmPrincipal);
                        idEventoTmp = prCargaEventos.getIdEventoTmp();
                        
                        //Se realiza la carga
                        resultado = prCargaEventos.cargarArchivo();
                    } finally {
                        tareaCarga.setControlCorrer(false);
                        lblAvance.setVisible(false);
                        habilitarComponentes(true);
                    }
                    
                    if (resultado) {
                        //Si el resultado fue correcto, se actualiza la tabla de redes
                        cargarTablaEventos();
                        
                        JOptionPane.showMessageDialog(frmPrincipal, "Points file uploaded successfully.", "File Upload", JOptionPane.INFORMATION_MESSAGE);
                        
                        //Se limpia el formulario
                        txtDescEvento.setText("");
                        cmbRed.setSelectedIndex(-1);
                        txtArchEventos.setText("");
                        habilitarComponentes(true);
                    } else {
                        JOptionPane.showMessageDialog(frmPrincipal, "Format error in points KML file.", "File Upload", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            Thread hiloCarga = new Thread(procesoCarga, "procesarCarga");
            hiloCarga.start();
        }
    }//GEN-LAST:event_btnCargarActionPerformed

    private void cmbAtributoFechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAtributoFechaItemStateChanged
        if (this.cmbAtributoFecha.getSelectedIndex() == 0) {
            this.cmbFormatoFecha.setSelectedIndex(0);
            this.cmbFormatoFecha.setEnabled(false);
        } else {
            this.cmbFormatoFecha.setEnabled(true);
        }
    }//GEN-LAST:event_cmbAtributoFechaItemStateChanged

    private void cmbAtributoHoraItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAtributoHoraItemStateChanged
        if (this.cmbAtributoHora.getSelectedIndex() == 0) {
            this.cmbFormatoHora.setSelectedIndex(0);
            this.cmbFormatoHora.setEnabled(false);
        } else {
            this.cmbFormatoHora.setEnabled(true);
        }
    }//GEN-LAST:event_cmbAtributoHoraItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarArchivo;
    private javax.swing.JButton btnCargar;
    private javax.swing.JComboBox cmbAtributoFecha;
    private javax.swing.JComboBox cmbAtributoHora;
    private javax.swing.JComboBox cmbFormatoFecha;
    private javax.swing.JComboBox cmbFormatoHora;
    private javax.swing.JComboBox cmbRed;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAvance;
    private javax.swing.JTable tblEventos;
    private javax.swing.JTextField txtArchEventos;
    private javax.swing.JTextField txtDescEvento;
    // End of variables declaration//GEN-END:variables
    
    class ButtonRendererCargaEventos extends JButton implements TableCellRenderer {
        public ButtonRendererCargaEventos() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    class ButtonEditorCargaEventos extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final char tipoAccion;
        private final JTable table;
        private final FrmCargarEventos frmCargarEventos;
        private final long[] arrIdRed;
        private final long[] arrIdEvento;
        
        public ButtonEditorCargaEventos(JCheckBox checkBox, JTable table, FrmCargarEventos frmCargarEventos, long[] arrIdRed, long[] arrIdEvento, char tipoAccion) {
            super(checkBox);
            this.table = table;
            this.frmCargarEventos = frmCargarEventos;
            this.arrIdRed = arrIdRed;
            this.arrIdEvento = arrIdEvento;
            this.tipoAccion = tipoAccion;
            
            this.button = new JButton();
            this.button.setOpaque(true);
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                this.button.setForeground(table.getSelectionForeground());
                this.button.setBackground(table.getSelectionBackground());
            } else {
                this.button.setForeground(table.getForeground());
                this.button.setBackground(table.getBackground());
            }
            this.label = (value == null) ? "" : value.toString();
            this.button.setText(this.label);
            this.isPushed = true;
            return this.button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (this.isPushed) {
                switch(this.tipoAccion) {
                    case 'V': //Ver
                        FrmMostrarEvento frmMostrarEvento = new FrmMostrarEvento(this.arrIdRed[this.table.getSelectedRow()], this.arrIdEvento[this.table.getSelectedRow()], this.frmCargarEventos);
                        this.frmCargarEventos.getFrmPrincipal().getPanPrincipal().add(frmMostrarEvento, 0);
                        this.frmCargarEventos.habilitarComponentes(false);
                        this.frmCargarEventos.setVisible(false);
                        frmMostrarEvento.setVisible(true);
                        try {
                            frmMostrarEvento.setMaximum(true);
                        } catch (PropertyVetoException ex) {
                        }
                        frmMostrarEvento.mostrarEvento();
                        break;
                    case 'B': //Borrar
                        int seleccionAux = JOptionPane.showConfirmDialog(frmPrincipal, "Do you want to delete the event?", "Deleting Events", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (seleccionAux == JOptionPane.YES_OPTION) {
                            habilitarComponentes(false);
                            DbEventos dbEventos = new DbEventos();
                            int resultadoBorrar = dbEventos.borrarEvento(false, this.arrIdEvento[this.table.getSelectedRow()]);
                            switch (resultadoBorrar) {
                                case 1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Event deleted successfully.", "Deleting Events", JOptionPane.INFORMATION_MESSAGE);
                                    cargarTablaEventos();
                                    break;
                                case -1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Internal error during the deleting process.", "Deleting Events", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case -3:
                                    JOptionPane.showMessageDialog(frmPrincipal, "The event cannot be deleted because it has network K function results associated.\nPlease delete those results before trying to delete the event.", "Deleting Events", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case -4:
                                    JOptionPane.showMessageDialog(frmPrincipal, "The event cannot be deleted because it has NetKDE results associated.\nPlease delete those results before trying to delete the event.", "Deleting Events", JOptionPane.ERROR_MESSAGE);
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Error code (" + resultadoBorrar + ") during the deleting process.", "Deleting Events", JOptionPane.ERROR_MESSAGE);
                                    break;
                            }
                            habilitarComponentes(true);
                        }
                        break;
                }
            }
            this.isPushed = false;
            return this.label;
        }
        
        @Override
        public boolean stopCellEditing() {
            this.isPushed = false;
            return super.stopCellEditing();
        }
        
        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
