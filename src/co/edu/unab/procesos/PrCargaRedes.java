package co.edu.unab.procesos;

import co.edu.unab.FrmPrincipal;
import co.edu.unab.db.DbRedes;
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
 * Clase para la carga de archivos KML de redes
 * @author Feisar Moreno
 * @date 25/02/2016
 */
public class PrCargaRedes {
    private long idRed;
    private final long idRedTmp;
    private boolean indInicioCreacionRed;
    private final String nombreArchivo;
    private long idLinea;
    private final Red redTmp;
    private final ArrayList<RedAtributo> listaTmpRedesAtributos;
    private final ArrayList<RedLinea> listaTmpRedesLineas;
    private final ArrayList<RedLineaAtributo> listaTmpRedesLineasAtributos;
    private final ArrayList<RedLineaDet> listaTmpRedesLineasDet;
    private final FrmPrincipal frmPrincipal;
    
    /**
     * Constructor de la clase
     * @param descRed Descripción de la red
     * @param sistemaCoordenadas Objeto que representa el sistema de coordenadas
     * @param nombreArchivo Nombre del archivo KML que contiene la red
     * @param frmPrincipal Formulario principal
     */
    public PrCargaRedes(String descRed, SistemaCoordenadas sistemaCoordenadas, String nombreArchivo, FrmPrincipal frmPrincipal) {
        this.nombreArchivo = nombreArchivo;
        this.indInicioCreacionRed = false;
        this.redTmp = new Red(1, descRed, sistemaCoordenadas);
        this.listaTmpRedesAtributos = new ArrayList<>();
        this.listaTmpRedesLineas = new ArrayList<>();
        this.listaTmpRedesLineasAtributos = new ArrayList<>();
        this.listaTmpRedesLineasDet = new ArrayList<>();
        this.frmPrincipal = frmPrincipal;
        
        //Se crea el registro de red temporal
        DbRedes dbRedes = new DbRedes();
        this.idRedTmp = dbRedes.crearTmpRed(false, this.redTmp);
    }
    
    public long getIdRed() {
        return this.idRed;
    }
    
    public long getIdRedTmp() {
        return this.idRedTmp;
    }
    
    public boolean getIndInicioCreacionRed() {
        return this.indInicioCreacionRed;
    }
    
    /**
     * Método que carga el archivo KML de redes
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
                DbRedes dbRedes = new DbRedes();
                
                //Se revisan los nodos
                this.idLinea = 0;
                resultado = this.procesarNodos(doc.getChildNodes());
                
                //Se finaliza moviendo todos los datos de las tablas temporales a las tablas definitivas
                if (resultado) {
                    //Se insertan los valores en las tablas temporales
                    resultado = dbRedes.crearTmpRedComponentes(false, this.idRedTmp, this.listaTmpRedesAtributos, this.listaTmpRedesLineas, this.listaTmpRedesLineasAtributos, this.listaTmpRedesLineasDet);
                    
                    if (resultado) {
                        this.indInicioCreacionRed = true;
                        this.idRed = dbRedes.crearRed(false, this.idRedTmp);
                        resultado = (this.idRed > 0);
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
                        this.idLinea++;
                        RedLinea redLineaAux = new RedLinea(1, this.idLinea);
                        this.listaTmpRedesLineas.add(redLineaAux);
                        resultado = this.procesarLineas(nodoAux.getChildNodes());
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
                        RedAtributo redAtributoAux = new RedAtributo(1, ++contAtributos, nombreNodo, tipoNodo);
                        this.listaTmpRedesAtributos.add(redAtributoAux);
                    }
                }
            }
        }
        
        return resultado;
    }
    
    /**
     * Método privado que procesa los nodos de atributos del archivo KML.
     * @param listaNodos Listado de nodos contenidos en un nodo de tipo Schema.
     */
    private boolean procesarLineas(NodeList listaNodos) {
        boolean resultado = true;
        
        for (int cont = 0; cont < listaNodos.getLength() && resultado; cont++) {
            Node nodoAux = listaNodos.item(cont);
            
            //Solo se procesan nodos de elementos
            if (nodoAux.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + nodoAux.getNodeName());
                
                switch (nodoAux.getNodeName().toLowerCase()) {
                    case "simpledata":
                        resultado = this.procesarAtributosLinea(nodoAux);
                        break;
                        
                    case "coordinates":
                        resultado = this.procesarCoordenadasLinea(nodoAux);
                        break;
                        
                    default:
                        if (nodoAux.hasChildNodes()) {
                            //Se navega sobre los nodos hijos
                            resultado = this.procesarLineas(nodoAux.getChildNodes());
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
    private boolean procesarAtributosLinea(Node nodo) {
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
            
            //Se busca el id del atributo
            long idAtributoAux = -1;
            boolean indNumero = false;
            for (RedAtributo redAtributoAux : this.listaTmpRedesAtributos) {
                if (redAtributoAux.getNombreAtributo().equalsIgnoreCase(nombreAtributo)) {
                    idAtributoAux = redAtributoAux.getIdAtributo();
                    
                    //Se determina si el valor es numérico o de texto
                    switch (redAtributoAux.getTipoAtributo().toLowerCase()) {
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
            RedLineaAtributo redLineaAtributoAux;
            if (indNumero) {
                redLineaAtributoAux = new RedLineaAtributo(1, this.idLinea, idAtributoAux, Double.parseDouble(valorAtributo), "");
            } else {
                redLineaAtributoAux = new RedLineaAtributo(1, this.idLinea, idAtributoAux, 0, valorAtributo);
            }
            this.listaTmpRedesLineasAtributos.add(redLineaAtributoAux);
        }
        
        return true;
    }
    
    /**
     * Método privado que procesa las coordenadas (puntos) de una línea del archivo KML.
     */
    private boolean procesarCoordenadasLinea(Node nodo) {
        boolean resultado = true;
        
        //Se obtiene el listado de coordenadas
        String[] arrTuplas = nodo.getTextContent().split(" ");
        int contTupla = 0;
        for (String tuplaAux : arrTuplas) {
            contTupla++;
            
            String[] arrCoordenadasAux = tuplaAux.split(",");
            if (arrCoordenadasAux.length == 2) {
                try {
                    double longitud = Double.parseDouble(arrCoordenadasAux[0]);
                    double latitud = Double.parseDouble(arrCoordenadasAux[1]);
                    
                    RedLineaDet redLineaDetAux = new RedLineaDet(1, this.idLinea, contTupla, latitud, longitud);
                    this.listaTmpRedesLineasDet.add(redLineaDetAux);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    resultado = false;
                    break;
                }
            } else {
                resultado = false;
                break;
            }
        }
        
        return resultado;
    }
    
    /**
     * Método estático que devuelve la cantidad de registros cargados en la tabla tmp_redes_lineas
     * @param idRed Identificador de la red
     * @return Cantidad de registros encontrados.
     */
    public static long getCantidadTmpLineas(long idRed) {
        DbRedes dbRedes = new DbRedes();
        ValorGenerico valorGenerico = dbRedes.getCantidadTmpLineas(false, idRed);
        
        return valorGenerico.getValorEntero();
    }
    
    /**
     * Método estático que devuelve la cantidad de registros cargados en la tabla tmp_redes_lineas_atributos
     * @param idRed Identificador de la red
     * @return Cantidad de registros encontrados.
     */
    public static long getCantidadTmpLineasAtributos(long idRed) {
        DbRedes dbRedes = new DbRedes();
        ValorGenerico valorGenerico = dbRedes.getCantidadTmpLineasAtributos(false, idRed);
        
        return valorGenerico.getValorEntero();
    }
    
    /**
     * Método estático que devuelve la cantidad de registros cargados en la tabla tmp_redes_lineas_det
     * @param idRed Identificador de la red
     * @return Cantidad de registros encontrados.
     */
    public static long getCantidadTmpLineasDet(long idRed) {
        DbRedes dbRedes = new DbRedes();
        ValorGenerico valorGenerico = dbRedes.getCantidadTmpLineasDet(false, idRed);
        
        return valorGenerico.getValorEntero();
    }
}
