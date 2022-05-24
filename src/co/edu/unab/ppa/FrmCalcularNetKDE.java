package co.edu.unab.ppa;

import co.edu.unab.FrmPrincipal;
import co.edu.unab.db.DbEventos;
import co.edu.unab.db.DbFuncionesNucleo;
import co.edu.unab.db.DbNetKDE;
import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.Evento;
import co.edu.unab.entidad.EventoAtributo;
import co.edu.unab.entidad.FuncionNucleo;
import co.edu.unab.entidad.NetKDEResultado;
import co.edu.unab.entidad.Red;
import co.edu.unab.procesos.PrCalculoNetKDE;
import co.edu.unab.procesos.PrCierreNodosRed;
import co.edu.unab.procesos.PrProyeccionPuntos;
import co.edu.unab.utilidades.Utilidades;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
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
 * Formulario de Cálculo de NetKDE
 * @author Feisar Moreno
 * @date 09/06/2016
 */
public class FrmCalcularNetKDE extends JInternalFrame {
    private long idRed;
    private long idEvento = -1;
    private int indProy = 0;
    private final FrmPrincipal frmPrincipal;
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
                        mensajeAvance = "Procesando" + "....".substring(0, this.cantPuntosAvance + 1);
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
            mensajeAvance = "Processing" + "....".substring(0, this.cantPuntosAvance + 1);
            lblAvance.setText(mensajeAvance);
        }
    }
    
    /**
     * Creates new form FrmCargarRedes
     * @param frmPrincipal Formulario que contiene a este formulario
     */
    public FrmCalcularNetKDE(FrmPrincipal frmPrincipal) {
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
        
        //Se carga el combo de funciones de núcleo
        DbFuncionesNucleo dbFuncionesNucleo = new DbFuncionesNucleo();
        ArrayList<FuncionNucleo> listaFuncionesNucleo = dbFuncionesNucleo.getListaFuncionesNucleo(false, 1);
        
        this.cmbFuncionNucleo.removeAllItems();
        for (FuncionNucleo funcionNucleoAux : listaFuncionesNucleo) {
            this.cmbFuncionNucleo.addItem(funcionNucleoAux);
        }
        this.cmbFuncionNucleo.setSelectedIndex(-1);
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
            this.tpaNetKDE.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select an event.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbEventos.requestFocusInWindow();
            return false;
        }
        
        if (this.txtDistProy.getText().equals("")) {
            this.tpaNetKDE.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the maximum projection distance.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistProy.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtDistProy.getText());
            } catch (NumberFormatException e) {
                this.tpaNetKDE.setSelectedIndex(1);
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
            this.tpaNetKDE.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a network.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbRedes.requestFocusInWindow();
            return false;
        }
        
        if (this.txtDistCierre.getText().equals("")) {
            this.tpaNetKDE.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the maxumum node closure distance.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistCierre.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtDistCierre.getText());
            } catch (NumberFormatException e) {
                this.tpaNetKDE.setSelectedIndex(1);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The maximum closure distance must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtDistCierre.requestFocusInWindow();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Método que valida si se han seleccionado todos los campos requeridos para calcular NetKDE
     * @return <code>true</code> si se seleccionaron todos los campos requeridos,
     * de lo contrario <code>false</code>.
     */
    private boolean validarCamposCalculo() {
        //Se validan los valores puestos en los filtros
        String textoAux = Utilidades.validarFiltros(this.tblNetKDEFiltros);
        if (textoAux != null) {
            JOptionPane.showMessageDialog(this.frmPrincipal, textoAux, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        //Se valida que se hayan diligenciado todos los campos
        if (this.cmbRedes.getSelectedIndex() == -1) {
            this.tpaNetKDE.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "The maximum closure distance must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbRedes.requestFocusInWindow();
            return false;
        }
        
        if (this.cmbEventos.getSelectedIndex() == -1) {
            this.tpaNetKDE.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select an event.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbEventos.requestFocusInWindow();
            return false;
        }
        
        if (this.indProy != 1) {
            this.tpaNetKDE.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this.frmPrincipal, "Network projection missing.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtDistProy.requestFocusInWindow();
            return false;
        }
        
        if (this.txtAnchoBanda.getText().equals("")) {
            this.tpaNetKDE.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the bandwidth.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtAnchoBanda.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtAnchoBanda.getText());
            } catch (NumberFormatException e) {
                this.tpaNetKDE.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The bandwidth must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtAnchoBanda.requestFocusInWindow();
                return false;
            }
        }
        
        if (this.txtLargoLixel.getText().equals("")) {
            this.tpaNetKDE.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must type the lixel length.", "Error", JOptionPane.ERROR_MESSAGE);
            this.txtLargoLixel.requestFocusInWindow();
            return false;
        } else {
            //Se valida que se haya ingresado un número
            try {
                Double.parseDouble(this.txtLargoLixel.getText());
            } catch (NumberFormatException e) {
                this.tpaNetKDE.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this.frmPrincipal, "The lixel length must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                this.txtLargoLixel.requestFocusInWindow();
                return false;
            }
        }
        
        if (this.cmbFuncionNucleo.getSelectedIndex() == -1) {
            this.tpaNetKDE.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this.frmPrincipal, "You must select a kernel function.", "Error", JOptionPane.ERROR_MESSAGE);
            this.cmbFuncionNucleo.requestFocusInWindow();
            return false;
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
        this.tblNetKDEFiltros.setEnabled(habilitar);
        this.tblNetKDEResultados.setEnabled(habilitar);
        if (!habilitar) {
            this.txtAnchoBanda.setEnabled(false);
            this.txtLargoLixel.setEnabled(false);
            this.cmbFuncionNucleo.setEnabled(false);
            this.btnCalcular.setEnabled(false);
        } else if (this.cmbEventos.getSelectedIndex() != -1) {
            //Se valida si el evento seleccionado tiene una proyección realizada
            Evento eventoAux = (Evento)this.cmbEventos.getSelectedItem();
            DbEventos dbEventos = new DbEventos();
            eventoAux = dbEventos.getEvento(false, eventoAux.getIdEvento());
            if (eventoAux.getIndProy() == 1) {
                this.txtAnchoBanda.setEnabled(true);
                this.txtLargoLixel.setEnabled(true);
                this.cmbFuncionNucleo.setEnabled(true);
                this.btnCalcular.setEnabled(true);
            }
        }
    }
    
    private void cargarTablaNetKDEFiltros() {
        //Nombres de las columnas
        String[] nombCols = new String[3];
        nombCols[0] = "Attribute";
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
        
        this.tblNetKDEFiltros.setModel(tablaAtributos);
        TableColumnModel columnas = this.tblNetKDEFiltros.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(90);
        columnas.getColumn(1).setPreferredWidth(115);
        columnas.getColumn(2).setPreferredWidth(190);
    }
    
    private void cargarTablaNetKDEResultados() {
        //Nombres de las columnas
        String[] nombCols = new String[7];
        nombCols[0] = "Bandwidth";
        nombCols[1] = "Lixel length";
        nombCols[2] = "Kernel function";
        nombCols[3] = "Filters";
        nombCols[4] = "Date/Time";
        nombCols[5] = "Delete";
        nombCols[6] = "View";
        
        //Se obtienen los registros de resultados
        DbNetKDE dbNetKDE = new DbNetKDE();
        ArrayList<NetKDEResultado> listaNetKDEResultados = dbNetKDE.getListaNetKDEResultados(false, this.idEvento);
        long[] arrIdNetKDE = new long[listaNetKDEResultados.size()];
        String [][] cuerpoTabla = new String[listaNetKDEResultados.size()][0];
        int cont = 0;
        for (int i = listaNetKDEResultados.size() - 1; i >= 0; i--) {
            NetKDEResultado netKDEResultadoAux = listaNetKDEResultados.get(i);
            
            arrIdNetKDE[cont] = netKDEResultadoAux.getIdNetKDE();
            
            String[] registroAux = new String[7];
            registroAux[0] = "" + netKDEResultadoAux.getAnchoBanda();
            registroAux[1] = "" + netKDEResultadoAux.getLargoLixel();
            registroAux[2] = "" + netKDEResultadoAux.getFuncionNucleo().getNombreFuncion();
            registroAux[3] = netKDEResultadoAux.getFiltrosResultado().equals("") ? "(Ninguno)" : netKDEResultadoAux.getFiltrosResultado();
            registroAux[4] = netKDEResultadoAux.getFechaResultado();
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
        
        this.tblNetKDEResultados.setModel(tablaRedes);
        TableColumnModel columnas = this.tblNetKDEResultados.getColumnModel();
        columnas.getColumn(0).setPreferredWidth(75);
        columnas.getColumn(1).setPreferredWidth(75);
        columnas.getColumn(2).setPreferredWidth(115);
        columnas.getColumn(3).setPreferredWidth(185);
        columnas.getColumn(4).setPreferredWidth(135);
        columnas.getColumn(5).setPreferredWidth(45);
        columnas.getColumn(6).setPreferredWidth(45);
        
        this.tblNetKDEResultados.getColumn("Delete").setCellRenderer(new ButtonRendererNetKDE());
        this.tblNetKDEResultados.getColumn("View").setCellRenderer(new ButtonRendererNetKDE());
        this.tblNetKDEResultados.getColumn("Delete").setCellEditor(
            new ButtonEditorNetKDE(new JCheckBox(), this.tblNetKDEResultados, this, arrIdNetKDE, 'B')
        );
        this.tblNetKDEResultados.getColumn("View").setCellEditor(
            new ButtonEditorNetKDE(new JCheckBox(), this.tblNetKDEResultados, this, arrIdNetKDE, 'V')
        );
    }
    
    private void seleccionarRed() {
        this.idEvento = -1;
        
        //Se verifica que se haya seleccionado una red
        if (this.cmbRedes.getSelectedIndex() != -1) {
            //Se verifica si ya se efectuó el cierre de nodos sobre la red
            Red red = (Red)cmbRedes.getSelectedItem();
            this.idRed = red.getIdRed();
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
            ArrayList<Evento> listaEventos = dbEventos.getListaEventos(false, this.idRed);
            
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
            this.cargarTablaNetKDEFiltros();
            
            //Se llena la tabla de resultados para el evento
            this.cargarTablaNetKDEResultados();
            
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
            
            this.tblNetKDEFiltros.setModel(new DefaultTableModel(new String[0][0], new String[0]));
            this.tblNetKDEResultados.setModel(new DefaultTableModel(new String[0][0], new String[0]));
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
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        cmbRedes = new javax.swing.JComboBox();
        lblCerradoNodos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbEventos = new javax.swing.JComboBox();
        lblProyectado = new javax.swing.JLabel();
        btnCalcular = new javax.swing.JButton();
        lblAvance = new javax.swing.JLabel();
        tpaNetKDE = new javax.swing.JTabbedPane();
        panCalculoNetKDE = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtAnchoBanda = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtLargoLixel = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNetKDEFiltros = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        cmbFuncionNucleo = new javax.swing.JComboBox();
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
        tblNetKDEResultados = new javax.swing.JTable();

        setClosable(true);
        setTitle("NetKDE");
        setMinimumSize(new java.awt.Dimension(760, 517));
        setPreferredSize(new java.awt.Dimension(760, 517));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NetKDE");

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
                    .addComponent(lblCerradoNodos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbRedes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4)
                        .addComponent(cmbEventos, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblProyectado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbRedes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(cmbEventos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProyectado)
                    .addComponent(lblCerradoNodos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCalcular.setText("Calculate");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        lblAvance.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAvance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAvance.setText(".");

        tpaNetKDE.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel2.setText("Bandwidth (meters)");

        txtAnchoBanda.setToolTipText("");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Calculate new value");

        jLabel6.setText("Lixel length (meters)");

        txtLargoLixel.setToolTipText("");

        jLabel9.setText("Kernel function");

        tblNetKDEFiltros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblNetKDEFiltros.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblNetKDEFiltros);

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
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtAnchoBanda)
                                .addGap(45, 45, 45)))
                        .addGap(119, 119, 119)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtLargoLixel)
                                .addGap(44, 44, 44))
                            .addComponent(jLabel6))
                        .addGap(109, 109, 109)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(cmbFuncionNucleo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                        .addComponent(txtAnchoBanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFuncionNucleo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLargoLixel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panCalculoNetKDELayout = new javax.swing.GroupLayout(panCalculoNetKDE);
        panCalculoNetKDE.setLayout(panCalculoNetKDELayout);
        panCalculoNetKDELayout.setHorizontalGroup(
            panCalculoNetKDELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCalculoNetKDELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panCalculoNetKDELayout.setVerticalGroup(
            panCalculoNetKDELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCalculoNetKDELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpaNetKDE.addTab("Calculation", panCalculoNetKDE);

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
                        .addComponent(lblProyectado2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpaNetKDE.addTab("Network Closure and Points Projection", panCierreProy);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Calculated values");

        tblNetKDEResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblNetKDEResultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblNetKDEResultados);

        javax.swing.GroupLayout panResultadosLayout = new javax.swing.GroupLayout(panResultados);
        panResultados.setLayout(panResultadosLayout);
        panResultadosLayout.setHorizontalGroup(
            panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panResultadosLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE))
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

        tpaNetKDE.addTab("Results", panResultados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnCalcular)
                    .addComponent(tpaNetKDE)
                    .addComponent(lblAvance, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
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
                .addComponent(tpaNetKDE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            this.tpaNetKDE.setSelectedIndex(0);
            //Se inhabilitan los componentes
            this.habilitarComponentes(false);
            
            //Se inicia el hilo que muestra el avance del proceso
            final AvanceCalculo tareaCalculo = new AvanceCalculo();
            Thread hiloProcesar = new Thread(tareaCalculo, "Procesando");
            hiloProcesar.start();
            
            Runnable procesoCarga = new Runnable() {
                @Override
                public void run() {
                    long idNetKDE = -1;
                    try {
                        lblAvance.setVisible(true);
                        Evento evento = (Evento)cmbEventos.getSelectedItem();
                        String textoAux = txtAnchoBanda.getText();
                        double anchoBanda = Double.parseDouble(textoAux);
                        textoAux = txtLargoLixel.getText();
                        double largoLixel = Double.parseDouble(textoAux);
                        FuncionNucleo funcionNucleoAux = (FuncionNucleo)cmbFuncionNucleo.getSelectedItem();
                        LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros = Utilidades.obtenerFiltros(tblNetKDEFiltros);
                        
                        //Se llama a la clase que realiza el cálculo
                        PrCalculoNetKDE prCalculoNetKDE = new PrCalculoNetKDE(evento, mapaFiltros, anchoBanda, largoLixel, funcionNucleoAux.getIdFuncion());
                        
                        //Se realiza la carga
                        idNetKDE = prCalculoNetKDE.calcularNetKDE();
                    } finally {
                        tareaCalculo.setControlCorrer(false);
                        lblAvance.setVisible(false);
                        habilitarComponentes(true);
                    }
                    
                    if (idNetKDE > 0) {
                        //Si el resultado fue correcto, se actualiza la tabla de redes
                        cargarTablaNetKDEResultados();
                        
                        tpaNetKDE.setSelectedIndex(2);
                        JOptionPane.showMessageDialog(frmPrincipal, "NetKDE calculated successfully.", "NetKDE", JOptionPane.INFORMATION_MESSAGE);
                        
                        //Se limpia el formulario
                        cargarTablaNetKDEFiltros();
                        txtAnchoBanda.setText("");
                        txtLargoLixel.setText("");
                        cmbFuncionNucleo.setSelectedIndex(-1);
                        habilitarComponentes(true);
                    } else {
                        switch (Integer.parseInt(idNetKDE + "")) {
                            case -2:
                                JOptionPane.showMessageDialog(frmPrincipal, "Error creating lixel temporary records, please check if the information is valid", "NetKDE", JOptionPane.ERROR_MESSAGE);
                                break;
                            case -3:
                                JOptionPane.showMessageDialog(frmPrincipal, "Error creating lixel details temporary records, please check if the information is valid", "NetKDE", JOptionPane.ERROR_MESSAGE);
                                break;
                            case -4:
                                JOptionPane.showMessageDialog(frmPrincipal, "Error creating points temporary records, please check if the information is valid", "NetKDE", JOptionPane.ERROR_MESSAGE);
                                break;
                            case -5:
                                JOptionPane.showMessageDialog(frmPrincipal, "Error creating results temporary records, please check if the information is valid", "NetKDE", JOptionPane.ERROR_MESSAGE);
                                break;
                            default:
                                JOptionPane.showMessageDialog(frmPrincipal, "Calculating error, please check if the information is valid", "NetKDE", JOptionPane.ERROR_MESSAGE);
                                break;
                        }
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
    private javax.swing.JComboBox cmbFuncionNucleo;
    private javax.swing.JComboBox cmbRedes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel panCalculoNetKDE;
    private javax.swing.JPanel panCierreProy;
    private javax.swing.JPanel panResultados;
    private javax.swing.JTable tblNetKDEFiltros;
    private javax.swing.JTable tblNetKDEResultados;
    private javax.swing.JTabbedPane tpaNetKDE;
    private javax.swing.JTextField txtAnchoBanda;
    private javax.swing.JTextField txtDistCierre;
    private javax.swing.JTextField txtDistProy;
    private javax.swing.JTextField txtLargoLixel;
    // End of variables declaration//GEN-END:variables
    
    class ButtonRendererNetKDE extends JButton implements TableCellRenderer {
        public ButtonRendererNetKDE() {
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
    
    class ButtonEditorNetKDE extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final char tipoAccion;
        private final JTable table;
        private final FrmCalcularNetKDE frmCalcularNetKDE;
        private final long[] arrIdNetKDE;
        
        public ButtonEditorNetKDE(JCheckBox checkBox, JTable table, FrmCalcularNetKDE frmCalcularNetKDE, long[] arrIdNetKDE, char tipoAccion) {
            super(checkBox);
            this.table = table;
            this.frmCalcularNetKDE = frmCalcularNetKDE;
            this.arrIdNetKDE = arrIdNetKDE;
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
                        FrmMostrarNetKDE frmMostrarNetKDE = new FrmMostrarNetKDE(idRed, this.arrIdNetKDE[this.table.getSelectedRow()], this.frmCalcularNetKDE);
                        this.frmCalcularNetKDE.getFrmPrincipal().getPanPrincipal().add(frmMostrarNetKDE, 0);
                        this.frmCalcularNetKDE.habilitarComponentes(false);
                        this.frmCalcularNetKDE.setVisible(false);
                        frmMostrarNetKDE.setVisible(true);
                        try {
                            frmMostrarNetKDE.setMaximum(true);
                        } catch (PropertyVetoException ex) {
                        }
                        frmMostrarNetKDE.mostrarResultados();
                        break;
                    case 'B': //Borrar
                        int seleccionAux = JOptionPane.showConfirmDialog(frmPrincipal, "Do you want to delete the NetKDE result?", "Deleting Results", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (seleccionAux == JOptionPane.YES_OPTION) {
                            habilitarComponentes(false);
                            DbNetKDE dbNetKDE = new DbNetKDE();
                            int resultadoBorrar = dbNetKDE.borrarNetKDEResultado(false, this.arrIdNetKDE[this.table.getSelectedRow()]);
                            switch (resultadoBorrar) {
                                case 1:
                                    JOptionPane.showMessageDialog(frmPrincipal, "Result deleted successfully.", "Deleting Results", JOptionPane.INFORMATION_MESSAGE);
                                    cargarTablaNetKDEResultados();
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
