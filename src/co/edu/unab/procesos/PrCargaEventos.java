package co.edu.unab.procesos;

import co.edu.unab.FrmPrincipal;
import co.edu.unab.db.DbEventos;
import co.edu.unab.entidad.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Clase para la carga de archivos KML de eventos
 * @author Feisar Moreno
 * @date 04/03/2016
 */
public class PrCargaEventos {
    private long idEvento;
    private long idEventoTmp;
    private boolean indInicioCreacionEvento;
    private final String nombreArchivo;
    private String atributoFecha;
    private String formatoFecha;
    private String atributoHora;
    private String formatoHora;
    private long idPunto;
    private String fechaPunto;
    private double latitudPunto;
    private double longitudPunto;
    private ArrayList<EventoAtributo> listaTmpEventosAtributos;
    private ArrayList<EventoPunto> listaTmpEventosPuntos;
    private ArrayList<EventoPuntoAtributo> listaTmpEventosPuntosAtributos;
    private FrmPrincipal frmPrincipal;
    
    /**
     * Constructor de la clase
     * @param descEvento Descripción de la red
     * @param red Objeto que representa la red
     * @param nombreArchivo Nombre del archivo KML que contiene la red
     * @param atributoFecha Atributo que contiene la fecha del evento
     * @param formatoFecha Formato de la fecha en el archivo KML
     * @param atributoHora Atributo que contiene la hora del evento
     * @param formatoHora Formato de la hora en el archivo KML
     * @param frmPrincipal Formulario principal
     */
    public PrCargaEventos(String descEvento, Red red, String nombreArchivo, String atributoFecha, String formatoFecha, String atributoHora, String formatoHora, FrmPrincipal frmPrincipal) {
        this.nombreArchivo = nombreArchivo;
        this.atributoFecha = atributoFecha;
        this.formatoFecha = formatoFecha;
        this.atributoHora = atributoHora;
        this.formatoHora = formatoHora;
        this.indInicioCreacionEvento = false;
        this.listaTmpEventosAtributos = new ArrayList<>();
        this.listaTmpEventosPuntos = new ArrayList<>();
        this.listaTmpEventosPuntosAtributos = new ArrayList<>();
        this.frmPrincipal = frmPrincipal;
        
        //Se crea el registro del evento
        DbEventos dbEventos = new DbEventos();
        this.idEventoTmp = dbEventos.crearTmpEvento(false, descEvento, red.getIdRed());
    }
    
    /**
     * Constructor de la clase
     * @param nombreArchivo Nombre del archivo KML que contiene la red
     */
    public PrCargaEventos(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public long getIdEvento() {
        return this.idEvento;
    }
    
    public long getIdEventoTmp() {
        return this.idEventoTmp;
    }
    
    public boolean getIndInicioCreacionEvento() {
        return this.indInicioCreacionEvento;
    }
    
    /**
     * Método que carga el archivo KML de puntos
     * @return <code>true</code> si se logra cargar el archivo con éxito, de lo contrario <code>false</code>.
     */
    public boolean cargarArchivo() {
        boolean resultado;
        //Se abre el archivo seleccionado
        try {
            File archKML = new File(this.nombreArchivo);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(archKML);
            
            if (doc.hasChildNodes()) {
                DbEventos dbEventos = new DbEventos();
                
                //Se revisan los puntos
                this.idPunto = 0;
                resultado = this.procesarNodos(doc.getChildNodes());
                
                //Se finaliza moviendo todos los datos de las tablas temporales a las tablas definitivas
                if (resultado) {
                    //Se insertan los valores en las tablas temporales
                    resultado = dbEventos.crearTmpEventoComponentes(false, this.idEventoTmp, (this.formatoFecha + " " + this.formatoHora).trim(), this.listaTmpEventosAtributos, this.listaTmpEventosPuntos, this.listaTmpEventosPuntosAtributos);
                    
                    if (resultado) {
                        this.indInicioCreacionEvento = true;
                        this.idEvento = dbEventos.crearEvento(false, this.idEventoTmp, this.atributoFecha, this.atributoHora);
                        resultado = (this.idEvento > 0);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this.frmPrincipal, "The selected file does not contain nodes.", "File Upload", JOptionPane.ERROR_MESSAGE);
                resultado = false;
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
            resultado = false;
        }
        
        return resultado;
    }
    
    private boolean procesarNodos(NodeList listaNodos) {
        boolean resultado = true;
        for (int count = 0; count < listaNodos.getLength() && resultado; count++) {
            Node nodoAux = listaNodos.item(count);
            
            //Solo se validan nodos de elementos
            if (nodoAux.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + nodoAux.getNodeName());
                
                //Se valida de acuerdo al tipo de nodo
                switch (nodoAux.getNodeName().toLowerCase()) {
                    case "schema": //Atributos
                        resultado = this.procesarAtributos(nodoAux.getChildNodes());
                        break;
                        
                    case "placemark": //Segmentos
                        this.idPunto++;
                        this.fechaPunto = "";
                        this.latitudPunto = 0L;
                        this.longitudPunto = 0L;
                        
                        EventoPunto eventoPuntoAux = new EventoPunto(1, this.idPunto, "", 0, 0);
                        this.listaTmpEventosPuntos.add(eventoPuntoAux);
                        
                        resultado = this.procesarPuntos(nodoAux.getChildNodes());
                        if (resultado) {
                            //Se actualizan la fecha y las coordenadas del punto
                            if (!this.fechaPunto.equals("") && this.latitudPunto != 0 && this.longitudPunto != 0) {
                                //Se simplifica el formato de la fecha
                                this.fechaPunto = this.fechaPunto.toUpperCase();
                                this.fechaPunto = this.fechaPunto.replace(".", "");
                                this.fechaPunto = this.fechaPunto.replace("A M", "AM");
                                this.fechaPunto = this.fechaPunto.replace("P M", "PM");
                                
                                eventoPuntoAux.setFechaPunto(this.fechaPunto);
                                eventoPuntoAux.setLatitud(this.latitudPunto);
                                eventoPuntoAux.setLongitud(this.longitudPunto);
                            } else {
                                resultado = false;
                            }
                        }
                        break;
                        
                    default:
                        if (nodoAux.hasChildNodes()) {
                            //Se navega sobre los nodos hijos
                            resultado = this.procesarNodos(nodoAux.getChildNodes());
                        }
                        break;
                }
                
                if (!resultado) {
                    break;
                }
            }
        }
        
        return resultado;
    }
    
    /**
     * Método privado que procesa los nodos de atributos del archivo KML.
     * @param listaNodos Listado de nodos contenidos en un nodo de tipo Schema.
     */
    private boolean procesarAtributos(NodeList listaNodos) {
        boolean resultado = true;
        DbEventos dbEventos = new DbEventos();
        try {
            dbEventos.crearConexion();
            int contAtributos = 0;
            for (int cont = 0; cont < listaNodos.getLength(); cont++) {
                Node nodoAux = listaNodos.item(cont);

                //Solo se procesan nodos de elementos
                if (nodoAux.getNodeType() == Node.ELEMENT_NODE) {
                    if (nodoAux.getNodeName().equalsIgnoreCase("simplefield") && nodoAux.hasAttributes()) {
                        //Se obtienen los nombres y valores de los atributos
                        NamedNodeMap mapaAtributos = nodoAux.getAttributes();

                        String nombreNodo = "", tipoNodo = "";
                        for (int i = 0; i < mapaAtributos.getLength(); i++) {
                            Node nodo = mapaAtributos.item(i);

                            switch (nodo.getNodeName().toLowerCase()) {
                                case "name":
                                    nombreNodo = nodo.getNodeValue();
                                    break;
                                case "type":
                                    tipoNodo = nodo.getNodeValue();
                                    break;
                            }
                        }

                        if (!nombreNodo.equals("") && !tipoNodo.equals("")) {
                            //Se crea el registro del atributo
                            EventoAtributo eventoAtributoAux = new EventoAtributo(1, ++contAtributos, nombreNodo, tipoNodo);
                            this.listaTmpEventosAtributos.add(eventoAtributoAux);
                        }
                    }
                }
            }
        } finally {
            dbEventos.cerrarConexion();
        }
        
        return resultado;
    }
    
    /**
     * Método privado que procesa los nodos de atributos del archivo KML.
     * @param listaNodos Listado de nodos contenidos en un nodo de tipo Schema.
     */
    private boolean procesarPuntos(NodeList listaNodos) {
        boolean resultado = true;
        
        for (int cont = 0; cont < listaNodos.getLength() && resultado; cont++) {
            Node nodoAux = listaNodos.item(cont);
            
            //Solo se procesan nodos de elementos
            if (nodoAux.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + nodoAux.getNodeName());
                
                switch (nodoAux.getNodeName().toLowerCase()) {
                    case "simpledata":
                        resultado = this.procesarAtributosPunto(nodoAux);
                        break;
                        
                    case "coordinates":
                        resultado = this.procesarCoordenadasPunto(nodoAux);
                        break;
                        
                    default:
                        if (nodoAux.hasChildNodes()) {
                            //Se navega sobre los nodos hijos
                            resultado = this.procesarPuntos(nodoAux.getChildNodes());
                        }
                        break;
                }
            }
        }
        
        return resultado;
    }
    
    /**
     * Método privado que procesa los atributos de una línea del archivo KML.
     */
    private boolean procesarAtributosPunto(Node nodo) {
        boolean resultado = true;
        
        //Se busca el nombre del atributo
        NamedNodeMap mapaAtributos = nodo.getAttributes();

        String nombreAtributo = "";
        for (int i = 0; i < mapaAtributos.getLength(); i++) {
            Node nodoAux = mapaAtributos.item(i);

            switch (nodoAux.getNodeName().toLowerCase()) {
                case "name":
                    nombreAtributo = nodoAux.getNodeValue();
                    break;
            }
        }
        
        if (!nombreAtributo.equals("")) {
            //Se obtiene el valor del atributo
            String valorAtributo = nodo.getTextContent();
            
            if (nombreAtributo.equalsIgnoreCase(this.atributoFecha)) {
                this.fechaPunto = valorAtributo + this.fechaPunto;
            } else if (nombreAtributo.equalsIgnoreCase(this.atributoHora) && !this.atributoFecha.equalsIgnoreCase(this.atributoHora)) {
                this.fechaPunto += " " + valorAtributo;
            } else {
                //Se busca el id del atributo
                long idAtributoAux = -1;
                boolean indNumero = false;
                for (EventoAtributo eventoAtributoAux : this.listaTmpEventosAtributos) {
                    if (eventoAtributoAux.getNombreAtributo().equalsIgnoreCase(nombreAtributo)) {
                        idAtributoAux = eventoAtributoAux.getIdAtributo();
                        
                        //Se determina si el valor es numérico o de texto
                        switch (eventoAtributoAux.getTipoAtributo().toLowerCase()) {
                            case "int":
                            case "uint":
                            case "short":
                            case "ushort":
                            case "float":
                            case "double":
                                indNumero = true;
                                break;
                        }
                        break;
                    }
                }
                
                if (idAtributoAux < 0) {
                    return false;
                }
                
                //Se inserta el registro
                EventoPuntoAtributo eventoPuntoAtributoAux;
                if (indNumero) {
                    eventoPuntoAtributoAux = new EventoPuntoAtributo(1, this.idPunto, idAtributoAux, Double.parseDouble(valorAtributo), "");
                } else {
                    eventoPuntoAtributoAux = new EventoPuntoAtributo(1, this.idPunto, idAtributoAux, 0, valorAtributo);
                }
                this.listaTmpEventosPuntosAtributos.add(eventoPuntoAtributoAux);
            }
        }
        
        return resultado;
    }
    
    /**
     * Método privado que procesa las coordenadas de un punto del archivo KML.
     */
    private boolean procesarCoordenadasPunto(Node nodo) {
        boolean resultado = true;
        
        //Se obtienen las coordenadas
        String[] arrCoordenadasAux = nodo.getTextContent().split(",");
        if (arrCoordenadasAux.length == 2) {
            try {
                this.longitudPunto = Double.parseDouble(arrCoordenadasAux[0]);
                this.latitudPunto = Double.parseDouble(arrCoordenadasAux[1]);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                resultado = false;
            }
        } else {
            resultado = false;
        }
        
        return resultado;
    }
    
    /**
     * Método que carga el archivo KML de puntos
     * @return <code>true</code> si se logra cargar el archivo con éxito, de lo contrario <code>false</code>.
     */
    public ArrayList<String> obtenerListaAtributos() {
        ArrayList<String> listaAtributos = null;
        //Se abre el archivo seleccionado
        try {
            File archKML = new File(this.nombreArchivo);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(archKML);
            
            if (doc.hasChildNodes()) {
                listaAtributos = this.obtenerListaAtributosNodos(doc.getChildNodes());
            } else {
                JOptionPane.showMessageDialog(this.frmPrincipal, "The selected file does not contain nodes.", "File Upload", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this.frmPrincipal, "File structure not valid.", "File Upload", JOptionPane.ERROR_MESSAGE);
        }
        
        return listaAtributos;
    }
    
    private ArrayList<String> obtenerListaAtributosNodos(NodeList listaNodos) {
        ArrayList<String> listaAtributos = null;
        for (int count = 0; count < listaNodos.getLength(); count++) {
            Node nodoAux = listaNodos.item(count);
            
            //Solo se validan nodos de elementos
            if (nodoAux.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + nodoAux.getNodeName());
                
                //Se valida de acuerdo al tipo de nodo
                if (nodoAux.getNodeName().equalsIgnoreCase("schema")) {
                    listaAtributos = this.obtenerListaAtributosNodosDet(nodoAux.getChildNodes());
                    break;
                } else {
                    listaAtributos = this.obtenerListaAtributosNodos(nodoAux.getChildNodes());
                }
            }
        }
        
        return listaAtributos;
    }
    
    private ArrayList<String> obtenerListaAtributosNodosDet(NodeList listaNodos) {
        ArrayList<String> listaAtributos = new ArrayList<>();
        for (int cont = 0; cont < listaNodos.getLength(); cont++) {
            Node nodoAux = listaNodos.item(cont);

            //Solo se procesan nodos de elementos
            if (nodoAux.getNodeType() == Node.ELEMENT_NODE) {
                if (nodoAux.getNodeName().equalsIgnoreCase("simplefield") && nodoAux.hasAttributes()) {
                    //Se obtienen los nombres de los atributos
                    NamedNodeMap mapaAtributos = nodoAux.getAttributes();
                    
                    String nombreNodo = "";
                    for (int i = 0; i < mapaAtributos.getLength(); i++) {
                        Node nodo = mapaAtributos.item(i);
                        
                        if (nodo.getNodeName().equalsIgnoreCase("name")) {
                            nombreNodo = nodo.getNodeValue();
                        }
                    }
                    
                    if (!nombreNodo.equals("")) {
                        listaAtributos.add(nombreNodo);
                    }
                }
            }
        }
        
        return listaAtributos;
    }
    
    /**
     * Método estático que devuelve la cantidad de registros cargados en la tabla tmp_eventos_puntos
     * @param idEvento Identificador del evento
     * @return Cantidad de registros encontrados.
     */
    public static long getCantidadTmpPuntos(long idEvento) {
        DbEventos dbEventos = new DbEventos();
        ValorGenerico valorGenerico = dbEventos.getCantidadTmpPuntos(false, idEvento);
        
        return valorGenerico.getValorEntero();
    }
    
    /**
     * Método estático que devuelve la cantidad de registros cargados en la tabla tmp_eventos_puntos_atributos
     * @param idEvento Identificador del evento
     * @return Cantidad de registros encontrados.
     */
    public static long getCantidadTmpPuntosAtributos(long idEvento) {
        DbEventos dbEventos = new DbEventos();
        ValorGenerico valorGenerico = dbEventos.getCantidadTmpPuntosAtributos(false, idEvento);
        
        return valorGenerico.getValorEntero();
    }
}
