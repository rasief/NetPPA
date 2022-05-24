package co.edu.unab.ppa;

import co.edu.unab.FrmPrincipal;
import co.edu.unab.db.DbRedes;
import co.edu.unab.db.DbSistemasCoordenadas;
import co.edu.unab.entidad.Red;
import co.edu.unab.entidad.SistemaCoordenadas;
import co.edu.unab.procesos.PrCargaRedes;
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
 * Formulario de carga de archivos KML de redes
 * @author Feisar Moreno
 * @date 24/02/2014
 */
public class FrmCargarRedes extends javax.swing.JInternalFrame {
    PrCargaRedes prCargaRedes = null;
    private long idRedTmp = 0;
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
                    if (idRedTmp > 0L) {
                        this.mostrarAvance();
                    }
                } catch (InterruptedException e) {}
            }
        }
        
        public void setControlCorrer(boolean controlCorrer) {
            this.controlCorrer = controlCorrer;
        }
        
        private void mostrarAvance() {
            boolean indInicioCreacionRed = false;
            String mensajeAvance;
            
            if (prCargaRedes != null) {
                indInicioCreacionRed = prCargaRedes.getIndInicioCreacionRed();
            }
            
            if (indInicioCreacionRed) {
                mensajeAvance = "Calculating lines length";
            } else {
                //Se obtiene la información de avance de la carga
                long cantLineas = PrCargaRedes.getCantidadTmpLineas(idRedTmp);
                long cantLineasAtributos = PrCargaRedes.getCantidadTmpLineasAtributos(idRedTmp);
                long cantLineasDet = PrCargaRedes.getCantidadTmpLineasDet(idRedTmp);
                
                if (cantLineasDet > 0) {
                    mensajeAvance = "Creating line details (" + cantLineasDet + ")";
                } else if (cantLineasAtributos > 0) {
                    mensajeAvance = "Creating attributes (" + cantLineasAtributos + ")";
                } else if (cantLineas > 0) {
                    mensajeAvance = "Creating lines (" + cantLineas + ")";
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
     * Creates new form FrmCargarRedes
     * @param frmPrincipal Formulario que contiene a este formulario
     */
    public FrmCargarRedes(FrmPrincipal frmPrincipal) {
        initComponents();
        
        this.frmPrincipal = frmPrincipal;
        
        //Se limpia el texto de avance de carga
        this.lblAvance.setVisible(false);
        this.lblAvance.setText(" ");
        
        //Se carga el combo de sistemas de coordenadas
        DbSistemasCoordenadas dbSistemasCoordenadas = new DbSistemasCoordenadas();
        ArrayList<SistemaCoordenadas> listaSistemasCoordenadas = dbSistemasCoordenadas.getListaSistemasCoordenadas();
        
        this.cmbSistemaCoordenadas.removeAllItems();
        for (SistemaCoordenadas sistemaAux : listaSistemasCoordenadas) {
            this.cmbSistemaCoordenadas.addItem(sistemaAux);
        }
        this.cmbSistemaCoordenadas.setSelectedIndex(-1);
        
        //Se carga el listado de redes en la tabla
        this.cargarTablaRedes();
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
        if (this.txtDescRed.getText().length() <= 5) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type a valid network description.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDescRed.requestFocusInWindow();
            return false;
        }
        
        if (this.cmbSistemaCoordenadas.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a coordinate system.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbSistemaCoordenadas.requestFocusInWindow();
            return false;
        }
        
        if (this.txtArchRed.getText().equals("")) {
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a network KML file.", "Error", JOptionPane.ERROR_MESSAGE);
            this.btnBuscarArchivo.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
    
    public void habilitarComponentes(boolean habilitar) {
        this.txtDescRed.setEnabled(habilitar);
        this.cmbSistemaCoordenadas.setEnabled(habilitar);
        this.btnBuscarArchivo.setEnabled(habilitar);
        this.btnCargar.setEnabled(habilitar);
        this.tblRedes.setEnabled(habilitar);
    }
    
    private void cargarTablaRedes() {
        //Nombres de las columnas
        String[] nombCols = new String[5];
        nombCols[0] = "Network";
        nombCols[1] = "Attributes";
        nombCols[2] = "Lines";
        nombCols[3] = "Delete";
        nombCols[4] = "View";

        //Se obtienen los registros de redes
        DbRedes dbRedes = new DbRedes();
        ArrayList<Red> listaRedes = dbRedes.getListaRedes(false);
        long[] arrIdRed = new long[listaRedes.size()];
        String [][] cuerpoTabla = new String[listaRedes.size()][0];
        for (int i = 0; i < listaRedes.size(); i++) {
            Red redAux = listaRedes.get(i);
            
            arrIdRed[i] = redAux.getIdRed();
            
            String[] registroAux = new String[5];
            registroAux[0] = redAux.getDescRed();
            registroAux[1] = "" + redAux.getCantAtributos();
            registroAux[2] = "" + redAux.getCantLineas();
            registroAux[3] = " ... ";
            registroAux[4] = " ... ";
            
            cuerpoTabla[i] = registroAux;
        }

        DefaultTableModel tablaRedes = new DefaultTableModel(cuerpoTabla, nombCols) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 3 || columnIndex == 4;
            }
        };

        this.tblRedes.setModel(tablaRedes);
        TableColumnModel columnas = this.tblRedes.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(275);
        columnas.getColumn(1).setPreferredWidth(90);
        columnas.getColumn(2).setPreferredWidth(90);
        columnas.getColumn(3).setPreferredWidth(45);
        columnas.getColumn(4).setPreferredWidth(45);
        
        this.tblRedes.getColumn("Delete").setCellRenderer(new FrmCargarRedes.ButtonRendererCargaRedes());
        this.tblRedes.getColumn("View").setCellRenderer(new FrmCargarRedes.ButtonRendererCargaRedes());
        this.tblRedes.getColumn("Delete").setCellEditor(
            new FrmCargarRedes.ButtonEditorCargaRedes(new JCheckBox(), this.tblRedes, this, arrIdRed, 'B')
        );
        this.tblRedes.getColumn("View").setCellEditor(
            new FrmCargarRedes.ButtonEditorCargaRedes(new JCheckBox(), this.tblRedes, this, arrIdRed, 'V')
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
        cmbSistemaCoordenadas = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        btnBuscarArchivo = new javax.swing.JButton();
        txtArchRed = new javax.swing.JTextField();
        txtDescRed = new javax.swing.JTextField();
        lblAvance = new javax.swing.JLabel();
        btnCargar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRedes = new javax.swing.JTable();

        setClosable(true);
        setTitle("Network Files");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NETWORK FILES");

        jLabel2.setText("Network description");

        jLabel3.setText("Coordinate system");

        cmbSistemaCoordenadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSistemaCoordenadasActionPerformed(evt);
            }
        });

        jLabel6.setText("KML file");

        btnBuscarArchivo.setText("...");
        btnBuscarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarArchivoActionPerformed(evt);
            }
        });

        txtArchRed.setEnabled(false);

        txtDescRed.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDescRed)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtArchRed)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarArchivo))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6)
                            .addComponent(cmbSistemaCoordenadas, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 294, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSistemaCoordenadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscarArchivo)
                    .addComponent(txtArchRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        tblRedes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblRedes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblRedes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCargar, javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblAvance, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
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

    private void cmbSistemaCoordenadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSistemaCoordenadasActionPerformed
        
    }//GEN-LAST:event_cmbSistemaCoordenadasActionPerformed
        
    private void btnBuscarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarArchivoActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int retornoSel = fileChooser.showOpenDialog(this);
        if (retornoSel == JFileChooser.APPROVE_OPTION) {
            txtArchRed.setText(fileChooser.getSelectedFile().getAbsolutePath());
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
                        String descRed = txtDescRed.getText();
                        SistemaCoordenadas sistemaCoordenadas = (SistemaCoordenadas)cmbSistemaCoordenadas.getSelectedItem();
                        String nombreArchivo = txtArchRed.getText();
                        
                        //Se llama a la clase que realiza la carga de archivos
                        prCargaRedes = new PrCargaRedes(descRed, sistemaCoordenadas, nombreArchivo, frmPrincipal);
                        idRedTmp = prCargaRedes.getIdRedTmp();
                        
                        //Se realiza la carga
                        resultado = prCargaRedes.cargarArchivo();
                    } finally {
                        tareaCarga.setControlCorrer(false);
                        lblAvance.setVisible(false);
                        habilitarComponentes(true);
                    }
                    
                    if (resultado) {
                        //Si el resultado fue correcto, se actualiza la tabla de redes
                        cargarTablaRedes();
                        
                        JOptionPane.showMessageDialog(frmPrincipal, "Network file uploaded successfully.", "File Upload", JOptionPane.INFORMATION_MESSAGE);
                        
                        //Se limpia el formulario
                        txtDescRed.setText("");
                        cmbSistemaCoordenadas.setSelectedIndex(-1);
                        txtArchRed.setText("");
                        habilitarComponentes(true);
                    } else {
                        JOptionPane.showMessageDialog(frmPrincipal, "Format error in network KML file.", "File Upload", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            Thread hiloCarga = new Thread(procesoCarga, "procesarCarga");
            hiloCarga.start();
        }
    }//GEN-LAST:event_btnCargarActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarArchivo;
    private javax.swing.JButton btnCargar;
    private javax.swing.JComboBox cmbSistemaCoordenadas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAvance;
    private javax.swing.JTable tblRedes;
    private javax.swing.JTextField txtArchRed;
    private javax.swing.JTextField txtDescRed;
    // End of variables declaration//GEN-END:variables
    
    class ButtonRendererCargaRedes extends JButton implements TableCellRenderer {
        public ButtonRendererCargaRedes() {
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
    
    class ButtonEditorCargaRedes extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final char tipoAccion;
        private final JTable table;
        private final FrmCargarRedes frmCargarRedes;
        private final long[] arrIdRed;
        
        public ButtonEditorCargaRedes(JCheckBox checkBox, JTable table, FrmCargarRedes frmCargarRedes, long[] arrIdRed, char tipoAccion) {
            super(checkBox);
            this.table = table;
            this.frmCargarRedes = frmCargarRedes;
            this.arrIdRed = arrIdRed;
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
                        FrmMostrarRed frmMostrarRed = new FrmMostrarRed(this.arrIdRed[this.table.getSelectedRow()], this.frmCargarRedes);
                        this.frmCargarRedes.getFrmPrincipal().getPanPrincipal().add(frmMostrarRed, 0);
                        this.frmCargarRedes.habilitarComponentes(false);
                        this.frmCargarRedes.setVisible(false);
                        frmMostrarRed.setVisible(true);
                        try {
                            frmMostrarRed.setMaximum(true);
                        } catch (PropertyVetoException ex) {
                        }
                        frmMostrarRed.mostrarRed();
                        break;
                    case 'B': //Borrar
                        int seleccionAux = JOptionPane.showConfirmDialog(frmPrincipal, "Do you want to delete the network?", "Deleting Networks", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (seleccionAux == JOptionPane.YES_OPTION) {
                            habilitarComponentes(false);
                            DbRedes dbRedes = new DbRedes();
                            int resultadoBorrar = dbRedes.borrarRed(false, this.arrIdRed[this.table.getSelectedRow()]);
                            switch (resultadoBorrar) {
                                case 1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Network deleted successfully.", "Deleting Networks", JOptionPane.INFORMATION_MESSAGE);
                                    cargarTablaRedes();
                                    break;
                                case -1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Internal error during the deleting process.", "Deleting Networks", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case -3:
                                    JOptionPane.showMessageDialog(frmPrincipal, "The network cannot be deleted because it has events associated.\nPlease delete those events before trying to delete the network.", "Deleting Networks", JOptionPane.ERROR_MESSAGE);
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Error code (" + resultadoBorrar + ") during the deleting process.", "Deleting Networks", JOptionPane.ERROR_MESSAGE);
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
