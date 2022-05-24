package co.edu.unab.ppa;

import co.edu.unab.db.DbNetKDE;
import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.*;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.JInternalFrame;

/**
 * Formulario para mostrar resultados de NetKDE
 * @author Feisar Moreno
 * @date 18/06/2016
 */
public class FrmMostrarNetKDE extends JInternalFrame {
    private final long idRed;
    private final long idNetKDE;
    private final FrmCalcularNetKDE frmCalcularNetKDE;
    private final NetKDEResultado netKDEResultado;
    private VisorRed visorRed, visorRed2;
    private boolean indSegResul = false;
    private Dimension dimensionBase;
            
    /**
     * Constructor para el formulario FrmMostrarNetKDE
     * @param idRed Identificador de la red
     * @param idNetKDE Identificador del resultado a mostrar
     * @param frmCalcularNetKDE Formulario del cálculo de NetKDE
     */
    public FrmMostrarNetKDE(long idRed, long idNetKDE, FrmCalcularNetKDE frmCalcularNetKDE) {
        initComponents();
        
        this.idRed = idRed;
        this.idNetKDE = idNetKDE;
        this.frmCalcularNetKDE = frmCalcularNetKDE;
        
        //Se cargan los valores de resultado a mostrar
        DbNetKDE dbNetKDE = new DbNetKDE();
        this.netKDEResultado = dbNetKDE.getNetKDEResultado(false, this.idNetKDE);
        
        //Se cargan los datos del resultado
        String textoAux =
                "<html><b>Event:</b> " + this.netKDEResultado.getEvento().getDescEvento() + "<br /><br />" +
                "<b>Bandwidth (meters):</b> " + this.netKDEResultado.getAnchoBanda()+ "<br />" +
                "<b>Lixel length (meters):</b> " + this.netKDEResultado.getLargoLixel()+ "<br />" +
                "<b>Kernel function:</b> " + this.netKDEResultado.getFuncionNucleo().getNombreFuncion() + "<br />" +
                "<b>Number of points:</b> " + this.netKDEResultado.getCantPuntos() + "</html>";
        lblInformacion.setText(textoAux);
        
        //Se cargan los filtros del resultado
        textoAux = this.netKDEResultado.getFiltrosResultado().replaceAll("; ", "\n");
        textoAux = textoAux.equals("") ? "(None)" : textoAux;
        txtFiltros.setText(textoAux.replaceAll("; ", "\n"));
        
        //Se carga el combo de atributos de red
        DbRedes dbRedes = new DbRedes();
        ArrayList<RedAtributo> listaRedAtributos = dbRedes.getListaRedesAtributos(false, this.idRed);
        
        this.cmbAtributosRed.removeAllItems();
        for (RedAtributo redAtributoAux : listaRedAtributos) {
            this.cmbAtributosRed.addItem(redAtributoAux);
        }
        this.cmbAtributosRed.setSelectedIndex(-1);
        this.cmbAtributosRed.setEnabled(false);
        
        //Se agrega el listener para cambios al comboBox de redes
        this.cmbAtributosRed.addItemListener(
            new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    seleccionarAtributo();
                }
            }
        );
        
        //Se carga el procentaje inicial de resalte en la etiqueta
        this.lblPorcResalte.setText(this.sliResalte.getValue() + "%");
        
        //Se carga el combo de evento para comparar
        ArrayList<NetKDEResultado> listaNetKDEResultados = dbNetKDE.getListaNetKDEResultadosOtros(false, this.idNetKDE);
        
        this.cmbNetKDEComparar.removeAllItems();
        for (NetKDEResultado netKDEResultadoAux : listaNetKDEResultados) {
            this.cmbNetKDEComparar.addItem(netKDEResultadoAux);
        }
        this.cmbNetKDEComparar.setSelectedIndex(-1);
        this.cmbNetKDEComparar.setEnabled(false);
        
        //Se agrega el listener para cambios al comboBox de redes
        this.cmbNetKDEComparar.addItemListener(
            new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    seleccionarNetKDEComparar();
                }
            }
        );
    }
    
    @Override
    public void dispose() {
        this.frmCalcularNetKDE.habilitarComponentes(true);
        this.frmCalcularNetKDE.setVisible(true);
        super.dispose();
    }
    
    /**
     * Método que muestra los resultados de NetKDE.
     */
    public void mostrarResultados() {
        //Se agrega el lienzo al panel
        RedAtributo redAtributoAux = (RedAtributo)this.cmbAtributosRed.getSelectedItem();
        
        this.panContenedor.setLayout(new java.awt.GridLayout(1, 1));
        this.panContenedor.removeAll();
        
        this.visorRed = new VisorRed(this.idRed, this.idNetKDE, this.chkEtiquetasRed.isSelected(), this.chkLeyenda.isSelected(), redAtributoAux, this.sliResalte.getValue());
        this.panResultados.removeAll();
        this.panResultados.add(visorRed);
        this.panContenedor.add(this.panResultados);
        this.dimensionBase = this.panResultados.getSize();
        
        this.panResultados.setSize(this.panContenedor.getSize());
        this.visorRed.setSize(this.panResultados.getSize());
    }
    
    private void seleccionarAtributo() {
        if (this.chkEtiquetasRed.isSelected() && this.cmbAtributosRed.getSelectedIndex() >= 0) {
            this.visorRed.setIndVerAtributos(this.chkEtiquetasRed.isSelected());
            this.visorRed.setRedAtributo((RedAtributo)this.cmbAtributosRed.getSelectedItem());
            this.visorRed.repaint();
            
            if (this.indSegResul) {
                this.visorRed2.setIndVerAtributos(this.chkEtiquetasRed.isSelected());
                this.visorRed2.setRedAtributo((RedAtributo)this.cmbAtributosRed.getSelectedItem());
                this.visorRed2.repaint();
            }
        }
    }
    
    private void seleccionarNetKDEComparar() {
        if (this.chkNetKDEComparar.isSelected() && this.cmbNetKDEComparar.getSelectedIndex() >= 0) {
            //Se obtienen los datos del resultado
            NetKDEResultado netKDEResultadoAux = (NetKDEResultado)this.cmbNetKDEComparar.getSelectedItem();
            long idNetKDEAux = netKDEResultadoAux.getIdNetKDE();
            
            //Se cargan los datos del segundo resultado
            String textoAux =
                    "<html><b>Bandwidth (meters):</b> " + netKDEResultadoAux.getAnchoBanda()+ "<br />" +
                    "<b>Lixel length (meters):</b> " + netKDEResultadoAux.getLargoLixel()+ "<br />" +
                    "<b>Kernel function:</b> " + netKDEResultadoAux.getFuncionNucleo().getNombreFuncion() + "<br />" +
                    "<b>Number of points:</b> " + netKDEResultadoAux.getCantPuntos() + "</html>";
            lblInformacionComparar.setText(textoAux);
            
            //Se reajusta el tamaño del primer contenedor
            this.panResultados.setSize(this.dimensionBase);
            this.visorRed.setSize(this.panResultados.getSize());
            this.visorRed.repaint();
            
            //Se carga el segundo resultado
            RedAtributo redAtributoAux = (RedAtributo)this.cmbAtributosRed.getSelectedItem();
            this.visorRed2 = new VisorRed(this.idRed, idNetKDEAux, this.chkEtiquetasRed.isSelected(), this.chkLeyenda.isSelected(), redAtributoAux, this.sliResalte.getValue());
            this.panResultados2.removeAll();
            this.panResultados2.add(visorRed2);
            this.panContenedor.add(this.panResultados2);
            this.visorRed2.setSize(this.panResultados2.getSize());
            
            this.visorRed.setVisor2(this.visorRed2);
            this.visorRed2.setVisor2(this.visorRed);
            
            //Se asignan las mismas propiedades de tamaño y ubicación del primer resultado al segundo
            this.visorRed2.setXCentro(this.visorRed.getXCentro());
            this.visorRed2.setYCentro(this.visorRed.getYCentro());
            this.visorRed2.setFactorZoom(this.visorRed.getFactorZoom());
            
            this.visorRed2.setIndVerAtributos(this.chkEtiquetasRed.isSelected());
            this.visorRed2.setRedAtributo((RedAtributo)this.cmbAtributosRed.getSelectedItem());
            
            int valorResalteAux = this.sliResalte.getValue();
            this.visorRed2.setPorcResalte(valorResalteAux);
            
            this.visorRed2.repaint();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtFiltros = new javax.swing.JTextArea();
        lblInformacion = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkEtiquetasRed = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        cmbAtributosRed = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        chkLeyenda = new javax.swing.JCheckBox();
        sliResalte = new javax.swing.JSlider();
        lblPorcResalte = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panContenedor = new javax.swing.JPanel();
        panResultados = new javax.swing.JPanel();
        panResultados2 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        cmbNetKDEComparar = new javax.swing.JComboBox();
        chkNetKDEComparar = new javax.swing.JCheckBox();
        lblInformacionComparar = new javax.swing.JLabel();

        setClosable(true);
        setTitle("NetKDE");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Filters:");

        txtFiltros.setEditable(false);
        txtFiltros.setColumns(20);
        txtFiltros.setRows(4);
        jScrollPane1.setViewportView(txtFiltros);

        chkEtiquetasRed.setText("View network labels");
        chkEtiquetasRed.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEtiquetasRedItemStateChanged(evt);
            }
        });

        jLabel1.setText("Label:");

        jLabel2.setText("<html>* Labels will be displayed for scales in wich a pixel represents a meter or less.</html>");

        chkLeyenda.setText("View legend");
        chkLeyenda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkLeyendaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbAtributosRed, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkLeyenda)
                            .addComponent(chkEtiquetasRed))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkLeyenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkEtiquetasRed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbAtributosRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sliResalte.setMajorTickSpacing(10);
        sliResalte.setMinorTickSpacing(5);
        sliResalte.setPaintLabels(true);
        sliResalte.setPaintTicks(true);
        sliResalte.setToolTipText("");
        sliResalte.setValue(5);
        sliResalte.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliResalteStateChanged(evt);
            }
        });

        lblPorcResalte.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPorcResalte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPorcResalte.setText("%");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Highlight Hotspots");

        panContenedor.setBackground(new java.awt.Color(255, 255, 255));
        panContenedor.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        panResultados.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panResultadosLayout = new javax.swing.GroupLayout(panResultados);
        panResultados.setLayout(panResultadosLayout);
        panResultadosLayout.setHorizontalGroup(
            panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );
        panResultadosLayout.setVerticalGroup(
            panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );

        panContenedor.add(panResultados);

        panResultados2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panResultados2Layout = new javax.swing.GroupLayout(panResultados2);
        panResultados2.setLayout(panResultados2Layout);
        panResultados2Layout.setHorizontalGroup(
            panResultados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );
        panResultados2Layout.setVerticalGroup(
            panResultados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );

        panContenedor.add(panResultados2);

        cmbNetKDEComparar.setMaximumSize(new java.awt.Dimension(28, 20));

        chkNetKDEComparar.setText("Compare with");
        chkNetKDEComparar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkNetKDECompararItemStateChanged(evt);
            }
        });

        lblInformacionComparar.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInformacionComparar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkNetKDEComparar)
                            .addComponent(cmbNetKDEComparar, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkNetKDEComparar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbNetKDEComparar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInformacionComparar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sliResalte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPorcResalte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sliResalte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPorcResalte)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sliResalteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliResalteStateChanged
        int valorResalteAux = this.sliResalte.getValue();
        this.lblPorcResalte.setText(valorResalteAux + "%");
        this.visorRed.setPorcResalte(valorResalteAux);
        this.visorRed.repaint();
        
        if (this.indSegResul) {
            this.visorRed2.setPorcResalte(valorResalteAux);
            this.visorRed2.repaint();
        }
    }//GEN-LAST:event_sliResalteStateChanged

    private void chkNetKDECompararItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkNetKDECompararItemStateChanged
        this.cmbNetKDEComparar.setEnabled(this.chkNetKDEComparar.isSelected());
        this.cmbNetKDEComparar.setSelectedIndex(-1);
        this.indSegResul = this.chkNetKDEComparar.isSelected();
        
        this.panContenedor.remove(this.panResultados2);
        
        if (!this.indSegResul) {
            //Se reajusta el tamaño del primer contenedor
            this.panResultados.setSize(this.panContenedor.getSize());
            this.visorRed.setSize(this.panResultados.getSize());
            this.visorRed.setVisor2(null);
            this.visorRed.repaint();
        }
        this.visorRed.setSize(this.panResultados.getSize());
        this.lblInformacionComparar.setText("");
    }//GEN-LAST:event_chkNetKDECompararItemStateChanged

    private void chkLeyendaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkLeyendaItemStateChanged
        //Se verifica si se debe mostrar o no la leyenda
        this.visorRed.setIndVerLeyenda(this.chkLeyenda.isSelected());
        this.visorRed.repaint();
        if (this.indSegResul) {
            this.visorRed2.setIndVerLeyenda(this.chkLeyenda.isSelected());
            this.visorRed2.repaint();
        }
    }//GEN-LAST:event_chkLeyendaItemStateChanged

    private void chkEtiquetasRedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEtiquetasRedItemStateChanged
        this.cmbAtributosRed.setEnabled(this.chkEtiquetasRed.isSelected());
        if ((this.chkEtiquetasRed.isSelected() && this.cmbAtributosRed.getSelectedIndex() >= 0) || !this.chkEtiquetasRed.isSelected()) {
            this.visorRed.setIndVerAtributos(this.chkEtiquetasRed.isSelected());
            this.visorRed.setRedAtributo((RedAtributo)this.cmbAtributosRed.getSelectedItem());
            this.visorRed.repaint();

            if (this.indSegResul) {
                this.visorRed2.setIndVerAtributos(this.chkEtiquetasRed.isSelected());
                this.visorRed2.setRedAtributo((RedAtributo)this.cmbAtributosRed.getSelectedItem());
                this.visorRed2.repaint();
            }
        }
    }//GEN-LAST:event_chkEtiquetasRedItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkEtiquetasRed;
    private javax.swing.JCheckBox chkLeyenda;
    private javax.swing.JCheckBox chkNetKDEComparar;
    private javax.swing.JComboBox cmbAtributosRed;
    private javax.swing.JComboBox cmbNetKDEComparar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInformacion;
    private javax.swing.JLabel lblInformacionComparar;
    private javax.swing.JLabel lblPorcResalte;
    private javax.swing.JPanel panContenedor;
    private javax.swing.JPanel panResultados;
    private javax.swing.JPanel panResultados2;
    private javax.swing.JSlider sliResalte;
    private javax.swing.JTextArea txtFiltros;
    // End of variables declaration//GEN-END:variables
}
