package co.edu.unab.procesos;

import co.edu.unab.db.*;
import co.edu.unab.entidad.*;
import co.edu.unab.utilidades.Utilidades;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * Clase para el cálculo de la función K para redes
 * @author Feisar Moreno
 * @date 17/04/2016
 */
public class PrCalculoKNet {
    private final long idRed;
    private final long idEvento;
    private final LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros;
    private final double distanciaIniKNet;
    private final double distanciaFinKNet;
    private final double incrementoKNet;
    private final int cantAleatorios;
    private boolean indInicioCalculo;
    private int contAleatoriosGeneracion;
    private boolean indCalculoReal;
    private int contAleatoriosCalculo;
    
    /**
     * Constructor de la clase
     * @param evento Objeto que representa el evento
     * @param mapaFiltros LinkedHashMap con los filtros a aplicar
     * @param distanciaIniKNet Distancia inicial de cálculo en metros
     * @param distanciaFinKNet Distancia final de cálculo en metros
     * @param incrementoKNet Incremento de distancia en metros
     * @param cantAleatorios Cantidad de grupos de datos aleatorios a generar
     */
    public PrCalculoKNet(Evento evento, LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros, double distanciaIniKNet, double distanciaFinKNet, double incrementoKNet, int cantAleatorios) {
        this.idRed = evento.getRed().getIdRed();
        this.idEvento = evento.getIdEvento();
        this.mapaFiltros = mapaFiltros;
        this.distanciaIniKNet = distanciaIniKNet;
        this.distanciaFinKNet = distanciaFinKNet;
        this.incrementoKNet = incrementoKNet;
        this.cantAleatorios = cantAleatorios;
        this.indInicioCalculo = false;
        this.contAleatoriosGeneracion = 0;
        this.indCalculoReal = false;
        this.contAleatoriosCalculo = 0;
    }
    
    public long getIdEvento() {
        return this.idEvento;
    }
    
    public int getCantAleatorios() {
        return cantAleatorios;
    }
    
    public boolean isIndInicioCalculo() {
        return indInicioCalculo;
    }
    
    public int getContAleatoriosGeneracion() {
        return contAleatoriosGeneracion;
    }
    
    public boolean isIndCalculoReal() {
        return indCalculoReal;
    }
    
    public int getContAleatoriosCalculo() {
        return contAleatoriosCalculo;
    }
    
    /**
     * Método que realiza el cálculo
     * @return Identificador del registro de resultados de la función K para redes.
     */
    public long calcularKNet() {
        this.indInicioCalculo = true;
        
        DbRedes dbRedes = new DbRedes();
        DbEventos dbEventos = new DbEventos();
        
        //Se obtienen los datos de la red
        Red red = dbRedes.getRed(false, this.idRed);
        
        //Se obtienen las líneas de la red
        ArrayList<RedLinea> listaRedesLineas = dbRedes.getListaRedesLineas(false, this.idRed);
        
        //Se obtienen el detalle de las líneas de la red
        ArrayList<RedLineaDet> listaRedesLineasDet = dbRedes.getListaRedesLineasDet(false, this.idRed);
        LinkedHashMap<Long, LinkedHashMap<Long, RedLineaDet>> mapaRedesLineasDet = new LinkedHashMap<>();
        for (RedLineaDet redLineaDetAux : listaRedesLineasDet) {
            if (!mapaRedesLineasDet.containsKey(redLineaDetAux.getIdLinea())) {
                mapaRedesLineasDet.put(redLineaDetAux.getIdLinea(), new LinkedHashMap<Long, RedLineaDet>());
            }
            mapaRedesLineasDet.get(redLineaDetAux.getIdLinea()).put(redLineaDetAux.getNumPunto(), redLineaDetAux);
        }
        
        //Se obtiene el listado de nodos de red
        ArrayList<RedLineaDet> listaNodosRed = dbRedes.getListaRedesLineasDetNodos(false, this.idRed);
        LinkedHashMap<String, ArrayList<RedLineaDet>> mapaNodosRed = new LinkedHashMap<>();
        for (RedLineaDet redLineaDetAux : listaNodosRed) {
            String llaveAux = redLineaDetAux.getIdLinea() + "-" + redLineaDetAux.getNumPunto();
            if (!mapaNodosRed.containsKey(llaveAux)) {
                mapaNodosRed.put(llaveAux, new ArrayList<RedLineaDet>());
            }
            mapaNodosRed.get(llaveAux).add(redLineaDetAux);
        }
        
        //Se obtiene el listado de puntos del evento
        ArrayList<EventoPunto> listaEventosPuntos = dbEventos.getListaEventosPuntosFiltros(false, this.idEvento, this.mapaFiltros);
        LinkedHashMap<Long, EventoPunto> mapaPuntosEvento = new LinkedHashMap<>();
        for (EventoPunto eventoPuntoAux : listaEventosPuntos) {
            mapaPuntosEvento.put(eventoPuntoAux.getIdPunto(), eventoPuntoAux);
        }
        
        //Se crean los grupos de datos aleatorios
        ArrayList<LinkedHashMap<Long, EventoPunto>> listaMapasPuntosAleatorios = new ArrayList<>();
        for (int i = 0; i < this.cantAleatorios; i++) {
            this.contAleatoriosGeneracion++;
            LinkedHashMap<Long, EventoPunto> mapaEventosPuntosAux = new LinkedHashMap<>();
            //Para cada grupo se genera un número de puntos igual al del evento real
            for (int j = 0; j < listaEventosPuntos.size(); j++) {
                double numAux = this.generarAleatorio() * red.getLargoRed();
                
                //Se busca la línea sobre la que se ubicará el punto
                long idLineaAux = listaRedesLineas.get(0).getIdLinea();
                double largoAnt = 0;
                for (RedLinea redLineaAux : listaRedesLineas) {
                    if (redLineaAux.getLargoAcumulado() >= numAux) {
                        idLineaAux = redLineaAux.getIdLinea();
                        break;
                    } else {
                        largoAnt = redLineaAux.getLargoAcumulado();
                    }
                }
                
                //Se busca el segmento de la línea sobre el que se encuentra el punto
                numAux -= largoAnt;
                LinkedHashMap<Long, RedLineaDet> mapaRedesLineasDetAux = mapaRedesLineasDet.get(idLineaAux);
                long numPuntoAux = 0;
                double latitudIniAux = 0;
                double longitudIniAux = 0;
                double latitudFinAux = 0;
                double longitudFinAux = 0;
                double largoAcumuladoIniAux = 0;
                double largoAcumuladoFinAux = 0;
                for (long numPuntoAux2 : mapaRedesLineasDetAux.keySet()) {
                    RedLineaDet redLineaDetAux = mapaRedesLineasDetAux.get(numPuntoAux2);
                    largoAcumuladoFinAux += redLineaDetAux.getLargoSegmento();
                    if (largoAcumuladoFinAux <= numAux) {
                        largoAcumuladoIniAux += redLineaDetAux.getLargoSegmento();
                        numPuntoAux = redLineaDetAux.getNumPunto();
                        latitudIniAux = redLineaDetAux.getLatitud();
                        longitudIniAux = redLineaDetAux.getLongitud();
                    } else {
                        latitudFinAux = redLineaDetAux.getLatitud();
                        longitudFinAux = redLineaDetAux.getLongitud();
                        break;
                    }
                }
                
                //Se hallan las coordenadas del punto aleatorio generado
                numAux -= largoAcumuladoIniAux;
                double factorAux = numAux / (largoAcumuladoFinAux - largoAcumuladoIniAux);
                double latitudAux = latitudIniAux + (latitudFinAux - latitudIniAux) * factorAux;
                double longitudAux = longitudIniAux + (longitudFinAux - longitudIniAux) * factorAux;
                
                //Se crea el evento
                EventoPunto eventoPuntoAux = new EventoPunto(i + 1, j + 1, "", latitudAux, longitudAux, latitudAux, longitudAux, this.idRed, idLineaAux, numPuntoAux);
                mapaEventosPuntosAux.put(new Long(j + 1), eventoPuntoAux);
            }
            
            listaMapasPuntosAleatorios.add(mapaEventosPuntosAux);
        }
        
        //Se crea el registro temporal de resultados
        String cadenFiltrosAux = Utilidades.mapaFiltrosACadena(this.mapaFiltros);
        DbKNet dbKNet = new DbKNet();
        long idKNetTmp = dbKNet.crearTmpKNetResultado(false, this.idEvento, this.distanciaIniKNet, this.distanciaFinKNet, this.incrementoKNet, listaEventosPuntos.size(), this.cantAleatorios, cadenFiltrosAux);
        
        //Se hallan los valores KNet para el evento
        this.indCalculoReal = true;
        LinkedHashMap<Double, Double> mapaValoresKNet = this.calcularKNetEvento(red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosEvento);
        
        //Se hallan los valores KNet para los grupos de puntos aleatorios
        ArrayList<LinkedHashMap<Double, Double>> listaMapasValoresAleatorios = new ArrayList<>();
        for (LinkedHashMap<Long, EventoPunto> mapaPuntosAleatoriosAux : listaMapasPuntosAleatorios) {
            this.contAleatoriosCalculo++;
            LinkedHashMap<Double, Double> mapaValoresKNetAux = this.calcularKNetEvento(red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosAleatoriosAux);
            listaMapasValoresAleatorios.add(mapaValoresKNetAux);
        }
        
        //Se ordenan los grupos de valores obtenidos para hallar los límites
        LinkedHashMap<Double, ArrayList<Double>> mapaListasValoresAleatorios = new LinkedHashMap<>();
        for (double distanciaKNetAux = this.distanciaIniKNet; distanciaKNetAux <= this.distanciaFinKNet; distanciaKNetAux += this.incrementoKNet) {
            ArrayList<Double> listaValoresAux = new ArrayList<>();
            for (LinkedHashMap<Double, Double> mapaValoresAleatoriosAux : listaMapasValoresAleatorios) {
                if (listaValoresAux.isEmpty() && mapaListasValoresAleatorios.containsKey(distanciaKNetAux)) {
                    listaValoresAux = mapaListasValoresAleatorios.get(distanciaKNetAux);
                }
                
                int indiceAux = listaValoresAux.size();
                for (int i = 0; i < listaValoresAux.size(); i++) {
                    if (mapaValoresAleatoriosAux.get(distanciaKNetAux) < listaValoresAux.get(i)) {
                        indiceAux = i;
                        break;
                    }
                }
                listaValoresAux.add(indiceAux, mapaValoresAleatoriosAux.get(distanciaKNetAux));
            }
            mapaListasValoresAleatorios.put(distanciaKNetAux, listaValoresAux);
        }
        
        //Se calculan los valores de los índices de lis límites
        int indiceMin = new Double(Math.ceil(2.5 * this.cantAleatorios / 100)).intValue() - 1;
        int indiceMax = new Double(Math.ceil(97.5 * this.cantAleatorios / 100)).intValue() - 1;
        
        //Se insertan los valores calculados sobre el grupo de datos reales a la tabla temporal de valores
        for (double distanciaKNetAux = this.distanciaIniKNet; distanciaKNetAux <= this.distanciaFinKNet; distanciaKNetAux += this.incrementoKNet) {
            //Se inserta el valor del set de datos reales
            double limiteMinAux = mapaListasValoresAleatorios.get(distanciaKNetAux).get(indiceMin);
            double limiteMaxAux = mapaListasValoresAleatorios.get(distanciaKNetAux).get(indiceMax);
            boolean resultadoAux = dbKNet.crearTmpKNetValores(false, idKNetTmp, distanciaKNetAux, mapaValoresKNet.get(distanciaKNetAux), limiteMinAux, limiteMaxAux);
            if (!resultadoAux) {
                return -1;
            }
        }
        
        //Se mueven los datos a la tabla de resultados
        long idKNet = dbKNet.crearKNetResultado(false, idKNetTmp);
        
        return idKNet;
    }
    
    private double generarAleatorio() {
        Random rand = new Random(System.nanoTime());
        
        return rand.nextDouble();
    }
    
    /**
     * Método que realiza el cálculo KNet para uno de los eventos
     * @param red Objeto que representa la red
     * @param mapaRedesLineasDet Mapa que contiene mapas de segmentos por línea
     * @param mapaNodosRed Listado de nodos que conforman la red
     * @param mapaPuntosEvento <code>LinkedHashMap</code> con los puntos del evento
     * @return Valor de la función K para redes del conjunto de datos recibido.
     */
    private LinkedHashMap<Double, Double> calcularKNetEvento(Red red, LinkedHashMap<Long, LinkedHashMap<Long, RedLineaDet>> mapaRedesLineasDet, LinkedHashMap<String, ArrayList<RedLineaDet>> mapaNodosRed, LinkedHashMap<Long, EventoPunto> mapaPuntosEvento) {
        //Mapa que contendrá los resultados
        LinkedHashMap<Double, Double> mapaValoresKNet = new LinkedHashMap<>();
        
        //Factor de densidad por el que se multiplicarán las sumatorias de distancias
        double factorKNet = red.getLargoRed() / Math.pow(mapaPuntosEvento.size(), 2);
        
        //Se crea un mapa de puntos agrupados por segmento para hacer más rápidas las búsquedas posteriores
        LinkedHashMap<String, ArrayList<EventoPunto>> mapaPuntosSegmento = new LinkedHashMap<>();
        for (long idPuntoAux : mapaPuntosEvento.keySet()) {
            EventoPunto eventoPuntoAux = mapaPuntosEvento.get(idPuntoAux);
            String llaveAux = eventoPuntoAux.getIdLinea() + "-" + eventoPuntoAux.getNumPunto();
            if (!mapaPuntosSegmento.containsKey(llaveAux)) {
                mapaPuntosSegmento.put(llaveAux, new ArrayList<EventoPunto>());
            }
            mapaPuntosSegmento.get(llaveAux).add(eventoPuntoAux);
        }
        
        //Mapa que contendrá todos los puntos con el listado de puntos dentro de las distancias de búsqueda
        LinkedHashMap<Long, LinkedHashMap<Long, EventoPunto>> mapaPuntosKNet = new LinkedHashMap<>();
        
        //Se recorre el listado de puntos
        for (long idPuntoAux : mapaPuntosEvento.keySet()) {
            EventoPunto eventoPuntoAux = mapaPuntosEvento.get(idPuntoAux);
            
            //Mapa que contendrá los puntos de la red que se encuentran dentro de la distancia dada
            LinkedHashMap<Long, EventoPunto> mapaPuntosKNetAux = new LinkedHashMap<>();
            
            //Se obtienen los puntos que se encuentran en el mismo segmento
            ArrayList<EventoPunto> listaEventosPuntosAux = mapaPuntosSegmento.get(eventoPuntoAux.getIdLinea() + "-" + eventoPuntoAux.getNumPunto());
            if (listaEventosPuntosAux != null) {
                for (EventoPunto eventoPuntoRel : listaEventosPuntosAux) {
                    //No se compara el punto consigo mismo
                    if (eventoPuntoAux.getIdPunto() != eventoPuntoRel.getIdPunto()) {
                        //Se halla la distancia hasta el punto
                        double distanciaAux = Utilidades.calcularDistanciaPuntos(eventoPuntoAux.getLatitudProy(), eventoPuntoAux.getLongitudProy(), eventoPuntoRel.getLatitudProy(), eventoPuntoRel.getLongitudProy(), red.getSistemaCoordenadas().getUnidadMedida());
                        if (distanciaAux <= this.distanciaFinKNet) {
                            //El punto se halla dentro de la distancia definida
                            eventoPuntoRel.setDistanciaRed(distanciaAux);
                            mapaPuntosKNetAux.put(eventoPuntoRel.getIdPunto(), eventoPuntoRel);
                        }
                    }
                }
            }
            
            //Se hallan los nodos adyacentes al inicio del segmento
            ArrayList<RedLineaDet> listaNodosRed1 = mapaNodosRed.get(eventoPuntoAux.getIdLinea() + "-" + eventoPuntoAux.getNumPunto());
            if (listaNodosRed1 != null && listaNodosRed1.size() > 0) {
                //Se halla la distancia del punto al inicio del segmento
                double distanciaAux = Utilidades.calcularDistanciaPuntos(eventoPuntoAux.getLatitudProy(), eventoPuntoAux.getLongitudProy(), listaNodosRed1.get(0).getLatitud(), listaNodosRed1.get(0).getLongitud(), red.getSistemaCoordenadas().getUnidadMedida());
                
                //Se hallan los puntos dentro de la distancia de red partiendo del inicio del segmento
                for (RedLineaDet redLineaDetAux : listaNodosRed1) {
                    this.calcularKNetEventoRec(mapaPuntosKNetAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    if (redLineaDetAux.getNumPunto2() > 1) {
                        //Segmento anterior
                        this.calcularKNetEventoRec(mapaPuntosKNetAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    }
                }
            }
            
            //Si el segmento inicial tiene un nodo anterior, también se incluye en la búsqueda
            if (eventoPuntoAux.getNumPunto() > 1) {
                //Se halla la distancia del segmento anterior
                if (mapaRedesLineasDet.containsKey(eventoPuntoAux.getIdLinea()) && mapaRedesLineasDet.get(eventoPuntoAux.getIdLinea()).containsKey(eventoPuntoAux.getNumPunto())) {
                    RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(eventoPuntoAux.getIdLinea()).get(eventoPuntoAux.getNumPunto());
                    double distanciaAux = Utilidades.calcularDistanciaPuntos(eventoPuntoAux.getLatitudProy(), eventoPuntoAux.getLongitudProy(), redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red.getSistemaCoordenadas().getUnidadMedida());
                    
                    this.calcularKNetEventoRec(mapaPuntosKNetAux, distanciaAux, redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                }
            }
            
            //Se hallan los nodos adyacentes al final del segmento
            ArrayList<RedLineaDet> listaNodosRed2 = mapaNodosRed.get(eventoPuntoAux.getIdLinea() + "-" + (eventoPuntoAux.getNumPunto() + 1));
            if (listaNodosRed2 != null && listaNodosRed2.size() > 0) {
                //Se halla la distancia del punto al final del segmento
                double distanciaAux = Utilidades.calcularDistanciaPuntos(eventoPuntoAux.getLatitudProy(), eventoPuntoAux.getLongitudProy(), listaNodosRed2.get(0).getLatitud(), listaNodosRed2.get(0).getLongitud(), red.getSistemaCoordenadas().getUnidadMedida());
                
                //Se hallan los puntos dentro de la distancia de red partiendo del inicio del segmento
                for (RedLineaDet redLineaDetAux : listaNodosRed2) {
                    this.calcularKNetEventoRec(mapaPuntosKNetAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    if (redLineaDetAux.getNumPunto2() > 1) {
                        //Segmento anterior
                        this.calcularKNetEventoRec(mapaPuntosKNetAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    }
                }
            }
            
            mapaPuntosKNet.put(idPuntoAux, mapaPuntosKNetAux);
        }
        
        //Se calculan los valores KNet para cada valor de distancia
        for (double distanciaKNetAux = this.distanciaIniKNet; distanciaKNetAux <= this.distanciaFinKNet; distanciaKNetAux += this.incrementoKNet) {
            //Se cuentan los puntos que se encuentran dentro de la distancia dada
            int cantPuntosDistancia = 0;
            for (long idPuntoAux : mapaPuntosKNet.keySet()) {
                LinkedHashMap<Long, EventoPunto> mapaPuntosKNetAux = mapaPuntosKNet.get(idPuntoAux);
                for (long idPuntoAux2 : mapaPuntosKNetAux.keySet()) {
                    EventoPunto eventoPuntoAux = mapaPuntosKNetAux.get(idPuntoAux2);
                    if (eventoPuntoAux.getDistanciaRed() <= distanciaKNetAux) {
                        cantPuntosDistancia++;
                    }
                }
            }
            
            double valorKNetAux = factorKNet * cantPuntosDistancia;
            mapaValoresKNet.put(distanciaKNetAux, valorKNetAux);
        }
        
        return mapaValoresKNet;
    }
    
    /**
     * Método recursivo que realiza el cálculo KNet para uno de los eventos
     * @param mapaPuntosKNet <code>LinkedHashMap</code> que contiene los puntos hallados hasta el momento con su respectiva distancia.
     * @param distanciaAcum Distancia acumulada anterior
     * @param idLinea Identificador de la línea
     * @param numPunto Número del segmento de línea
     * @param esAnterior Indica si se trata de un segmento anterior
     * @param latitud  Latitud del punto inicial del segmento
     * @param longitud Longitud inicial del punto del segmento
     * @param red Objeto que representa la red
     * @param mapaRedesLineasDet Mapa que contiene mapas de segmentos por línea
     * @param mapaNodosRed Listado de nodos que conforman la red
     * @param mapaPuntosSegmento <code>LinkedHashMap</code> con los puntos del evento.
     */
    private void calcularKNetEventoRec(LinkedHashMap<Long, EventoPunto> mapaPuntosKNet, double distanciaAcum, long idLinea, long numPunto, boolean esAnterior, double latitud, double longitud, Red red, LinkedHashMap<Long, LinkedHashMap<Long, RedLineaDet>> mapaRedesLineasDet, LinkedHashMap<String, ArrayList<RedLineaDet>> mapaNodosRed, LinkedHashMap<String, ArrayList<EventoPunto>> mapaPuntosSegmento) {
        //Se hallan los puntos que se encuentran dentro del segmento
        ArrayList<EventoPunto> listaPuntosSegmento = mapaPuntosSegmento.get(idLinea + "-" + numPunto);
        if (listaPuntosSegmento != null) {
            for (EventoPunto eventoPuntoAux : listaPuntosSegmento) {
                //Se halla la distancia hasta el punto
                double distanciaAux = distanciaAcum + Utilidades.calcularDistanciaPuntos(latitud, longitud, eventoPuntoAux.getLatitudProy(), eventoPuntoAux.getLongitudProy(), red.getSistemaCoordenadas().getUnidadMedida());
                if (distanciaAux <= this.distanciaFinKNet) {
                    //Se verifica si el punto ya fue agregado al mapa y si la distancia actual es mayor
                    if (!mapaPuntosKNet.containsKey(eventoPuntoAux.getIdPunto()) || mapaPuntosKNet.get(eventoPuntoAux.getIdPunto()).getDistanciaRed() > distanciaAux) {
                        eventoPuntoAux.setDistanciaRed(distanciaAux);
                        mapaPuntosKNet.put(eventoPuntoAux.getIdPunto(), eventoPuntoAux);
                    }
                }
            }
        }
        
        //Se halla la longitud del segmento
        String llaveAux;
        if (esAnterior) {
            llaveAux = idLinea + "-" + numPunto;
        } else {
            llaveAux = idLinea + "-" + (numPunto + 1);
        }
        
        //Se hallan los nodos que se conectan al extremo del nodo actual
        ArrayList<RedLineaDet> listaNodosRed = mapaNodosRed.get(llaveAux);
        
        if (listaNodosRed != null && listaNodosRed.size() > 0) {
            double distanciaSeg = 0;
            if (esAnterior) {
                //Se halla la longitud del segmento actual
                ArrayList<RedLineaDet> listaNodosRedAux = mapaNodosRed.get(idLinea + "-" + (numPunto + 1));
                if (listaNodosRedAux != null && listaNodosRedAux.size() > 0) {
                    distanciaSeg = listaNodosRedAux.get(0).getLargoSegmento();
                }
            } else {
                distanciaSeg = listaNodosRed.get(0).getLargoSegmento();
            }
            
            //Si no se ha superado la distancia de búsqueda, se continúa con los segmentos adyacentes
            if (this.distanciaFinKNet >= (distanciaAcum + distanciaSeg)) {
                //Se hallan los puntos dentro de la distancia de red dentro de los segmentos adyacentes
                for (RedLineaDet redLineaDetAux : listaNodosRed) {
                    this.calcularKNetEventoRec(mapaPuntosKNet, distanciaAcum + distanciaSeg, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    if (redLineaDetAux.getNumPunto2() > 1) {
                        //Segmento anterior
                        this.calcularKNetEventoRec(mapaPuntosKNet, distanciaAcum + distanciaSeg, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    }
                }
            }
        }
        
        if (esAnterior) {
            //Si el segmento tiene un nodo anterior, también se incluye en la búsqueda
            if (numPunto > 1) {
                //Se halla la distancia del segmento anterior
                if (mapaRedesLineasDet.containsKey(idLinea) && mapaRedesLineasDet.get(idLinea).containsKey(numPunto)) {
                    RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(idLinea).get(numPunto);
                    double distanciaAux = Utilidades.calcularDistanciaPuntos(latitud, longitud, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red.getSistemaCoordenadas().getUnidadMedida());
                    
                    if (this.distanciaFinKNet >= (distanciaAcum + distanciaAux)) {
                        this.calcularKNetEventoRec(mapaPuntosKNet, distanciaAcum + distanciaAux, redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                    }
                }
            }
        } else {
            //Si el segmento tiene un nodo siguiente, también se incluye en la búsqueda
            if (mapaRedesLineasDet.containsKey(idLinea) && mapaRedesLineasDet.get(idLinea).containsKey(numPunto + 1)) {
                RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(idLinea).get(numPunto + 1);
                
                if (this.distanciaFinKNet >= (distanciaAcum + redLineaDetAux.getLargoSegmento())) {
                    this.calcularKNetEventoRec(mapaPuntosKNet, distanciaAcum + redLineaDetAux.getLargoSegmento(), redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaPuntosSegmento);
                }
            }
        }
    }
}
