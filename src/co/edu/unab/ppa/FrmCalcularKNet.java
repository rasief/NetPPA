package co.edu.unab.ppa;

import co.edu.unab.FrmPrincipal;
import co.edu.unab.db.DbEventos;
import co.edu.unab.db.DbKNet;
import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.Evento;
import co.edu.unab.entidad.EventoAtributo;
import co.edu.unab.entidad.KNetResultado;
import co.edu.unab.entidad.Red;
import co.edu.unab.procesos.PrCalculoKNet;
import co.edu.unab.procesos.PrCierreNodosRed;
import co.edu.unab.procesos.PrProyeccionPuntos;
import co.edu.unab.utilidades.Utilidades;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Formulario de Cálculo de la Función K para Redes
 * @author Feisar Moreno
 * @date 11/03/2016
 */
public class FrmCalcularKNet extends JInternalFrame {
    private long idEvento = -1;
    private int indProy = 0;
    private final FrmPrincipal frmPrincipal;
    private PrCalculoKNet prCalculoKNet;
    private PrCierreNodosRed prCierreNodosRed;
    private PrProyeccionPuntos prProyeccionPuntos;
    
    private class AvanceTarea implements Runnable {
        private final int tipoProceso;
        private boolean controlCorrer = true;
        private int cantPuntosAvance = 0;
        
        public AvanceTarea(int tipoProceso) {
            this.tipoProceso = tipoProceso;
        }
        
        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            while (this.controlCorrer) {
                try {
                    Thread.sleep(500);
                    this.mostrarAvance();
                } catch (InterruptedException e) {}
            }
        }
        
        public void setControlCorrer(boolean controlCorrer) {
            this.controlCorrer = controlCorrer;
        }
        
        private void mostrarAvance() {
            String mensajeAvance = "";
            
            this.cantPuntosAvance++;
            this.cantPuntosAvance = this.cantPuntosAvance % 3;
            switch (this.tipoProceso) {
                case 1: //Proyección de puntos
                    if (prProyeccionPuntos != null && prProyeccionPuntos.isIndInicioProyeccion()) {
                        mensajeAvance = "Projecting points (" + prProyeccionPuntos.getPuntoActProceso() + " of " + prProyeccionPuntos.getCantTotalPuntos() + ")";
                    } else {
                        mensajeAvance = "Processing" + "....".substring(0, this.cantPuntosAvance + 1);
                    }
                    break;
                case 2: //Cierre de nodos de red
                    if (prCierreNodosRed != null && prCierreNodosRed.isIndInicioCierre()) {
                        mensajeAvance = "Processing nodes (" + prCierreNodosRed.getLineaActProceso() + " of " + prCierreNodosRed.getCantTotalLineas() + ")";
                    } else {
                        mensajeAvance = "Loading data" + "....".substring(0, this.cantPuntosAvance + 1);
                    }
                    break;
            }
            lblAvance.setText(mensajeAvance);
        }
    }
    
    private class AvanceCalculo implements Runnable {
        private boolean controlCorrer = true;
        private int cantPuntosAvance = 0;
        
        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            while (this.controlCorrer) {
                try {
                    Thread.sleep(500);
                    this.mostrarAvance();
                } catch (InterruptedException e) {}
            }
        }
        
        public void setControlCorrer(boolean controlCorrer) {
            this.controlCorrer = controlCorrer;
        }
        
        private void mostrarAvance() {
            String mensajeAvance;
            
            this.cantPuntosAvance++;
            this.cantPuntosAvance = this.cantPuntosAvance % 3;
            if (prCalculoKNet != null && prCalculoKNet.isIndInicioCalculo()) {
                if (prCalculoKNet.getContAleatoriosCalculo() > 0) {
                    mensajeAvance = "Calculating random values (" + prCalculoKNet.getContAleatoriosCalculo() + " of " + prCalculoKNet.getCantAleatorios() + ")";
                } else if (prCalculoKNet.isIndCalculoReal()) {
                    mensajeAvance = "Calculating function";
                } else if (prCalculoKNet.getContAleatoriosGeneracion() > 0) {
                    mensajeAvance = "Creating random values (" + prCalculoKNet.getContAleatoriosGeneracion()+ " of " + prCalculoKNet.getCantAleatorios() + ")";
                } else {
                    mensajeAvance = "Processing";
                }
            } else {
                mensajeAvance = "Processing";
            }
            mensajeAvance += "....".substring(0, this.cantPuntosAvance + 1);
            lblAvance.setText(mensajeAvance);
        }
    }
    
    /**
     * Creates new form FrmCargarRedes
     * @param frmPrincipal Formulario que contiene a este formulario
     */
    public FrmCalcularKNet(FrmPrincipal frmPrincipal) {
        initComponents();
        
        this.frmPrincipal = frmPrincipal;
        
        //Se limpia el texto de cierre de la red
        this.lblCerradoNodos.setVisible(true);
        this.lblCerradoNodos.setText(" ");
        this.lblCerradoNodos2.setVisible(true);
        this.lblCerradoNodos2.setText(" ");
        
        //Se limpia el texto de evento proyectado
        this.lblProyectado.setVisible(true);
        this.lblProyectado.setText(" ");
        this.lblProyectado2.setVisible(true);
        this.lblProyectado2.setText(" ");
        
        //Se limpia el texto de avance de carga
        this.lblAvance.setVisible(false);
        this.lblAvance.setText(" ");
        
        //Se carga el combo de redes
        DbRedes dbRedes = new DbRedes();
        ArrayList<Red> listaRedes = dbRedes.getListaRedes(false);
        
        this.cmbRedes.removeAllItems();
        for (Red redAux : listaRedes) {
            this.cmbRedes.addItem(redAux);
        }
        this.cmbRedes.setSelectedIndex(-1);
        
        //Se agrega el listener para cambios al comboBox de redes
        this.cmbRedes.addItemListener(
            new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    seleccionarRed();
                }
            }
        );
        
        //Se agrega el listener para cambios al comboBox de eventos
        this.cmbEventos.addItemListener(
            new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    seleccionarEvento();
                }
            }
        );
    }
    
    public FrmPrincipal getFrmPrincipal() {
        return this.frmPrincipal;
    }
    
    /**
     * Método que valida si se han seleccionado todos los campos requeridos para proyectar los puntos
     * @return <code>true</code> si se seleccionaron todos los campos requeridos,
     * de lo contrario <code>false</code>.
     */
    private boolean validarCamposProyeccion() {
        //Se valida que se hayan diligenciado todos los campos
        if (this.cmbEventos.getSelectedIndex() == -1) {
            this.tpaKNet.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select an event.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbEventos.requestFocusInWindow();
            return false;
        }
        
        if (this.txtDistProy.getText().equals("")) {
            this.tpaKNet.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the maximum projection distance.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistProy.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtDistProy.getText());
            } catch (NumberFormatException e) {
                this.tpaKNet.setSelectedIndex(1);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The maximum projection distance must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtDistProy.requestFocusInWindow();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Método que valida si se han seleccionado todos los campos requeridos para cerrar los nodos de la red
     * @return <code>true</code> si se seleccionaron todos los campos requeridos,
     * de lo contrario <code>false</code>.
     */
    private boolean validarCamposCierreNodos() {
        //Se valida que se hayan diligenciado todos los campos
        if (this.cmbRedes.getSelectedIndex() == -1) {
            this.tpaKNet.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a network.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbRedes.requestFocusInWindow();
            return false;
        }
        
        if (this.txtDistCierre.getText().equals("")) {
            this.tpaKNet.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the maxumum node closure distance.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistCierre.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtDistCierre.getText());
            } catch (NumberFormatException e) {
                this.tpaKNet.setSelectedIndex(1);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The maximum closure distance must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtDistCierre.requestFocusInWindow();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Método que valida si se han seleccionado todos los campos requeridos para calcular la función K para redes
     * @return <code>true</code> si se seleccionaron todos los campos requeridos,
     * de lo contrario <code>false</code>.
     */
    private boolean validarCamposCalculo() {
        //Se validan los valores puestos en los filtros
        String textoAux = Utilidades.validarFiltros(this.tblKNetFiltros);
        if (textoAux != null) {
            JOptionPane.showMessageDialog(this.frmPrincipal, textoAux, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        //Se valida que se hayan diligenciado todos los campos
        if (this.cmbRedes.getSelectedIndex() == -1) {
            this.tpaKNet.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a network.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbRedes.requestFocusInWindow();
            return false;
        }
        
        if (this.cmbEventos.getSelectedIndex() == -1) {
            this.tpaKNet.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select an event.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbEventos.requestFocusInWindow();
            return false;
        }
        
        if (this.indProy != 1) {
            this.tpaKNet.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "Network projection missing.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistProy.requestFocusInWindow();
            return false;
        }
        
        if (this.txtDistanciaIniKNET.getText().equals("")) {
            this.tpaKNet.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the initial network distance.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistanciaIniKNET.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtDistanciaIniKNET.getText());
            } catch (NumberFormatException e) {
                this.tpaKNet.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this, "The initial network distance must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtDistanciaIniKNET.requestFocusInWindow();
                return false;
            }
        }
        
        if (this.txtDistanciaFinKNET.getText().equals("")) {
            this.tpaKNet.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the final network distance.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistanciaFinKNET.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtDistanciaFinKNET.getText());
            } catch (NumberFormatException e) {
                this.tpaKNet.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The final network distance must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtDistanciaFinKNET.requestFocusInWindow();
                return false;
            }
        }
        
        if (this.txtIncrementoKNET.getText().equals("")) {
            this.tpaKNet.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the increment.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtIncrementoKNET.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtIncrementoKNET.getText());
            } catch (NumberFormatException e) {
                this.tpaKNet.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The increment must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtIncrementoKNET.requestFocusInWindow();
                return false;
            }
        }
        
        if (this.txtCantAleatorios.getText().equals("")) {
            this.tpaKNet.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the number of random groups.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtCantAleatorios.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtCantAleatorios.getText());
            } catch (NumberFormatException e) {
                this.tpaKNet.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The number of random groups must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtCantAleatorios.requestFocusInWindow();
                return false;
            }
        }
        
        return true;
    }
    
    public void habilitarComponentes(boolean habilitar) {
        this.cmbRedes.setEnabled(habilitar);
        this.cmbEventos.setEnabled(habilitar);
        this.txtDistProy.setEnabled(habilitar);
        this.txtDistCierre.setEnabled(habilitar);
        this.btnProyectar.setEnabled(habilitar);
        this.btnCerrarNodos.setEnabled(habilitar);
        this.tblKNetFiltros.setEnabled(habilitar);
        this.tblKNetResultados.setEnabled(habilitar);
        if (!habilitar) {
            this.txtDistanciaIniKNET.setEnabled(false);
            this.txtDistanciaFinKNET.setEnabled(false);
            this.txtIncrementoKNET.setEnabled(false);
            this.txtCantAleatorios.setEnabled(false);
            this.btnCalcular.setEnabled(false);
        } else if (this.cmbEventos.getSelectedIndex() != -1) {
            //Se valida si el evento seleccionado tiene una proyección realizada
            Evento eventoAux = (Evento)this.cmbEventos.getSelectedItem();
            DbEventos dbEventos = new DbEventos();
            eventoAux = dbEventos.getEvento(false, eventoAux.getIdEvento());
            if (eventoAux.getIndProy() == 1) {
                this.txtDistanciaIniKNET.setEnabled(true);
                this.txtDistanciaFinKNET.setEnabled(true);
                this.txtIncrementoKNET.setEnabled(true);
                this.txtCantAleatorios.setEnabled(true);
                this.btnCalcular.setEnabled(true);
            }
        }
    }
    
    private void cargarTablaKNetFiltros() {
        //Nombres de las columnas
        String[] nombCols = new String[3];
        nombCols[0] = "Attibute";
        nombCols[1] = "Type/Format";
        nombCols[2] = "Value/Filter";
        
        DbEventos dbEventos = new DbEventos();
        
        //Se obtienen los datos del evento
        Evento eventoAux = dbEventos.getEvento(false, this.idEvento);
        
        //Se obtienen los atributos de fecha y hora
        EventoAtributo eventoAtributoFecha = dbEventos.getEventoAtributo(false, this.idEvento, eventoAux.getIdAtributoFecha());
        EventoAtributo eventoAtributoHora = dbEventos.getEventoAtributo(false, this.idEvento, eventoAux.getIdAtributoHora());
        
        //Se obtienen los registros de atributos del evento
        ArrayList<EventoAtributo> listaEventosAtributos = dbEventos.getListaEventosAtributos(false, this.idEvento);
        String [][] cuerpoTabla = new String[listaEventosAtributos.size()][0];
        for (int i = 0; i < listaEventosAtributos.size(); i++) {
            EventoAtributo eventoAtributoAux = listaEventosAtributos.get(i);
            
            String[] registroAux = new String[3];
            registroAux[0] = eventoAtributoAux.getNombreAtributo();
            
            if ((eventoAtributoFecha != null && eventoAtributoAux.getNombreAtributo().equalsIgnoreCase(eventoAtributoFecha.getNombreAtributo())) ||
                    (eventoAtributoHora != null && eventoAtributoAux.getNombreAtributo().equalsIgnoreCase(eventoAtributoHora.getNombreAtributo()))) {
                String tipoFechaAux = "";
                if (eventoAtributoFecha != null && eventoAtributoAux.getNombreAtributo().equalsIgnoreCase(eventoAtributoFecha.getNombreAtributo())) {
                    tipoFechaAux = "dd/mm/yyyy";
                }
                String tipoHoraAux = "";
                if (eventoAtributoHora != null && eventoAtributoAux.getNombreAtributo().equalsIgnoreCase(eventoAtributoHora.getNombreAtributo())) {
                    tipoHoraAux = "hh:mm";
                }
                
                if (!tipoFechaAux.equals("") && !tipoHoraAux.equals("")) {
                    registroAux[1] = tipoFechaAux + " or " + tipoHoraAux;
                } else if (!tipoFechaAux.equals("")) {
                    registroAux[1] = tipoFechaAux;
                } else {
                    registroAux[1] = tipoHoraAux;
                }
            } else {
                registroAux[1] = eventoAtributoAux.getTipoAtributo();
            }
            registroAux[2] = "";
            
            cuerpoTabla[i] = registroAux;
        }
        
        DefaultTableModel tablaAtributos = new DefaultTableModel(cuerpoTabla, nombCols) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 2;
            }
        };
        
        this.tblKNetFiltros.setModel(tablaAtributos);
        TableColumnModel columnas = this.tblKNetFiltros.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(90);
        columnas.getColumn(1).setPreferredWidth(115);
        columnas.getColumn(2).setPreferredWidth(190);
    }
    
    private void cargarTablaKNetResultados() {
        //Nombres de las columnas
        String[] nombCols = new String[7];
        nombCols[0] = "Initial dist.";
        nombCols[1] = "Final dist.";
        nombCols[2] = "Increment";
        nombCols[3] = "Filters";
        nombCols[4] = "Date/Time";
        nombCols[5] = "Delete";
        nombCols[6] = "View";
        
        //Se obtienen los registros de resultados
        DbKNet dbKNet = new DbKNet();
        ArrayList<KNetResultado> listaKNetResultados = dbKNet.getListaKNetResultados(false, this.idEvento);
        long[] arrIdKNet = new long[listaKNetResultados.size()];
        String [][] cuerpoTabla = new String[listaKNetResultados.size()][0];
        int cont = 0;
        for (int i = listaKNetResultados.size() - 1; i >= 0; i--) {
            KNetResultado kNetResultadoAux = listaKNetResultados.get(i);
            
            arrIdKNet[cont] = kNetResultadoAux.getIdKNet();
            
            String[] registroAux = new String[7];
            registroAux[0] = "" + kNetResultadoAux.getDistanciaIni();
            registroAux[1] = "" + kNetResultadoAux.getDistanciaFin();
            registroAux[2] = "" + kNetResultadoAux.getIncrementoDist();
            registroAux[3] = kNetResultadoAux.getFiltrosResultado().equals("") ? "(None)" : kNetResultadoAux.getFiltrosResultado();
            registroAux[4] = kNetResultadoAux.getFechaResultado();
            registroAux[5] = " ... ";
            registroAux[6] = " ... ";
            
            cuerpoTabla[cont] = registroAux;
            cont++;
        }
        
        DefaultTableModel tablaRedes = new DefaultTableModel(cuerpoTabla, nombCols) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex >= 5;
            }
        };
        
        this.tblKNetResultados.setModel(tablaRedes);
        TableColumnModel columnas = this.tblKNetResultados.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(65);
        columnas.getColumn(1).setPreferredWidth(65);
        columnas.getColumn(2).setPreferredWidth(65);
        columnas.getColumn(3).setPreferredWidth(255);
        columnas.getColumn(4).setPreferredWidth(135);
        columnas.getColumn(5).setPreferredWidth(45);
        columnas.getColumn(6).setPreferredWidth(45);
        
        this.tblKNetResultados.getColumn("Delete").setCellRenderer(new ButtonRendererKNET());
        this.tblKNetResultados.getColumn("View").setCellRenderer(new ButtonRendererKNET());
        this.tblKNetResultados.getColumn("Delete").setCellEditor(
            new ButtonEditorKNET(new JCheckBox(), this.tblKNetResultados, this, arrIdKNet, 'B')
        );
        this.tblKNetResultados.getColumn("View").setCellEditor(
            new ButtonEditorKNET(new JCheckBox(), this.tblKNetResultados, this, arrIdKNet, 'V')
        );
    }
    
    private void seleccionarRed() {
        this.idEvento = -1;
        
        //Se verifica que se haya seleccionado una red
        if (this.cmbRedes.getSelectedIndex() != -1) {
            //Se verifica si ya se efectuó el cierre de nodos sobre la red
            Red red = (Red)cmbRedes.getSelectedItem();
            this.lblDescRed.setText(red.getDescRed());
            
            DbRedes dbRedes = new DbRedes();
            red = dbRedes.getRed(false, red.getIdRed());
            
            int indCierreNodos = red.getIndCierreNodos();
            if (indCierreNodos == 1) {
                //Red cerrada
                this.lblCerradoNodos.setForeground(Color.BLACK);
                this.lblCerradoNodos.setText("Node closure: " + red.getDistCierreNodos() + " m");
                this.lblCerradoNodos2.setForeground(Color.BLACK);
                this.lblCerradoNodos2.setText("Node closure: " + red.getDistCierreNodos() + " m");
                this.txtDistCierre.setText(red.getDistCierreNodos() + "");
            } else {
                //Red no cerrada
                this.lblCerradoNodos.setForeground(Color.RED);
                this.lblCerradoNodos.setText("Nodes not closed");
                this.lblCerradoNodos2.setForeground(Color.RED);
                this.lblCerradoNodos2.setText("Nodes not closed");
                this.txtDistCierre.setText("");
            }
            this.lblCerradoNodos.setVisible(true);
            this.lblCerradoNodos2.setVisible(true);
            
            //Se llena el combo de eventos de la red
            DbEventos dbEventos = new DbEventos();
            ArrayList<Evento> listaEventos = dbEventos.getListaEventos(false, red.getIdRed());
            
            this.cmbEventos.removeAllItems();
            for (Evento eventoAux : listaEventos) {
                this.cmbEventos.addItem(eventoAux);
            }
            this.cmbEventos.setSelectedIndex(-1);
        } else {
            this.lblDescRed.setText("-");
            this.lblCerradoNodos.setVisible(false);
            this.lblCerradoNodos2.setVisible(false);
        }
    }
    
    private void seleccionarEvento() {
        //Se verifica que se haya seleccionado un evento
        if (this.cmbEventos.getSelectedIndex() != -1) {
            //Se verifica si el evento ya fue proyectado
            Evento evento = (Evento)cmbEventos.getSelectedItem();
            this.lblDescEvento.setText(evento.getDescEvento());
            this.idEvento = evento.getIdEvento();
            
            //Se llena la tabla de atributos para el evento
            this.cargarTablaKNetFiltros();
            
            //Se llena la tabla de resultados para el evento
            this.cargarTablaKNetResultados();
            
            DbEventos dbEventos = new DbEventos();
            Evento eventoAux = dbEventos.getEvento(false, evento.getIdEvento());
            this.indProy = eventoAux.getIndProy();
            if (eventoAux.getIndProy() == 1) {
                //Evento con proyección sobre la red
                this.lblProyectado.setForeground(Color.BLACK);
                this.lblProyectado.setText("Projection: " + eventoAux.getDistProy() + " m");
                this.lblProyectado2.setForeground(Color.BLACK);
                this.lblProyectado2.setText("Projection: " + eventoAux.getDistProy() + " m");
                this.txtDistProy.setText(eventoAux.getDistProy() + "");
            } else {
                //Evento sin proyeccción sobre la red
                this.lblProyectado.setForeground(Color.RED);
                this.lblProyectado.setText("Not projected");
                this.lblProyectado2.setForeground(Color.RED);
                this.lblProyectado2.setText("Not projected");
                this.txtDistProy.setText("");
            }
            this.lblProyectado.setVisible(true);
            this.lblProyectado2.setVisible(true);
        } else {
            this.lblDescEvento.setText("-");
            this.lblProyectado.setVisible(false);
            this.lblProyectado2.setVisible(false);
            this.idEvento = -1;
            
            this.tblKNetFiltros.setModel(new DefaultTableModel(new String[0][0], new String[0]));
            this.tblKNetResultados.setModel(new DefaultTableModel(new String[0][0], new String[0]));
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

        jLabel1 = new javax.swing.JLabel();
        lblAvance = new javax.swing.JLabel();
        btnCalcular = new javax.swing.JButton();
        tpaKNet = new javax.swing.JTabbedPane();
        panCalculoKNet = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDistanciaIniKNET = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCantAleatorios = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDistanciaFinKNET = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtIncrementoKNET = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblKNetFiltros = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        panCierreProy = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnCerrarNodos = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtDistCierre = new javax.swing.JTextField();
        lblCerradoNodos2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblDescRed = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblProyectado2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDistProy = new javax.swing.JTextField();
        btnProyectar = new javax.swing.JButton();
        lblDescEvento = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        panResultados = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKNetResultados = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        cmbRedes = new javax.swing.JComboBox();
        lblCerradoNodos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbEventos = new javax.swing.JComboBox();
        lblProyectado = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Network K Function");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NETWORK K FUNCTION");

        lblAvance.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAvance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAvance.setText(".");

        btnCalcular.setText("Calculate");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        tpaKNet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel2.setText("Initial network distance (meters)");

        jLabel3.setText("Number of random groups");

        txtDistanciaIniKNET.setToolTipText("");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Calculate new value");

        txtCantAleatorios.setText("100");
        txtCantAleatorios.setToolTipText("");

        jLabel6.setText("Final network distance (meters)");

        txtDistanciaFinKNET.setToolTipText("");

        jLabel9.setText("Increment (meters)");

        txtIncrementoKNET.setToolTipText("");

        tblKNetFiltros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblKNetFiltros.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblKNetFiltros);

        jLabel14.setText("<html>To include more than one value use the comma (,) as separator (e.g. 3.5,4,4.5).<br /><br />To define a range between two values use [#] as separator (e.g. 6:00#18:00).<br /><br />To get the values not equal to a base value, list, or range use [!] before the value, list, or range (e.g. !01/01/2000).</html>");
        jLabel14.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtDistanciaIniKNET, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtDistanciaFinKNET, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(txtIncrementoKNET, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(73, 73, 73)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtCantAleatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDistanciaIniKNET, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDistanciaFinKNET, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIncrementoKNET, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantAleatorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panCalculoKNetLayout = new javax.swing.GroupLayout(panCalculoKNet);
        panCalculoKNet.setLayout(panCalculoKNetLayout);
        panCalculoKNetLayout.setHorizontalGroup(
            panCalculoKNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCalculoKNetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panCalculoKNetLayout.setVerticalGroup(
            panCalculoKNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCalculoKNetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpaKNet.addTab("Calculation", panCalculoKNet);

        btnCerrarNodos.setText("Close nodes");
        btnCerrarNodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarNodosActionPerformed(evt);
            }
        });

        jLabel11.setText("Maximum node closure distance (meters)");

        txtDistCierre.setToolTipText("");

        lblCerradoNodos2.setText(".");

        jLabel12.setText("Network");

        lblDescRed.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescRed.setText("-");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDistCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(btnCerrarNodos)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCerradoNodos2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(lblDescRed))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDescRed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDistCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCerrarNodos)
                    .addComponent(lblCerradoNodos2))
                .addContainerGap())
        );

        lblProyectado2.setText(".");

        jLabel5.setText("Maximum projection distance (meters)");

        txtDistProy.setToolTipText("");

        btnProyectar.setText("Project points");
        btnProyectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProyectarActionPerformed(evt);
            }
        });

        lblDescEvento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescEvento.setText("-");

        jLabel13.setText("Event (points)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDistProy, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnProyectar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProyectado2, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(lblDescEvento))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDescEvento)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDistProy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProyectar)
                    .addComponent(lblProyectado2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panCierreProyLayout = new javax.swing.GroupLayout(panCierreProy);
        panCierreProy.setLayout(panCierreProyLayout);
        panCierreProyLayout.setHorizontalGroup(
            panCierreProyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCierreProyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panCierreProyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panCierreProyLayout.setVerticalGroup(
            panCierreProyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCierreProyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        tpaKNet.addTab("Network Closure and Points Projection", panCierreProy);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Calculated values");

        tblKNetResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblKNetResultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblKNetResultados);

        javax.swing.GroupLayout panResultadosLayout = new javax.swing.GroupLayout(panResultados);
        panResultados.setLayout(panResultadosLayout);
        panResultadosLayout.setHorizontalGroup(
            panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panResultadosLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel7)
                        .addGap(578, 578, 578))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        panResultadosLayout.setVerticalGroup(
            panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpaKNet.addTab("Results", panResultados);

        jLabel10.setText("Network");

        lblCerradoNodos.setText(".");

        jLabel4.setText("Event (points)");

        lblProyectado.setText(".");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbRedes, javax.swing.GroupLayout.Alignment.LEADING, 0, 323, Short.MAX_VALUE)
                    .addComponent(lblCerradoNodos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbEventos, 0, 323, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addComponent(lblProyectado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbRedes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbEventos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCerradoNodos)
                    .addComponent(lblProyectado))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnCalcular)
                    .addComponent(tpaKNet)
                    .addComponent(lblAvance, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpaKNet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalcular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAvance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        //Se valida que se hayan diligenciado todos los campos
        if (this.validarCamposCalculo()) {
            this.tpaKNet.setSelectedIndex(0);
            //Se inhabilitan los componentes
            this.habilitarComponentes(false);
            
            //Se inicia el hilo que muestra el avance del proceso
            final AvanceCalculo tareaCalculo = new AvanceCalculo();
            Thread hiloProcesar = new Thread(tareaCalculo, "Procesando");
            hiloProcesar.start();
            
            Runnable procesoCarga = new Runnable() {
                @Override
                public void run() {
                    boolean resultado = true;
                    try {
                        lblAvance.setVisible(true);
                        Evento evento = (Evento)cmbEventos.getSelectedItem();
                        String textoAux = txtDistanciaIniKNET.getText();
                        double distanciaIniKNet = Double.parseDouble(textoAux);
                        textoAux = txtDistanciaFinKNET.getText();
                        double distanciaFinKNet = Double.parseDouble(textoAux);
                        textoAux = txtIncrementoKNET.getText();
                        double incrementoKNet = Double.parseDouble(textoAux);
                        textoAux = txtCantAleatorios.getText();
                        int cantAleatorios = Integer.parseInt(textoAux);
                        LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros = Utilidades.obtenerFiltros(tblKNetFiltros);
                        
                        //Se llama a la clase que realiza el cálculo
                        prCalculoKNet = new PrCalculoKNet(evento, mapaFiltros, distanciaIniKNet, distanciaFinKNet, incrementoKNet, cantAleatorios);
                        
                        //Se realiza la carga
                        long idKNet = prCalculoKNet.calcularKNet();
                        resultado = (idKNet > 0);
                    } finally {
                        tareaCalculo.setControlCorrer(false);
                        lblAvance.setVisible(false);
                        habilitarComponentes(true);
                    }
                    
                    if (resultado) {
                        //Si el resultado fue correcto, se actualiza la tabla de redes
                        cargarTablaKNetResultados();
                        
                        tpaKNet.setSelectedIndex(2);
                        JOptionPane.showMessageDialog(frmPrincipal, "Network K function calculated successfully.", "Network K Function", JOptionPane.INFORMATION_MESSAGE);
                        
                        //Se limpia el formulario
                        cargarTablaKNetFiltros();
                        txtDistanciaIniKNET.setText("");
                        txtDistanciaFinKNET.setText("");
                        txtIncrementoKNET.setText("");
                        txtCantAleatorios.setText("100");
                        habilitarComponentes(true);
                    } else {
                        JOptionPane.showMessageDialog(frmPrincipal, "Calculation error, please check if the information is valid.", "Network K Function", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            Thread hiloCarga = new Thread(procesoCarga, "procesarCarga");
            hiloCarga.start();
        }
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnProyectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProyectarActionPerformed
        //Se valida que se hayan diligenciado todos los campos
        if (this.validarCamposProyeccion()) {
            //Se inhabilitan los componentes
            this.habilitarComponentes(false);
            
            //Se inicia el hilo que muestra el avance del proceso
            final AvanceTarea tareaProyeccion = new AvanceTarea(1);
            Thread hiloProcesar = new Thread(tareaProyeccion, "Procesando");
            hiloProcesar.start();
            
            Runnable procesoCarga = new Runnable() {
                @Override
                public void run() {
                    boolean resultado = true;
                    try {
                        lblAvance.setVisible(true);
                        Evento evento = (Evento)cmbEventos.getSelectedItem();
                        double distProy = Double.parseDouble(txtDistProy.getText());
                        
                        //Se llama a la clase que realiza la proyección de los puntos
                        prProyeccionPuntos = new PrProyeccionPuntos(evento, distProy);
                        
                        //Se realiza la proyección
                        resultado = prProyeccionPuntos.realizarProyeccion();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    } finally {
                        tareaProyeccion.setControlCorrer(false);
                        lblAvance.setVisible(false);
                        habilitarComponentes(true);
                    }
                    
                    if (resultado) {
                        seleccionarEvento();
                        JOptionPane.showMessageDialog(frmPrincipal, "Points projected successfully.", "Points Projection", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frmPrincipal, "Internal error during the projection.", "Points Projection", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            Thread hiloCarga = new Thread(procesoCarga, "procesarCarga");
            hiloCarga.start();
        }
    }//GEN-LAST:event_btnProyectarActionPerformed

    private void btnCerrarNodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarNodosActionPerformed
        //Se valida que se hayan diligenciado todos los campos
        if (this.validarCamposCierreNodos()) {
            //Se inhabilitan los componentes
            this.habilitarComponentes(false);
            
            //Se inicia el hilo que muestra el avance del proceso
            final AvanceTarea tareaCierre = new AvanceTarea(2);
            Thread hiloProcesar = new Thread(tareaCierre, "Procesando");
            hiloProcesar.start();
            
            Runnable procesoCarga = new Runnable() {
                @Override
                public void run() {
                    int cantCierres = -2;
                    try {
                        lblAvance.setVisible(true);
                        Red red = (Red)cmbRedes.getSelectedItem();
                        double distCierreNodos = Double.parseDouble(txtDistCierre.getText());
                        
                        //Se llama a la clase que realiza el cierre de la red
                        prCierreNodosRed = new PrCierreNodosRed(red, distCierreNodos);
                        
                        //Se realiza el cierre de la red
                        cantCierres = prCierreNodosRed.realizarCierreNodos();
                    } finally {
                        tareaCierre.setControlCorrer(false);
                        lblAvance.setVisible(false);
                        habilitarComponentes(true);
                    }
                    
                    if (cantCierres >= 0) {
                        int indiceAux = cmbEventos.getSelectedIndex();
                        seleccionarRed();
                        cmbEventos.setSelectedIndex(indiceAux);
                        seleccionarEvento();
                        JOptionPane.showMessageDialog(frmPrincipal, cantCierres + " nodes closed successfully.", "Network Nodes Closure", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frmPrincipal, "Internal error during the node closure.", "Network Nodes Closure", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            Thread hiloCarga = new Thread(procesoCarga, "procesarCarga");
            hiloCarga.start();
        }
    }//GEN-LAST:event_btnCerrarNodosActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCerrarNodos;
    private javax.swing.JButton btnProyectar;
    private javax.swing.JComboBox cmbEventos;
    private javax.swing.JComboBox cmbRedes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAvance;
    private javax.swing.JLabel lblCerradoNodos;
    private javax.swing.JLabel lblCerradoNodos2;
    private javax.swing.JLabel lblDescEvento;
    private javax.swing.JLabel lblDescRed;
    private javax.swing.JLabel lblProyectado;
    private javax.swing.JLabel lblProyectado2;
    private javax.swing.JPanel panCalculoKNet;
    private javax.swing.JPanel panCierreProy;
    private javax.swing.JPanel panResultados;
    private javax.swing.JTable tblKNetFiltros;
    private javax.swing.JTable tblKNetResultados;
    private javax.swing.JTabbedPane tpaKNet;
    private javax.swing.JTextField txtCantAleatorios;
    private javax.swing.JTextField txtDistCierre;
    private javax.swing.JTextField txtDistProy;
    private javax.swing.JTextField txtDistanciaFinKNET;
    private javax.swing.JTextField txtDistanciaIniKNET;
    private javax.swing.JTextField txtIncrementoKNET;
    // End of variables declaration//GEN-END:variables
    
    private class ButtonRendererKNET extends JButton implements TableCellRenderer {
        public ButtonRendererKNET() {
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
    
    private class ButtonEditorKNET extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final char tipoAccion;
        private final JTable table;
        private final FrmCalcularKNet frmCalcularKNET;
        private final long[] arrIdKNet;
        
        public ButtonEditorKNET(JCheckBox checkBox, JTable table, FrmCalcularKNet frmCalcularKNET, long[] arrIdKNet, char tipoAccion) {
            super(checkBox);
            this.table = table;
            this.frmCalcularKNET = frmCalcularKNET;
            this.arrIdKNet = arrIdKNet;
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
                        FrmMostrarKNet frmMostrarKNET = new FrmMostrarKNet(this.arrIdKNet[this.table.getSelectedRow()], this.frmCalcularKNET);
                        this.frmCalcularKNET.getFrmPrincipal().getPanPrincipal().add(frmMostrarKNET, 0);
                        this.frmCalcularKNET.habilitarComponentes(false);
                        this.frmCalcularKNET.setVisible(false);
                        frmMostrarKNET.setVisible(true);
                        break;
                    case 'B': //Borrar
                        int seleccionAux = JOptionPane.showConfirmDialog(frmPrincipal, "Do you want to delete the network K function result?", "Deleting Results", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (seleccionAux == JOptionPane.YES_OPTION) {
                            habilitarComponentes(false);
                            DbKNet dbKNet = new DbKNet();
                            int resultadoBorrar = dbKNet.borrarKNetResultado(false, this.arrIdKNet[this.table.getSelectedRow()]);
                            switch (resultadoBorrar) {
                                case 1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Result deleted successfully.", "Deleting Results", JOptionPane.INFORMATION_MESSAGE);
                                    cargarTablaKNetResultados();
                                    break;
                                case -1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Internal error during the deleting process.", "Deleting Results", JOptionPane.ERROR_MESSAGE);
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Error code (" + resultadoBorrar + ") during the deleting process.", "Deleting Results", JOptionPane.ERROR_MESSAGE);
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
