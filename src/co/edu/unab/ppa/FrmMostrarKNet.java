package co.edu.unab.ppa;

import co.edu.unab.db.DbKNet;
import co.edu.unab.entidad.KNetResultado;
import co.edu.unab.entidad.KNetValor;
import java.awt.Color;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * Formulario para mostrar resultados de la Función K para Redes
 * @author Feisar Moreno
 * @date 08/05/2016
 */
public class FrmMostrarKNet extends JInternalFrame {
    private final long idKNet;
    private final FrmCalcularKNet frmCalcularKNet;
    private KNetResultado kNetResultado;
    private ArrayList<KNetValor> listaKNetValores;
    
    /**
     * Constructor para el formulario FrmMostrarKNet
     * @param idKNet Identificador del resultado a mostrar
     * @param frmCalcularKNet Formulario del cálculo de función K para redes
     */
    public FrmMostrarKNet(long idKNet, FrmCalcularKNet frmCalcularKNet) {
        initComponents();
        
        this.idKNet = idKNet;
        this.frmCalcularKNet = frmCalcularKNet;
        
        this.mostrarResultados();
    }
    
    @Override
    public void dispose() {
        this.frmCalcularKNet.habilitarComponentes(true);
        this.frmCalcularKNet.setVisible(true);
        super.dispose();
    }
    
    private void mostrarResultados() {
        //Se cargan los valores de resultado a mostrar
        DbKNet dbKNet = new DbKNet();
        this.kNetResultado = dbKNet.getKNetResultado(false, this.idKNet);
        this.listaKNetValores = dbKNet.getListaKNetValores(false, this.idKNet);
        
        //Se cargan los datos del resultado
        String textoAux =
                "<html><b>Event:</b> " + this.kNetResultado.getEvento().getDescEvento() + "<br /><br />" +
                "<b>Initial distance (meters):</b> " + this.kNetResultado.getDistanciaIni() + "<br />" +
                "<b>Final distance (meters):</b> " + this.kNetResultado.getDistanciaFin() + "<br />" +
                "<b>Increment (meters):</b> " + this.kNetResultado.getIncrementoDist() + "<br />" +
                "<b>Random groups:</b> " + this.kNetResultado.getCantAleatorios() + "<br />" +
                "<b>Number of points:</b> " + this.kNetResultado.getCantPuntos() + "</html>";
        this.lblInformacion.setText(textoAux);
        
        //Se cargan los filtros del resultado
        textoAux = this.kNetResultado.getFiltrosResultado().replaceAll("; ", "\n");
        textoAux = textoAux.equals("") ? "(None)" : textoAux;
        this.txtFiltros.setText(textoAux.replaceAll("; ", "\n"));
        
        //Se genera la gráfica
        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setSize(450, 350);
        fxPanel.setBackground(Color.WHITE);
        this.panGrafico.add(fxPanel);
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                iniciarFX(fxPanel);
            }
        });
        
        //Se carga la tabla de resultados
        this.cargarTablaKNetResultados();
    }
    
    private void iniciarFX(JFXPanel fxPanel) {
        Scene scene = crearGrafico();
        fxPanel.setScene(scene);
    }

    private Scene crearGrafico() {
        //Se definen los ejes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Distance");
        
        //Se crea el gráfico
        final LineChart<Number,Number> chartLineal = new LineChart<>(xAxis,yAxis);
        
        //Se agregan los datos de la función K
        XYChart.Series serieReal = new XYChart.Series();
        serieReal.setName("K value");
        XYChart.Series serieMin = new XYChart.Series();
        serieMin.setName("Lower limit");
        XYChart.Series serieMax = new XYChart.Series();
        serieMax.setName("Upper limit");
        
        for (KNetValor kNetValor : listaKNetValores) {
            serieReal.getData().add(new XYChart.Data(kNetValor.getDistanciaKNet(), kNetValor.getValor()));
            serieMin.getData().add(new XYChart.Data(kNetValor.getDistanciaKNet(), kNetValor.getLimiteMin()));
            serieMax.getData().add(new XYChart.Data(kNetValor.getDistanciaKNet(), kNetValor.getLimiteMax()));
        }
        
        chartLineal.setBackground(Background.EMPTY);
        Scene scene = new Scene(chartLineal, 450, 350);
        chartLineal.getData().addAll(serieReal, serieMin, serieMax);

        return scene;
    }
    
    private void cargarTablaKNetResultados() {
        //Nombres de las columnas
        String[] nombCols = new String[4];
        nombCols[0] = "Distance (meters)";
        nombCols[1] = "Lower limit";
        nombCols[2] = "Upper limit";
        nombCols[3] = "K value";
        
        //Se cargan los registros de resultados
        String [][] cuerpoTabla = new String[this.listaKNetValores.size()][0];
        for (int i = 0; i < this.listaKNetValores.size(); i++) {
            KNetValor kNetValorAux = listaKNetValores.get(i);
            
            String[] registroAux = new String[4];
            registroAux[0] = "" + kNetValorAux.getDistanciaKNet();
            registroAux[1] = "" + Math.round(kNetValorAux.getLimiteMin() * 10000000000.0) / 10000000000.0;
            registroAux[2] = "" + Math.round(kNetValorAux.getLimiteMax() * 10000000000.0) / 10000000000.0;
            registroAux[3] = "" + Math.round(kNetValorAux.getValor() * 10000000000.0) / 10000000000.0;
            
            cuerpoTabla[i] = registroAux;
        }
        
        DefaultTableModel tablaResultados = new DefaultTableModel(cuerpoTabla, nombCols) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        
        this.tblKNetResultados.setModel(tablaResultados);
        TableColumnModel columnas = this.tblKNetResultados.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(100);
        columnas.getColumn(1).setPreferredWidth(108);
        columnas.getColumn(2).setPreferredWidth(108);
        columnas.getColumn(3).setPreferredWidth(108);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        tblKNetResultados = new javax.swing.JTable();
        btnVolver = new javax.swing.JButton();
        panGrafico = new javax.swing.JPanel();
        lblInformacion = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Network K Function");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Filters:");

        txtFiltros.setEditable(false);
        txtFiltros.setColumns(20);
        txtFiltros.setRows(6);
        jScrollPane1.setViewportView(txtFiltros);

        tblKNetResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblKNetResultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(tblKNetResultados);

        btnVolver.setText("<< Back");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        panGrafico.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panGraficoLayout = new javax.swing.GroupLayout(panGrafico);
        panGrafico.setLayout(panGraficoLayout);
        panGraficoLayout.setHorizontalGroup(
            panGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panGraficoLayout.setVerticalGroup(
            panGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVolver)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(panGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVolver)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnVolverActionPerformed
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblInformacion;
    private javax.swing.JPanel panGrafico;
    private javax.swing.JTable tblKNetResultados;
    private javax.swing.JTextArea txtFiltros;
    // End of variables declaration//GEN-END:variables
}
