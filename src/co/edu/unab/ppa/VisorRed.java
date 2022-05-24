package co.edu.unab.ppa;

import co.edu.unab.db.DbEventos;
import co.edu.unab.db.DbNetKDE;
import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.*;
import co.edu.unab.utilidades.CalculadorDistribucion;
import co.edu.unab.utilidades.ManejadorColor;
import co.edu.unab.utilidades.Utilidades;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.JComponent;

/**
 * Clase para visualizar redes, eventos y resultados NetKDE
 * @author Feisar Moreno
 * date 26/06/2016
 */
public class VisorRed extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final long idRed;
    private long idEvento;
    private long idNetKDE;
    private VisorRed visor2;
    private final String tipoVista;
    private boolean indVerAtributos;
    private RedAtributo redAtributo;
    private Red red;
    private UnidadMedida unidadMedida;
    private Evento evento;
    private LinkedHashMap<Long, RedLinea> mapaRedesLineas;
    private LinkedHashMap<Long, ArrayList<RedLineaDet>> mapaRedesLineasDet;
    private LinkedHashMap<Long, EventoPunto> mapaEventosPuntos;
    private LinkedHashMap<Long, RedLineaAtributo> mapaRedesLineasAtributos;
    private LinkedHashMap<Long, NetKDELixel> mapaNetKDELixels;
    private LinkedHashMap<String, ArrayList<NetKDELixelDet>> mapaNetKDELixelsDet;
    private double latitudMin;
    private double latitudMinBase;
    private double latitudMed;
    private double latitudMax;
    private double longitudMin;
    private double longitudMinBase;
    private double longitudMed;
    private double longitudMax;
    private double anchoLienzo;
    private double altoLienzo;
    private double xCentro;
    private double yCentro;
    private double xIni;
    private double yIni;
    private double xPar;
    private double yPar;
    private double xFin;
    private double yFin;
    private double xZoom;
    private double yZoom;
    private double factorZoom;
    private boolean indVerLeyenda;
    private BufferedImage imagenFondo;
    private boolean isDragging;
    private int porcResalte;
    private double[] arrValoresOrdenados;
    private double[] arrDeciles;
    private Color[] arrColores;
    
    /**
     * Constructor para visualización de una red.
     * @param idRed Identificador de la red
     * @param indVerAtributos Indica si se deben cargar etiquetas de atributos
     * @param redAtributo Atributo a cargar
     */
    public VisorRed(long idRed, boolean indVerAtributos, RedAtributo redAtributo) {
        this.idRed = idRed;
        this.tipoVista = "R";
        this.indVerAtributos = indVerAtributos;
        this.indVerLeyenda = false;
        this.redAtributo = redAtributo;
        this.xCentro = Double.NaN;
        this.yCentro = Double.NaN;
        this.xZoom = Double.NaN;
        this.yZoom = Double.NaN;
        this.factorZoom = 1;
        this.isDragging = false;
        this.cargarRed();
    }
    
    /**
     * Constructor para visualización de un evento.
     * @param idRed Identificador de la red
     * @param indVerAtributos Indica si se deben cargar etiquetas de atributos
     * @param redAtributo Atributo a cargar
     * @param idEvento Identificador del evento
     */
    public VisorRed(long idRed, boolean indVerAtributos, RedAtributo redAtributo, long idEvento) {
        this.idRed = idRed;
        this.idEvento = idEvento;
        this.tipoVista = "E";
        this.indVerAtributos = indVerAtributos;
        this.indVerLeyenda = false;
        this.redAtributo = redAtributo;
        this.xCentro = Double.NaN;
        this.yCentro = Double.NaN;
        this.xZoom = Double.NaN;
        this.yZoom = Double.NaN;
        this.factorZoom = 1;
        this.isDragging = false;
        this.cargarEvento();
    }
    
    /**
     * Constructor para visualización de resultados NetKDE.
     * @param idRed Identificador de la red
     * @param idNetKDE Identificador del resultado NetKDE
     * @param indVerAtributos Indica si se deben cargar etiquetas de atributos
     * @param indVerLeyenda Indica si se debe mostrar la leyenda
     * @param redAtributo Atributo a cargar
     * @param porcResalte Porcentaje de datos a resaltar como puntos calientes
     */
    public VisorRed(long idRed, long idNetKDE, boolean indVerAtributos, boolean indVerLeyenda, RedAtributo redAtributo, int porcResalte) {
        this.idRed = idRed;
        this.idNetKDE = idNetKDE;
        this.tipoVista = "N";
        this.indVerAtributos = indVerAtributos;
        this.indVerLeyenda = indVerLeyenda;
        this.redAtributo = redAtributo;
        this.xCentro = Double.NaN;
        this.yCentro = Double.NaN;
        this.xZoom = Double.NaN;
        this.yZoom = Double.NaN;
        this.factorZoom = 1;
        this.isDragging = false;
        this.porcResalte = porcResalte;
        this.cargarNetKDE();
    }
    
    private void cargarRed() {
        //Se gregan los listeners de eventos del mouse
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        
        //Se cargan los datos de la red
        DbRedes dbRedes = new DbRedes();
        this.red = dbRedes.getRed(false, this.idRed);
        this.unidadMedida = this.red.getSistemaCoordenadas().getUnidadMedida();
        
        //Se cargan las líneas de la red
        this.mapaRedesLineas = new LinkedHashMap<>();
        ArrayList<RedLinea> listaRedesLineasAux = dbRedes.getListaRedesLineas(false, this.idRed);
        for (RedLinea redLineaAux : listaRedesLineasAux) {
            this.mapaRedesLineas.put(redLineaAux.getIdLinea(), redLineaAux);
        }
        
        //Se obtienen las coordenadas mínimas y máximas del resultado
        ParCoordenadas parCoordenadasAux = dbRedes.getParCoordenadasRed(false, this.idRed);
        this.latitudMin = parCoordenadasAux.getLatitudMin();
        this.latitudMinBase = this.latitudMin;
        this.longitudMin = parCoordenadasAux.getLongitudMin();
        this.longitudMinBase = this.longitudMin;
        this.latitudMax = parCoordenadasAux.getLatitudMax();
        this.longitudMax = parCoordenadasAux.getLongitudMax();
        this.latitudMed = (this.latitudMin + this.latitudMax) / 2;
        this.longitudMed = (this.longitudMin + this.longitudMax) / 2;
        
        //Se carga el detalle de las líneas
        this.mapaRedesLineasDet = new LinkedHashMap<>();
        ArrayList<RedLineaDet> listaRedesLineasDet;
        
        if (this.unidadMedida.getIndGrados() == 0) {
            listaRedesLineasDet = dbRedes.getListaRedesLineasDet(false, this.idRed);
        } else {
            listaRedesLineasDet = dbRedes.getListaRedesLineasDet(false, this.idRed, this.latitudMinBase, this.longitudMinBase);
            this.latitudMax = Utilidades.calcularDistanciaPuntos(this.latitudMin, this.longitudMin, this.latitudMax, this.longitudMin, this.unidadMedida, 8);
            this.longitudMax = Utilidades.calcularDistanciaPuntos(this.latitudMin, this.longitudMin, this.latitudMin, this.longitudMax, this.unidadMedida, 8);
            this.latitudMin = 0;
            this.longitudMin = 0;
            this.latitudMed = (this.latitudMin + this.latitudMax) / 2;
            this.longitudMed = (this.longitudMin + this.longitudMax) / 2;
        }
        
        for (RedLineaDet redLineaDetAux : listaRedesLineasDet) {
            long llaveAux = redLineaDetAux.getIdLinea();
            
            ArrayList<RedLineaDet> listaRedesLineasDetAux;
            if (this.mapaRedesLineasDet.containsKey(llaveAux)) {
                listaRedesLineasDetAux = this.mapaRedesLineasDet.get(llaveAux);
            } else {
                listaRedesLineasDetAux = new ArrayList<>();
            }
            listaRedesLineasDetAux.add(redLineaDetAux);
            this.mapaRedesLineasDet.put(llaveAux, listaRedesLineasDetAux);
        }
    }
    
    private void cargarEvento() {
        this.cargarRed();
        
        DbEventos dbEventos = new DbEventos();
        
        //Se cargan los datos del evento
        this.evento = dbEventos.getEvento(false, this.idEvento);
        
        //Se cargan los datos de los puntos del evento
        this.mapaEventosPuntos = new LinkedHashMap<>();
        ArrayList<EventoPunto> listaEventosPuntos;
        if (this.unidadMedida.getIndGrados() == 0) {
            listaEventosPuntos = dbEventos.getListaEventosPuntos(false, this.idEvento);
        } else {
            listaEventosPuntos = dbEventos.getListaEventosPuntos(false, this.idEvento, this.latitudMinBase, this.longitudMinBase);
        }
        for (EventoPunto eventoPuntoAux : listaEventosPuntos) {
            this.mapaEventosPuntos.put(eventoPuntoAux.getIdPunto(), eventoPuntoAux);
        }
    }
    
    private void cargarNetKDE() {
        //Se gregan los listeners de eventos del mouse
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        
        DbNetKDE dbNetKDE = new DbNetKDE();
        
        //Se cargan los datos de la red
        DbRedes dbRedes = new DbRedes();
        this.red = dbRedes.getRed(false, this.idRed);
        this.unidadMedida = this.red.getSistemaCoordenadas().getUnidadMedida();
        
        //Se cargan los lixels
        this.mapaNetKDELixels = new LinkedHashMap<>();
        ArrayList<NetKDELixel> listaNetKDELixelsAux = dbNetKDE.getListaNetKDELixels(false, this.idNetKDE);
        for (NetKDELixel netKDELixelAux : listaNetKDELixelsAux) {
            this.mapaNetKDELixels.put(netKDELixelAux.getIdLixel(), netKDELixelAux);
        }
        
        //Se obtienen las coordenadas mínimas y máximas del resultado
        ParCoordenadas parCoordenadasAux = dbNetKDE.getParCoordenadasNetKDE(false, this.idNetKDE);
        this.latitudMin = parCoordenadasAux.getLatitudMin();
        this.longitudMin = parCoordenadasAux.getLongitudMin();
        this.latitudMax = parCoordenadasAux.getLatitudMax();
        this.longitudMax = parCoordenadasAux.getLongitudMax();
        this.latitudMed = (this.latitudMin + this.latitudMax) / 2;
        this.longitudMed = (this.longitudMin + this.longitudMax) / 2;
        
        //Se carga el detalle de los lixels
        this.mapaNetKDELixelsDet = new LinkedHashMap<>();
        ArrayList<NetKDELixelDet> listaNetKDELixelsDet;
        if (this.unidadMedida.getIndGrados() == 0) {
            listaNetKDELixelsDet = dbNetKDE.getListaNetKDELixelsDet(false, this.idNetKDE);
        } else {
            listaNetKDELixelsDet = dbNetKDE.getListaNetKDELixelsDet(false, this.idNetKDE, this.latitudMin, this.longitudMin);
            this.latitudMax = Utilidades.calcularDistanciaPuntos(this.latitudMin, this.longitudMin, this.latitudMax, this.longitudMin, this.unidadMedida, 8);
            this.longitudMax = Utilidades.calcularDistanciaPuntos(this.latitudMin, this.longitudMin, this.latitudMin, this.longitudMax, this.unidadMedida, 8);
            this.latitudMin = 0;
            this.longitudMin = 0;
            this.latitudMed = (this.latitudMin + this.latitudMax) / 2;
            this.longitudMed = (this.longitudMin + this.longitudMax) / 2;
        }
        
        for (NetKDELixelDet netKDELixelDetAux : listaNetKDELixelsDet) {
            String llaveAux = netKDELixelDetAux.getIdLinea() + "-" + netKDELixelDetAux.getIdLixel();
            
            ArrayList<NetKDELixelDet> listaNetKDELixelsDetAux;
            if (this.mapaNetKDELixelsDet.containsKey(llaveAux)) {
                listaNetKDELixelsDetAux = this.mapaNetKDELixelsDet.get(llaveAux);
            } else {
                listaNetKDELixelsDetAux = new ArrayList<>();
            }
            listaNetKDELixelsDetAux.add(netKDELixelDetAux);
            this.mapaNetKDELixelsDet.put(llaveAux, listaNetKDELixelsDetAux);
        }
        
        //Se cargan las líneas de la red
        this.mapaRedesLineas = new LinkedHashMap<>();
        ArrayList<RedLinea> listaRedesLineasAux = dbRedes.getListaRedesLineas(false, this.idRed);
        for (RedLinea redLineaAux : listaRedesLineasAux) {
            this.mapaRedesLineas.put(redLineaAux.getIdLinea(), redLineaAux);
        }
        
        //Se obtienen los valores ordenados
        CalculadorDistribucion cd = new CalculadorDistribucion();
        double[] arrValoresAux = this.obtenerValoresNetKDELixels();
        this.arrValoresOrdenados = cd.ordenarArreglo(arrValoresAux);
        
        //Se hallan los valores de los deciles
        this.arrDeciles = cd.obtenerDeciles(this.arrValoresOrdenados);
        
        //Se hallan los colores de las líneas
        ManejadorColor mc = new ManejadorColor();
        String[] arrColoresAux = mc.obtenerEscala("FFFF00", "AA0000", 10, false);
        this.arrColores = new Color[arrColoresAux.length];
        for (int i = 0; i < arrColoresAux.length; i++) {
            String colorAux = arrColoresAux[i];
            int rAux = Integer.valueOf(colorAux.substring(0, 2), 16);
            int gAux = Integer.valueOf(colorAux.substring(2, 4), 16);
            int bAux = Integer.valueOf(colorAux.substring(4), 16);

            this.arrColores[i] = new Color(rAux, gAux, bAux);
        }
    }
    
    private double[] obtenerValoresNetKDELixels() {
        double[] arrValores = new double[this.mapaNetKDELixels.size()];
        int i = 0;
        for (long idLixelAux : this.mapaNetKDELixels.keySet()) {
            NetKDELixel netKDELixelAux = this.mapaNetKDELixels.get(idLixelAux);
            arrValores[i] = netKDELixelAux.getDensidadLixel();

            i++;
        }
        
        return arrValores;
    }
    
    @Override
    public void paint(Graphics g) {
        //Se obtienen las dimensiones para el lienzo
        this.anchoLienzo = this.getWidth();
        this.altoLienzo = this.getHeight();
        
        if (Double.isNaN(this.xCentro)) {
            this.xCentro = this.anchoLienzo/ 2.0;
            this.yCentro = this.altoLienzo/ 2.0;
        }
        
        //Se crea el fondo blanco
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int)this.anchoLienzo, (int)this.altoLienzo);
        
        if (this.isDragging) {
            g.drawImage(this.imagenFondo, (int)(this.xPar - this.xIni), (int)(this.yPar - this.yIni), null);
        } else {
            Graphics2D g2d = (Graphics2D)g;
            
            //Se cargan los atributos de las líneas de la red
            this.mapaRedesLineasAtributos = new LinkedHashMap<>();
            if (this.indVerAtributos && this.redAtributo != null) {
                DbRedes dbRedes = new DbRedes();
                ArrayList<RedLineaAtributo> listaRedesLineasAtributosAux = dbRedes.getListaRedesLineasAtributos(false, this.idRed, this.redAtributo.getIdAtributo());
                for (RedLineaAtributo redLineaAtributoAux : listaRedesLineasAtributosAux) {
                    this.mapaRedesLineasAtributos.put(redLineaAtributoAux.getIdLinea(), redLineaAtributoAux);
                }
            }
            
            //Se calcula el factor de escala que tendrá el gráfico
            double factorEscala = this.altoLienzo / (this.latitudMax - this.latitudMin);
            double factorEscalaAux = this.anchoLienzo / (this.longitudMax - this.longitudMin);
            if (factorEscalaAux < factorEscala) {
                factorEscala = factorEscalaAux;
            }
            factorEscala *= this.factorZoom;
            
            //Se verifica si hay el suficiente acercamiento como para agregar etiquetas
            boolean indZoomEtiquetas = false;
            if (this.unidadMedida.getIndGrados() == 1) {
                //Se ajusta a metros dado que ya se hizo la conversión de las coordenadas
                this.unidadMedida = new UnidadMedida(0, "Meters", 1, 0);
            }
            double valorBase = Utilidades.calcularDistanciaPuntos(this.latitudMed, this.longitudMed, this.latitudMed, this.longitudMed + 1, this.unidadMedida, 8) / factorEscala;
            if (valorBase < 1) {
                indZoomEtiquetas = true;
            }
            
            long idLineaAnt;
            boolean indEscrito;
            RedLinea redLineaAux;
            double largoLineaCentroAux;
            double largoAcumuladoAux;
            switch (this.tipoVista) {
                case "R": //Redes
                case "E": //Eventos
                    //Se recorren los detalles de las líneas y se pintan
                    idLineaAnt = -1;
                    indEscrito = false;
                    largoLineaCentroAux = 0;
                    largoAcumuladoAux = 0;
                    for (long idLineaAux : this.mapaRedesLineasDet.keySet()) {
                        //Se obtienen los datos de la línea actual
                        if (idLineaAux != idLineaAnt) {
                            redLineaAux = this.mapaRedesLineas.get(idLineaAux);
                            largoLineaCentroAux = redLineaAux.getLargoLinea() / 2;
                            largoAcumuladoAux = 0;
                            indEscrito = false;
                        }
                        
                        ArrayList<RedLineaDet> listaRedesLineasDetAux = this.mapaRedesLineasDet.get(idLineaAux);
                        double latitudAnt = Double.NaN;
                        double longitudAnt = Double.NaN;
                        for (RedLineaDet redLineaDetAux : listaRedesLineasDetAux) {
                            largoAcumuladoAux += redLineaDetAux.getLargoSegmento();
                            
                            if (!Double.isNaN(latitudAnt)) {
                                double latitudAct = redLineaDetAux.getLatitud();
                                double longitudAct = redLineaDetAux.getLongitud();
                                
                                //Color y ancho de línea
                                g.setColor(Color.BLACK);
                                g2d.setStroke(new BasicStroke(1));
                                
                                //Se hallan las coordenadas iniciales y finales del segmento
                                int xIniAux = Integer.parseInt("" + Math.round((longitudAnt - this.longitudMed) * factorEscala + this.xCentro));
                                int yIniAux = Integer.parseInt("" + Math.round((latitudAnt - this.latitudMed) * -factorEscala + this.yCentro));
                                int xFinAux = Integer.parseInt("" + Math.round((longitudAct - this.longitudMed) * factorEscala + this.xCentro));
                                int yFinAux = Integer.parseInt("" + Math.round((latitudAct - this.latitudMed) * -factorEscala + this.yCentro));
                                
                                g.drawLine(xIniAux, yIniAux, xFinAux, yFinAux);
                                
                                //Se verifica si hay que agregar etiquetas y si ya se pasó del centro de la línea
                                if (this.indVerAtributos && largoAcumuladoAux >= largoLineaCentroAux && indZoomEtiquetas && !indEscrito) {
                                    double factorDistAux = 1 - (largoAcumuladoAux - largoLineaCentroAux) / redLineaDetAux.getLargoSegmento();
                                    double[] arrCoordTexto = this.hallarCoordenadasTexto(xIniAux, yIniAux, xFinAux, yFinAux, factorDistAux);
                                    
                                    //Se escribe el valor del atributo en el gráfico
                                    String valorAux = "";
                                    RedLineaAtributo redLineaAtributoAux = this.mapaRedesLineasAtributos.get(idLineaAux);
                                    if (redLineaAtributoAux != null) {
                                        switch (this.redAtributo.getTipoAtributo().toLowerCase()) {
                                            case "int":
                                            case "uint":
                                            case "short":
                                            case "ushort":
                                            case "float":
                                            case "double":
                                                valorAux = this.mapaRedesLineasAtributos.get(idLineaAux).getValorNum() + "";
                                                break;
                                            default:
                                                valorAux = this.mapaRedesLineasAtributos.get(idLineaAux).getValorTex();
                                                break;
                                        }
                                    }
                                    
                                    g.drawString(valorAux, (int)arrCoordTexto[0], (int)arrCoordTexto[1] + 4);
                                    
                                    indEscrito = true;
                                }
                            }
                            
                            latitudAnt = redLineaDetAux.getLatitud();
                            longitudAnt = redLineaDetAux.getLongitud();
                        }
                        
                        idLineaAnt = idLineaAux;
                    }
                    
                    if (this.tipoVista.equals("E")) {
                        //Se define el color de los puntos
                        g.setColor(new Color(100, 0, 0));
                        
                        //Se recorren los eventos y se pintan
                        for (long idPuntoAux : this.mapaEventosPuntos.keySet()) {
                            EventoPunto eventoPuntoAux = this.mapaEventosPuntos.get(idPuntoAux);
                            
                            double latitudAux;
                            double longitudAux;
                            if (this.evento.getIndProy() == 1) {
                                latitudAux = eventoPuntoAux.getLatitudProy();
                                longitudAux = eventoPuntoAux.getLongitudProy();
                                
                                if (latitudAux == 0 && longitudAux == 0) {
                                    latitudAux = eventoPuntoAux.getLatitud();
                                    longitudAux = eventoPuntoAux.getLongitud();
                                }
                            } else {
                                latitudAux = eventoPuntoAux.getLatitud();
                                longitudAux = eventoPuntoAux.getLongitud();
                            }
                            
                            //Se hallan las coordenadas del punto
                            int xPuntoAux = Integer.parseInt("" + Math.round((longitudAux - this.longitudMed) * factorEscala + this.xCentro));
                            int yPuntoAux = Integer.parseInt("" + Math.round((latitudAux - this.latitudMed) * -factorEscala + this.yCentro));
                            
                            g.fillOval(xPuntoAux - 3, yPuntoAux - 3, 6, 6);
                        }
                    }
                    break;
                    
                case "N": //NetKDE
                    //Se halla el valor del percentil para resalte
                    CalculadorDistribucion cd = new CalculadorDistribucion();
                    double valResalte = cd.obtenerPercentil(this.arrValoresOrdenados, 100 - this.porcResalte);
                    
                    //Se recorren los detalles de los lixels para resaltar los de mayor valor
                    for (String llaveAux : this.mapaNetKDELixelsDet.keySet()) {
                        ArrayList<NetKDELixelDet> listaNetKDELixelDetAux = this.mapaNetKDELixelsDet.get(llaveAux);
                        double latitudAnt = Double.NaN;
                        double longitudAnt = Double.NaN;
                        for (NetKDELixelDet netKDELixelDetAux : listaNetKDELixelDetAux) {
                            if (!Double.isNaN(latitudAnt)) {
                                double latitudAct = netKDELixelDetAux.getLatitud();
                                double longitudAct = netKDELixelDetAux.getLongitud();
                                
                                //Se verifica si hay que resaltar el lixel
                                double densidadLixelAux = this.mapaNetKDELixels.get(netKDELixelDetAux.getIdLixel()).getDensidadLixel();
                                if (densidadLixelAux >= valResalte) {
                                    //Se selecciona el color que tendrá el segmento de línea
                                    Color colorAux = new Color(221, 153, 153);
                                    g.setColor(colorAux);
                                    
                                    //Se define el ancho de la línea
                                    g2d.setStroke(new BasicStroke(10));
                                    
                                    //Se hallan las coordenadas iniciales y finales del segmento
                                    int xIniAux = Integer.parseInt("" + Math.round((longitudAnt - this.longitudMed) * factorEscala + this.xCentro));
                                    int yIniAux = Integer.parseInt("" + Math.round((latitudAnt - this.latitudMed) * -factorEscala + this.yCentro));
                                    int xFinAux = Integer.parseInt("" + Math.round((longitudAct - this.longitudMed) * factorEscala + this.xCentro));
                                    int yFinAux = Integer.parseInt("" + Math.round((latitudAct - this.latitudMed) * -factorEscala + this.yCentro));
                                    
                                    g.drawLine(xIniAux, yIniAux, xFinAux, yFinAux);
                                }
                            }
                            
                            latitudAnt = netKDELixelDetAux.getLatitud();
                            longitudAnt = netKDELixelDetAux.getLongitud();
                        }
                    }
                    
                    //Se recorren los detalles de los lixels para crear las líneas
                    idLineaAnt = -1;
                    long idLineaAux;
                    indEscrito = false;
                    largoLineaCentroAux = 0;
                    largoAcumuladoAux = 0;
                    for (String llaveAux : this.mapaNetKDELixelsDet.keySet()) {
                        String[] arrAux = llaveAux.split("-");
                        idLineaAux = Long.parseLong(arrAux[0]);
                        
                        //Se obtienen los datos de la línea actual
                        if (idLineaAux != idLineaAnt) {
                            redLineaAux = this.mapaRedesLineas.get(idLineaAux);
                            largoLineaCentroAux = redLineaAux.getLargoLinea() / 2;
                            largoAcumuladoAux = 0;
                            indEscrito = false;
                        }
                        
                        ArrayList<NetKDELixelDet> listaNetKDELixelDetAux = this.mapaNetKDELixelsDet.get(llaveAux);
                        double latitudAnt = Double.NaN;
                        double longitudAnt = Double.NaN;
                        for (NetKDELixelDet netKDELixelDetAux : listaNetKDELixelDetAux) {
                            largoAcumuladoAux += netKDELixelDetAux.getLargoSegmento();
                            
                            if (!Double.isNaN(latitudAnt)) {
                                double latitudAct = netKDELixelDetAux.getLatitud();
                                double longitudAct = netKDELixelDetAux.getLongitud();
                                
                                //Se busca el decil en el que se encuentra el valor de desidad para seleccionar el color
                                double densidadLixelAux = this.mapaNetKDELixels.get(netKDELixelDetAux.getIdLixel()).getDensidadLixel();
                                int indiceDecil = -1;
                                if (densidadLixelAux != 0) {
                                    if (densidadLixelAux > this.arrDeciles[this.arrDeciles.length - 1]) {
                                        indiceDecil = this.arrDeciles.length;
                                    } else {
                                        for (int i = 0; i < this.arrDeciles.length; i++) {
                                            double decilAux = this.arrDeciles[i];

                                            if (densidadLixelAux <= decilAux) {
                                                indiceDecil = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                
                                //Se selecciona el color que tendrá el segmento de línea
                                Color colorSegmento = Color.BLACK;
                                if (indiceDecil >= 0 && indiceDecil < this.arrColores.length) {
                                    colorSegmento = this.arrColores[indiceDecil];
                                }
                                g.setColor(colorSegmento);
                                
                                //Se define el ancho de la línea
                                g2d.setStroke(new BasicStroke(1));
                                if (this.factorZoom > 2) {
                                    if (indiceDecil >= 8) {
                                        g2d.setStroke(new BasicStroke(2));
                                    }
                                }
                                
                                //Se hallan las coordenadas iniciales y finales del segmento
                                int xIniAux = Integer.parseInt("" + Math.round((longitudAnt - this.longitudMed) * factorEscala + this.xCentro));
                                int yIniAux = Integer.parseInt("" + Math.round((latitudAnt - this.latitudMed) * -factorEscala + this.yCentro));
                                int xFinAux = Integer.parseInt("" + Math.round((longitudAct - this.longitudMed) * factorEscala + this.xCentro));
                                int yFinAux = Integer.parseInt("" + Math.round((latitudAct - this.latitudMed) * -factorEscala + this.yCentro));
                                
                                g.drawLine(xIniAux, yIniAux, xFinAux, yFinAux);
                                
                                //Se verifica si hay que agregar etiquetas y si ya se pasó del centro de la línea
                                if (this.indVerAtributos && largoAcumuladoAux >= largoLineaCentroAux && indZoomEtiquetas && !indEscrito) {
                                    double factorDistAux = 1 - (largoAcumuladoAux - largoLineaCentroAux) / netKDELixelDetAux.getLargoSegmento();
                                    double[] arrCoordTexto = this.hallarCoordenadasTexto(xIniAux, yIniAux, xFinAux, yFinAux, factorDistAux);
                                    
                                    //Se escribe el valor del atributo en el gráfico
                                    String valorAux = "";
                                    RedLineaAtributo redLineaAtributoAux = this.mapaRedesLineasAtributos.get(idLineaAux);
                                    if (redLineaAtributoAux != null) {
                                        switch (this.redAtributo.getTipoAtributo().toLowerCase()) {
                                            case "int":
                                            case "uint":
                                            case "short":
                                            case "ushort":
                                            case "float":
                                            case "double":
                                                valorAux = this.mapaRedesLineasAtributos.get(idLineaAux).getValorNum() + "";
                                                break;
                                            default:
                                                valorAux = this.mapaRedesLineasAtributos.get(idLineaAux).getValorTex();
                                                break;
                                        }
                                    }
                                    g.drawString(valorAux, (int)arrCoordTexto[0], (int)arrCoordTexto[1] + 4);
                                    
                                    indEscrito = true;
                                }
                            }
                            
                            latitudAnt = netKDELixelDetAux.getLatitud();
                            longitudAnt = netKDELixelDetAux.getLongitud();
                        }
                        
                        idLineaAnt = idLineaAux;
                    }
                    
                    //Se agrega la leyenda
                    if (this.indVerLeyenda) {
                        this.pintarLeyenda(g2d);
                    }
                    break;
            }
            
            //Se agrega la escala
            this.pintarEscala(g2d, factorEscala);
        }
    }
    
    private double[] hallarCoordenadasTexto(int xIni, int yIni, int xFin, int yFin, double factorDist) {
        //Se halla el punto base de acuerdo al fator de distancia
        double xBase = xIni + (xFin - xIni) * factorDist;
        double yBase = yIni + (yFin - yIni) * factorDist;
        
        double distAux = 7;
        if (xIni == xFin) {
            //Línea vertical
            xBase += distAux;
        } else if (yIni == yFin) {
            //Línea horizontal
            yBase -= distAux;
        } else {
            double pendienteAux = -(float)(xFin - xIni) / (float)(yFin - yIni);
            double valorAux = distAux * Math.sqrt(1 / (Math.pow(pendienteAux, 2) + 1));
            xBase += valorAux;
            yBase += pendienteAux * valorAux;
        }
        
        double[] arrCoordenadas = {xBase, yBase};
        return arrCoordenadas;
    }
    
    private void pintarEscala(Graphics2D g, double factorEscala) {
        //Se halla el valor en metros de un pixel en el lienzo
        double valorBase = Utilidades.calcularDistanciaPuntos(this.latitudMed, this.longitudMed, this.latitudMed, this.longitudMed + 1, this.unidadMedida, 8) / factorEscala;
        
        //La línea de escala tendrá un valor máximo de 200 pixeles
        int valorBaseEscala = (int)Math.floor(valorBase * 200);
        int cantUnidades = (valorBaseEscala + "").length();
        int valorEscala = Math.floorDiv(valorBaseEscala, (int)Math.pow(10, cantUnidades - 1));
        switch (valorEscala) {
            case 3:
            case 4:
                valorEscala = 2;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                valorEscala = 5;
                break;
        }
        valorEscala *= (int)Math.pow(10, cantUnidades - 1);
        
        int largoLinea = valorEscala * 200 / valorBaseEscala;
        
        //Se agrega el rectángulo que contendrá la escala
        g.setColor(new Color(1, 1, 1, 0.8f));
        g.fillRect((int)this.anchoLienzo - largoLinea - 15, (int)this.altoLienzo - 30, largoLinea + 10, 25);
        
        //Se pinta la línea de escala
        g.setColor(Color.GRAY);
        g.drawLine((int)this.anchoLienzo - largoLinea - 10, (int)this.altoLienzo - 20, (int)this.anchoLienzo - 10, (int)this.altoLienzo - 20);
        g.drawLine((int)this.anchoLienzo - largoLinea - 10, (int)this.altoLienzo - 20, (int)this.anchoLienzo - largoLinea - 10, (int)this.altoLienzo - 25);
        g.drawLine((int)this.anchoLienzo - 10, (int)this.altoLienzo - 20, (int)this.anchoLienzo - 10, (int)this.altoLienzo - 25);
        
        //Se agrega el texto de la escala
        String textoEscala = valorEscala + " m";
        if (valorEscala >= 1000) {
            textoEscala = (valorEscala / 1000) + " km";
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        g.drawString(textoEscala, (int)this.anchoLienzo - (largoLinea / 2) - (textoEscala.length() * 3) - 10, (int)this.altoLienzo - 9);
    }
    
    private void pintarLeyenda(Graphics2D g) {
        //Se definen las coordenadas del rectángulo
        int xBase = (int)this.anchoLienzo - 155;
        int yBase = 5;
        int anchoBase = 150;
        int altoBase = 250;
        
        //Se crea el rectángulo contenedor
        g.setColor(Color.WHITE);
        g.fillRect(xBase, yBase, anchoBase, altoBase);
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        g.drawRect(xBase, yBase, anchoBase, altoBase);
        g.setStroke(new BasicStroke(1));
        
        //Título
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g.drawString("NetKDE Densities", xBase + 10, yBase + 15);
        
        //Se agregan los rectángulos de colores
        for (int i = this.arrColores.length - 1; i >= 0 ; i--) {
            Color colorAux = this.arrColores[i];
            
            //Se agrega el rectángulo del color
            g.setColor(colorAux);
            g.fillRect(xBase + 10, yBase + 20 + (9 - i) * 20, 40, 20);
            g.setColor(Color.BLACK);
            g.drawRect(xBase + 10, yBase + 20 + (9 - i) * 20, 40, 20);
            
            //Se agrega el texto del rango
            String textoAux;
            if (i < this.arrDeciles.length) {
                double valorAux1 = 0;
                if (i > 0) {
                    valorAux1 = Math.round(this.arrDeciles[i - 1] * 1000) / 1000.0;
                }
                double valorAux2 = Math.round(this.arrDeciles[i] * 1000) / 1000.0;
                textoAux = "(" + valorAux1 + ", " + valorAux2 + "]";
            } else {
                double valorAux1 = Math.round(this.arrDeciles[i - 1] * 1000) / 1000.0;
                textoAux = "(" + valorAux1 + ", ∞)";
            }
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawString(textoAux, xBase + 55, yBase + 34 + (9 - i) * 20);
        }
        
        //Se agrega el rectángulo de color negro
        g.setColor(Color.BLACK);
        g.fillRect(xBase + 10, yBase + 20 + 200, 40, 20);
        g.drawRect(xBase + 10, yBase + 20 + 200, 40, 20);
        
        //Se agrega el texto del cero
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("0", xBase + 55, yBase + 34 + 200);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        this.imagenFondo = null;
        
        this.iniciarClic(e);
        
        if (this.visor2 != null) {
            this.visor2.iniciarClic(e);
        }
    }
    
    private void iniciarClic(MouseEvent e) {
        this.xIni = e.getX();
        this.yIni = e.getY();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        this.soltarClic(e);
        
        if (this.visor2 != null) {
            this.visor2.soltarClic(e);
        } 
    }
    
    private void soltarClic(MouseEvent e) {
        this.isDragging = false;
        this.xFin = e.getX();
        this.yFin = e.getY();
        
        //Se calculan las coordenadas del nuevo centro del gráfico
        this.xCentro += (this.xFin - this.xIni);
        this.yCentro += (this.yFin - this.yIni);
        
        //Se pinta de nuevo el gráfico
        this.repaint();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.imagenFondo == null) {
            //Se almacena la gráfica como imagen para el efecto drag and drop
            this.imagenFondo = new BufferedImage((int)this.anchoLienzo, (int)this.altoLienzo, BufferedImage.TYPE_INT_RGB);
            Graphics2D gAux = this.imagenFondo.createGraphics();
            this.paintAll(gAux);
        }
        
        this.moverRaton(e);
        
        if (this.visor2 != null) {
            this.visor2.moverRaton(e);
        }
        
        //Se pinta el gráfico al ser arrastrado
        this.repaint();
    }
    
    private void moverRaton(MouseEvent e) {
        this.isDragging = true;
        this.xPar = e.getX();
        this.yPar = e.getY();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.hacerZoom(e);
        
        if (this.visor2 != null) {
            this.visor2.hacerZoom(e);
        }
    }
    
    private void hacerZoom(MouseWheelEvent e) {
        this.xZoom = e.getX();
        this.yZoom = e.getY();
        
        //Se halla el nuevo factor de zoom y se recalculan las coordenadas del centro del gráfico
        boolean hacerZoom = false;
        int rotacion = e.getWheelRotation() * -1;
        if (rotacion < 0) {
            if ((this.factorZoom / (-rotacion + 1)) >= 0.0009765625) {
                this.factorZoom /= (-rotacion + 1);
                this.xCentro += (this.xZoom - this.xCentro) / 2.0;
                this.yCentro += (this.yZoom - this.yCentro) / 2.0;
                hacerZoom = true;
            }
        } else if (rotacion > 0) {
            if ((this.factorZoom * (rotacion + 1)) <= 1024) {
                this.factorZoom *= (rotacion + 1);
                this.xCentro -= (this.xZoom - this.xCentro);
                this.yCentro -= (this.yZoom - this.yCentro);
                hacerZoom = true;
            }
        }
        
        //Se pinta el gráfico con el nuevo zoom
        if (hacerZoom) {
            this.repaint();
        }
    }

    
    public boolean isIndVerLeyenda() {
        return indVerLeyenda;
    }

    private void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) {
        g2d.translate((float)x,(float)y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float)x, -(float)y);
    }
    
    public void setIndVerLeyenda(boolean indVerLeyenda) {    
        this.indVerLeyenda = indVerLeyenda;
    }
    
    public double getXCentro() {
        return xCentro;
    }
    
    public void setXCentro(double xCentro) {
        this.xCentro = xCentro;
    }
    
    public double getYCentro() {
        return yCentro;
    }
    
    public void setYCentro(double yCentro) {
        this.yCentro = yCentro;
    }
    
    public double getFactorZoom() {
        return factorZoom;
    }
    
    public void setFactorZoom(double factorZoom) {
        this.factorZoom = factorZoom;
    }
    
    public void setIndVerAtributos(boolean indVerAtributos) {
        this.indVerAtributos = indVerAtributos;
    }
    
    public void setRedAtributo(RedAtributo redAtributo) {
        this.redAtributo = redAtributo;
    }
    
    public void setPorcResalte(int porcResalte) {
        this.porcResalte = porcResalte;
    }
    
    public void setVisor2(VisorRed visor2) {
        this.visor2 = visor2;
    }
}
