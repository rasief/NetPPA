package co.edu.unab.procesos;

import co.edu.unab.db.*;
import co.edu.unab.entidad.*;
import co.edu.unab.utilidades.Utilidades;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Clase para el cálculo del método NetKDE
 * @author Feisar Moreno
 * @date 09/06/2016
 */
public class PrCalculoNetKDE {
    private final long idRed;
    private final long idEvento;
    private final LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros;
    private final double anchoBanda;
    private final double largoLixel;
    private final int idFuncion;
    private final boolean indInicioCalculo;
    private long contLixels = 0;
    
    /**
     * Constructor de la clase
     * @param evento Objeto que representa el evento
     * @param mapaFiltros LinkedHashMap con los filtros a aplicar
     * @param anchoBanda Ancho de banda en metros
     * @param largoLixel Longitud de lixel en metros
     * @param idFuncion Identificador de la función de núcleo
     */
    public PrCalculoNetKDE(Evento evento, LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros, double anchoBanda, double largoLixel, int idFuncion) {
        this.idRed = evento.getRed().getIdRed();
        this.idEvento = evento.getIdEvento();
        this.mapaFiltros = mapaFiltros;
        this.anchoBanda = anchoBanda;
        this.largoLixel = largoLixel;
        this.idFuncion = idFuncion;
        this.indInicioCalculo = false;
    }
    
    public long getIdEvento() {
        return this.idEvento;
    }
    
    public boolean getIndInicioCalculo() {
        return this.indInicioCalculo;
    }
    
    /**
     * Método que realiza el cálculo
     * @return Identificador del registro de resultados del método NetKDE.
     */
    public long calcularNetKDE() {
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
        
        //Se obtiene el listado de puntos del evento
        ArrayList<EventoPunto> listaEventosPuntos = dbEventos.getListaEventosPuntosFiltros(false, this.idEvento, this.mapaFiltros);
        LinkedHashMap<Long, ArrayList<EventoPunto>> mapaPuntosEvento = new LinkedHashMap<>();
        for (EventoPunto eventoPuntoAux : listaEventosPuntos) {
            ArrayList<EventoPunto> listaEventosPuntosAux = mapaPuntosEvento.get(eventoPuntoAux.getIdLinea());
            if (listaEventosPuntosAux == null) {
                listaEventosPuntosAux = new ArrayList<>();
            }
            listaEventosPuntosAux.add(eventoPuntoAux);
            mapaPuntosEvento.put(eventoPuntoAux.getIdLinea(), listaEventosPuntosAux);
        }
        
        //Se crea el listado de lixels de la red incluyendo la cantidad de puntos por lixel
        LinkedHashMap<Long, NetKDELixel> mapaNetKDELixels = new LinkedHashMap<>();
        LinkedHashMap<Long, LinkedHashMap<Long, NetKDELixelDet>> mapaNetKDELixelsDet = new LinkedHashMap<>();
        LinkedHashMap<Long, LinkedHashMap<Long, EventoPunto>> mapaNetKDELixelsPuntos = new LinkedHashMap<>();
        this.contLixels = 0;
        for (RedLinea redLineaAux : listaRedesLineas) {
            //Solo se crearán lixels para líneas de longitud mayor a cero
            if (redLineaAux.getLargoLinea() > 0) {
                LinkedHashMap<Long, NetKDELixel> mapaNetKDELixelsAux = this.obtenerListaNetKDELixels(red, redLineaAux, mapaRedesLineasDet.get(redLineaAux.getIdLinea()), mapaNetKDELixelsDet, mapaPuntosEvento.get(redLineaAux.getIdLinea()), mapaNetKDELixelsPuntos);
                
                //Si hay lixels, se agregan al listado
                if (!mapaNetKDELixelsAux.isEmpty()) {
                    for (long numPuntoAux : mapaNetKDELixelsAux.keySet()) {
                        NetKDELixel netKDELixelAux = mapaNetKDELixelsAux.get(numPuntoAux);
                        mapaNetKDELixels.put(netKDELixelAux.getIdLixel(), netKDELixelAux);
                    }
                }
            }
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
        
        //Se crea el listado de lixels relacionados con sus respectivas distancias (lxCenter dentro del ancho de banda)
        LinkedHashMap<NetKDELixel, LinkedHashMap<Long, NetKDELixel>> mapaNetKDELixelsRel = this.hallarLixelsRelacionados(red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixels);
        
        //Se realiza el cálculo del valor de densidad parcial de cada lixel
        for (NetKDELixel netKDELixelAux : mapaNetKDELixelsRel.keySet()) {
            LinkedHashMap<Long, NetKDELixel> mapaNetKDELixelsAux = mapaNetKDELixelsRel.get(netKDELixelAux);
            
            //Se calcula el valor de la función de núcleo para los puntos del mismo lixel
            double densidadLixelAux = this.calcularFuncionNucleo(0) * netKDELixelAux.getCantidadPuntos() / this.anchoBanda;
            
            //Se calcula el valor de la función de núcleo para los puntos en los demás lixels dentro del ancho de banda
            for (long idLixelAux : mapaNetKDELixelsAux.keySet()) {
                NetKDELixel netKDELixelRel = mapaNetKDELixelsAux.get(idLixelAux);
                
                if (netKDELixelRel.getCantidadPuntos() > 0) {
                    //Valor de densidad parcial para el lixel fuente
                    densidadLixelAux += this.calcularFuncionNucleo(netKDELixelRel.getDistanciaRed()) * netKDELixelRel.getCantidadPuntos() / this.anchoBanda;
                } else {
                    //Valor de densidad parcial para el lixel actual (no fuente)
                    NetKDELixel netKDELixelPar = mapaNetKDELixels.get(netKDELixelRel.getIdLixel());
                    double densidadLixelAux2 = this.calcularFuncionNucleo(netKDELixelRel.getDistanciaRed()) * netKDELixelAux.getCantidadPuntos() / this.anchoBanda;
                    netKDELixelPar.addDensidadLixelParcial(densidadLixelAux2);
                    netKDELixelPar.addDensidadLixel(densidadLixelAux2);
                }
            }
            
            netKDELixelAux.setDensidadLixelParcial(densidadLixelAux);
            netKDELixelAux.setDensidadLixel(densidadLixelAux);
        }
        
        //Se calculan los valores de densidad definitivos para cada lixel
        for (NetKDELixel netKDELixelAux : mapaNetKDELixelsRel.keySet()) {
            LinkedHashMap<Long, NetKDELixel> mapaNetKDELixelsAux = mapaNetKDELixelsRel.get(netKDELixelAux);
            
            //Se recorren los lixels relacionados y se les agrega el valor de densidad parcial
            for (long idLixelAux : mapaNetKDELixelsAux.keySet()) {
                NetKDELixel netKDELixelRel = mapaNetKDELixels.get(idLixelAux);
                
                netKDELixelRel.addDensidadLixel(netKDELixelAux.getDensidadLixelParcial());
            }
        }
        
        String cadenFiltrosAux = Utilidades.mapaFiltrosACadena(this.mapaFiltros);
        DbNetKDE dbNetKDE = new DbNetKDE();
        
        //Se crea el registro temporar de resultados
        long idNetKDETmp = dbNetKDE.crearTmpKNetResultado(false, this.idEvento, this.anchoBanda, this.largoLixel, this.idFuncion, listaEventosPuntos.size(), cadenFiltrosAux);
        
        //Se crean los registros temporales de lixels
        boolean resultadoAux = dbNetKDE.crearTmpNetKDELixels(false, idNetKDETmp, idRed, mapaNetKDELixels);
        if (!resultadoAux) {
            return -2;
        }
        
        //Se crean los registros temporales de detalles de lixel
        resultadoAux = dbNetKDE.crearTmpNetKDELixelsDet(false, idNetKDETmp, mapaNetKDELixelsDet);
        if (!resultadoAux) {
            return -3;
        }
        
        //Se crean los registros temporales de puntos (eventos) asociados a lixels
        resultadoAux = dbNetKDE.crearTmpNetKDELixelsPuntos(false, idNetKDETmp, mapaNetKDELixelsPuntos);
        if (!resultadoAux) {
            return -4;
        }
        
        //Se crean los registros de resultados
        long idNetKDE = dbNetKDE.crearNetKDEResultado(false, idNetKDETmp);
        if (idNetKDE < 0) {
            return -5;
        }
        
        return idNetKDE;
    }
    
    private double calcularFuncionNucleo(double distancia) {
        double resultado = 0;
        
        if (distancia >= 0 && distancia <= this.anchoBanda) {
            switch (this.idFuncion) {
                case 1: //Función Gaussiana
                    resultado = Math.pow((Math.sqrt(2 * Math.PI)), -1) * Math.exp(-Math.pow(distancia, 2) / (2 * Math.pow(this.anchoBanda, 2)));
                    break;

                case 2: //Función de Epanechnikov
                    resultado = 0.75 * (1 - Math.pow(distancia, 2) / Math.pow(this.anchoBanda, 2));
                    break;

                case 3: //Función de Varianza Mínima
                    resultado = (3 / 8) * (3 - 3 * Math.pow(distancia, 2) / Math.pow(this.anchoBanda, 2));
                    break;
                    
                case 4: //Función Uniforme
                    resultado = 0.5;
                    break;
                    
                case 5: //Función Triangular
                    resultado = 1 - Math.abs(distancia / this.anchoBanda);
                    break;
            }
        }
        
        return resultado;
    }
    
    private LinkedHashMap<Long, NetKDELixel> obtenerListaNetKDELixels(Red red, RedLinea redLinea, LinkedHashMap<Long, RedLineaDet> mapaRedesLineasDet, LinkedHashMap<Long, LinkedHashMap<Long, NetKDELixelDet>> mapaNetKDELixelsDet, ArrayList<EventoPunto> listaPuntosEvento, LinkedHashMap<Long, LinkedHashMap<Long, EventoPunto>> mapaNetKDELixelsPuntos) {
        LinkedHashMap<Long, NetKDELixel> mapaNetKDELixelsAux = new LinkedHashMap<>();
        
        if (mapaRedesLineasDet != null && mapaRedesLineasDet.size() > 0) {
            double latBase = Double.NaN;
            double lonBase = Double.NaN;
            double distanciaAct = 0;
            long numPunto = 0;
            for (Long idRedLineaDetAux : mapaRedesLineasDet.keySet()) {
                RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(idRedLineaDetAux);
                if (Double.isNaN(latBase)) {
                    latBase = redLineaDetAux.getLatitud();
                    lonBase = redLineaDetAux.getLongitud();
                } else {
                    //Se crea el punto inicial del lixel
                    numPunto = this.agregarNetKDELixelDetIni(mapaNetKDELixelsDet, numPunto, latBase, lonBase);
                    
                    //Se mueven las coordenadas al punto actual
                    double latAct = redLineaDetAux.getLatitud();
                    double lonAct = redLineaDetAux.getLongitud();
                    
                    double distanciaRem = this.largoLixel - distanciaAct;
                    distanciaAct += redLineaDetAux.getLargoSegmento();
                    if (distanciaAct < this.largoLixel) {
                        //Se agrega el punto del lixel
                        latBase = latAct;
                        lonBase = lonAct;
                        numPunto = this.agregarNetKDELixelDet(mapaNetKDELixelsDet, numPunto, latBase, lonBase, redLineaDetAux.getLargoSegmento());
                    } else {
                        UnidadMedida unidadMedida = red.getSistemaCoordenadas().getUnidadMedida();

                        //Se verifica si la distancia del segmento supera a la distancia del lixel
                        while (distanciaAct >= this.largoLixel) {
                            //Se crea el punto inicial del lixel
                            numPunto = this.agregarNetKDELixelDetIni(mapaNetKDELixelsDet, numPunto, latBase, lonBase);
                            
                            double distanciaAux = this.largoLixel;
                            if (distanciaRem > 0) {
                                distanciaAux = distanciaRem;
                                distanciaRem = 0;
                            }
                            
                            //Se halla la longitud del segmento entre las coordenadas base y las coordenadas actuales
                            double distanciaAux2 = Utilidades.calcularDistanciaPuntos(latBase, lonBase, latAct, lonAct, unidadMedida);
                            double factorAux = distanciaAux / distanciaAux2;
                            
                            //Se agrega el punto final del lixel
                            latBase = latBase + (latAct - latBase) * factorAux;
                            lonBase = lonBase + (lonAct - lonBase) * factorAux;
                            this.agregarNetKDELixelDet(mapaNetKDELixelsDet, numPunto, latBase, lonBase, distanciaAux);
                            
                            //Se agrega el lixel al listado
                            this.agregarNetKDELixel(mapaNetKDELixelsAux, mapaNetKDELixelsDet.get(this.contLixels + 1), redLinea, mapaRedesLineasDet, listaPuntosEvento, mapaNetKDELixelsPuntos);
                            
                            distanciaAct -= this.largoLixel;
                            numPunto = 0;
                        }
                        
                        //El remanente del segmento hace parte de un nuevo lixel
                        if (distanciaAct > 0) {
                            //Se crea el punto inicial del lixel
                            numPunto = this.agregarNetKDELixelDetIni(mapaNetKDELixelsDet, numPunto, latBase, lonBase);
                            
                            //Se crea el punto del lixel que corresponde al final del segmento
                            latBase = latAct;
                            lonBase = lonAct;
                            numPunto = this.agregarNetKDELixelDet(mapaNetKDELixelsDet, numPunto, latBase, lonBase, distanciaAct);
                        }
                    }
                }
            }
            
            //Se verifica si se debe crear un registro de lixel dado que existe detalle
            LinkedHashMap<Long, NetKDELixelDet> mapaAux = new LinkedHashMap<>();
            if (mapaNetKDELixelsDet.containsKey(this.contLixels + 1)) {
                mapaAux = mapaNetKDELixelsDet.get(this.contLixels + 1);
            }
            if (!mapaAux.isEmpty() && !mapaNetKDELixelsAux.containsKey(this.contLixels + 1)) {
                //Se agrega el lixel al listado
                this.agregarNetKDELixel(mapaNetKDELixelsAux, mapaAux, redLinea, mapaRedesLineasDet, listaPuntosEvento, mapaNetKDELixelsPuntos);
            }
            
        }
        return mapaNetKDELixelsAux;
    }
    
    private void agregarNetKDELixel(LinkedHashMap<Long, NetKDELixel> mapaNetKDELixels, LinkedHashMap<Long, NetKDELixelDet> mapaNetKDELixelsDet, RedLinea redLinea, LinkedHashMap<Long, RedLineaDet> mapaRedesLineasDet, ArrayList<EventoPunto> listaPuntosEvento, LinkedHashMap<Long, LinkedHashMap<Long, EventoPunto>> mapaNetKDELixelsPuntos) {
        //Se halla la longitud actual del lixel con base en la longitud de los segmentos y se cuentan los puntos dentro del lixel
        double largoLixelAux = 0;
        int cantidadPuntos = 0;
        double latAnt = 0;
        double lonAnt = 0;
        for (long numPuntoAux : mapaNetKDELixelsDet.keySet()) {
            NetKDELixelDet netKDELixelDetAux = mapaNetKDELixelsDet.get(numPuntoAux);
            largoLixelAux += netKDELixelDetAux.getLargoSegmento();
            
            //Se buscan los puntos que se encuentran dentro del segmento
            if (netKDELixelDetAux.getLargoSegmento() > 0 && listaPuntosEvento != null && listaPuntosEvento.size() > 0) {
                double latAct = netKDELixelDetAux.getLatitud();
                double lonAct = netKDELixelDetAux.getLongitud();
                
                for (EventoPunto eventoPuntoAux : listaPuntosEvento) {
                    if (!eventoPuntoAux.isAsignadoNetKDE()) {
                        double latPunto = eventoPuntoAux.getLatitudProy();
                        double lonPunto = eventoPuntoAux.getLongitudProy();
                        
                        if (((latPunto >= latAnt && latPunto <= latAct) || (latPunto >= latAct && latPunto <= latAnt)) &&
                                ((lonPunto >= lonAnt && lonPunto <= lonAct) || (lonPunto >= lonAct && lonPunto <= lonAnt))) {
                            eventoPuntoAux.setAsignadoNetKDE(true);
                            
                            //Se asocia el punto con el lixel
                            LinkedHashMap<Long, EventoPunto> mapaNetKDELixelsPuntosAux = mapaNetKDELixelsPuntos.get(netKDELixelDetAux.getIdLixel());
                            if (mapaNetKDELixelsPuntosAux == null) {
                                mapaNetKDELixelsPuntosAux = new LinkedHashMap<>();
                            }
                            mapaNetKDELixelsPuntosAux.put(eventoPuntoAux.getIdPunto(), eventoPuntoAux);
                            mapaNetKDELixelsPuntos.put(netKDELixelDetAux.getIdLixel(), mapaNetKDELixelsPuntosAux);
                            
                            cantidadPuntos++;
                        }
                    }
                }
            }
            
            latAnt = netKDELixelDetAux.getLatitud();
            lonAnt = netKDELixelDetAux.getLongitud();
        }
        
        //Se busca el punto central del lixel
        double distanciaAux = 0;
        double latAntAux = 0;
        double lonAntAux = 0;
        for (long numPuntoAux : mapaNetKDELixelsDet.keySet()) {
            NetKDELixelDet netKDELixelDetAux = mapaNetKDELixelsDet.get(numPuntoAux);
            if ((distanciaAux + netKDELixelDetAux.getLargoSegmento()) >= (largoLixelAux / 2.0)) {
                double latActAux = netKDELixelDetAux.getLatitud();
                double lonActAux = netKDELixelDetAux.getLongitud();
                
                double factorAux = ((largoLixelAux / 2.0) - distanciaAux) / netKDELixelDetAux.getLargoSegmento();
                
                //Se hallan las coordenadas del punto central del lixel
                double latLxCenter = latAntAux + (latActAux - latAntAux) * factorAux;
                double lonLxCenter = lonAntAux + (lonActAux - lonAntAux) * factorAux;
                
                //Se determina el segmento sobre el que se encuentra el lxCenter
                long numPuntoLixel = -1;
                long numPuntoAnt = -1;
                latAnt = 0;
                lonAnt = 0;
                for (long numPuntoAux2 : mapaRedesLineasDet.keySet()) {
                    RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(numPuntoAux2);
                    
                    if (numPuntoAnt >= 0) {
                        double latAct = redLineaDetAux.getLatitud();
                        double lonAct = redLineaDetAux.getLongitud();
                        
                        //Se valida si el centro del lixel se encuentra dentro del segmento
                        if (((latLxCenter >= latAnt && latLxCenter <= latAct) || (latLxCenter >= latAct && latLxCenter <= latAnt)) &&
                                ((lonLxCenter >= lonAnt && lonLxCenter <= lonAct) || (lonLxCenter >= lonAct && lonLxCenter <= lonAnt))) {
                            numPuntoLixel = numPuntoAnt;
                            break;
                        }
                    }
                    
                    numPuntoAnt = numPuntoAux2;
                    latAnt = redLineaDetAux.getLatitud();
                    lonAnt = redLineaDetAux.getLongitud();
                }
                
                //Se agrega el lixel al listado
                NetKDELixel netKDELixelAux = new NetKDELixel(this.contLixels + 1, 0, this.idRed, redLinea.getIdLinea(), numPuntoLixel, latLxCenter, lonLxCenter, largoLixelAux, cantidadPuntos, 0, 0);
                mapaNetKDELixels.put(this.contLixels + 1, netKDELixelAux);
                
                this.contLixels++;
                break;
            }
            distanciaAux += netKDELixelDetAux.getLargoSegmento();
            latAntAux = netKDELixelDetAux.getLatitud();
            lonAntAux = netKDELixelDetAux.getLongitud();
        }
    }
    
    private long agregarNetKDELixelDet(LinkedHashMap<Long, LinkedHashMap<Long, NetKDELixelDet>> mapaNetKDELixelsDet, long numPunto, double latBase, double lonBase, double largoSegmento) {
        NetKDELixelDet netKDELixelDetAux = new NetKDELixelDet(this.contLixels + 1, ++numPunto, latBase, lonBase, largoSegmento);
        
        LinkedHashMap<Long, NetKDELixelDet> mapaAux = mapaNetKDELixelsDet.get(this.contLixels + 1);
        if (mapaAux == null) {
            mapaAux = new LinkedHashMap<>();
        }
        mapaAux.put(numPunto, netKDELixelDetAux);
        mapaNetKDELixelsDet.put(this.contLixels + 1, mapaAux);
        
        return numPunto;
    }
    
    private long agregarNetKDELixelDetIni(LinkedHashMap<Long, LinkedHashMap<Long, NetKDELixelDet>> mapaNetKDELixelsDet, long numPunto, double latBase, double lonBase) {
        //Se verifica si el lixel ya tiene puntos
        LinkedHashMap<Long, NetKDELixelDet> mapaAux = new LinkedHashMap<>();
        if (mapaNetKDELixelsDet.containsKey(this.contLixels + 1)) {
            mapaAux = mapaNetKDELixelsDet.get(this.contLixels + 1);
        }
        if (mapaAux.isEmpty()) {
            //Se agrega el punto inicial del lixel
            numPunto = this.agregarNetKDELixelDet(mapaNetKDELixelsDet, numPunto, latBase, lonBase, 0);
        }
        
        return numPunto;
    }
    
    /**
     * Método que busca los lixels fuente junto con los lixels que se encuentran dentro de su ancho de banda
     * @param red Objeto que representa la red
     * @param mapaRedesLineasDet Mapa que contiene mapas de segmentos por línea
     * @param mapaNodosRed Listado de nodos que conforman la red
     * @param mapaNetKDELixels <code>LinkedHashMap</code> con los lixels
     * @return <code>LinkedHashMap</code> con los lixels fuente y sus lixels relacionados
     */
    private LinkedHashMap<NetKDELixel, LinkedHashMap<Long, NetKDELixel>> hallarLixelsRelacionados(Red red, LinkedHashMap<Long, LinkedHashMap<Long, RedLineaDet>> mapaRedesLineasDet, LinkedHashMap<String, ArrayList<RedLineaDet>> mapaNodosRed, LinkedHashMap<Long, NetKDELixel> mapaNetKDELixels) {
        //Mapa que contendrá los resultados
        LinkedHashMap<NetKDELixel, LinkedHashMap<Long, NetKDELixel>> mapaNetKDELixelsResul = new LinkedHashMap<>();
        
        //Se crea un mapa de lixels agrupados por segmento de línea para hacer más rápidas las búsquedas posteriores
        LinkedHashMap<String, ArrayList<NetKDELixel>> mapaNetKDELixelsSegmento = new LinkedHashMap<>();
        for (long idLixelAux : mapaNetKDELixels.keySet()) {
            NetKDELixel netKDELixelAux = mapaNetKDELixels.get(idLixelAux);
            String llaveAux = netKDELixelAux.getIdLinea() + "-" + netKDELixelAux.getNumPunto();
            if (!mapaNetKDELixelsSegmento.containsKey(llaveAux)) {
                mapaNetKDELixelsSegmento.put(llaveAux, new ArrayList<NetKDELixel>());
            }
            mapaNetKDELixelsSegmento.get(llaveAux).add(netKDELixelAux);
        }
        
        //Se recorre el listado de lixels
        for (long idLixelAux : mapaNetKDELixels.keySet()) {
            NetKDELixel netKDELixelAux = mapaNetKDELixels.get(idLixelAux);
            if (netKDELixelAux.getCantidadPuntos() > 0) {
                //Mapa que contendrá los lixels que se encuentran dentro del ancho de banda
                LinkedHashMap<Long, NetKDELixel> mapaNetKDELixelsAux = new LinkedHashMap<>();

                //Se obtienen los puntos (lxCenter) que se encuentran en el mismo segmento
                ArrayList<NetKDELixel> listaNetKDELixelsAux = mapaNetKDELixelsSegmento.get(netKDELixelAux.getIdLinea() + "-" + netKDELixelAux.getNumPunto());
                if (listaNetKDELixelsAux != null) {
                    for (NetKDELixel netKDELixelRel : listaNetKDELixelsAux) {
                        //No se compara el lixel consigo mismo
                        if (netKDELixelAux.getIdLixel() != netKDELixelRel.getIdLixel()) {
                            //Se halla la distancia hasta el lxCenter
                            double distanciaAux = Utilidades.calcularDistanciaPuntos(netKDELixelAux.getLatLxCenter(), netKDELixelAux.getLonLxCenter(), netKDELixelRel.getLatLxCenter(), netKDELixelRel.getLonLxCenter(), red.getSistemaCoordenadas().getUnidadMedida(), 8);
                            if (distanciaAux <= this.anchoBanda) {
                                //El lxCenter se halla dentro del ancho de banda
                                netKDELixelRel.setDistanciaRed(distanciaAux);
                                try {
                                    mapaNetKDELixelsAux.put(netKDELixelRel.getIdLixel(), netKDELixelRel.clone());
                                } catch (CloneNotSupportedException ex) {
                                }
                            }
                        }
                    }
                }

                //Se hallan los nodos adyacentes al inicio del segmento
                ArrayList<RedLineaDet> listaNodosRed1 = mapaNodosRed.get(netKDELixelAux.getIdLinea() + "-" + netKDELixelAux.getNumPunto());
                if (listaNodosRed1 != null && listaNodosRed1.size() > 0) {
                    //Se halla la distancia del lxCenter al inicio del segmento
                    double distanciaAux = Utilidades.calcularDistanciaPuntos(netKDELixelAux.getLatLxCenter(), netKDELixelAux.getLonLxCenter(), listaNodosRed1.get(0).getLatitud(), listaNodosRed1.get(0).getLongitud(), red.getSistemaCoordenadas().getUnidadMedida(), 8);

                    //Se hallan los lxCenter dentro de la distancia de red partiendo del inicio del segmento
                    for (RedLineaDet redLineaDetAux : listaNodosRed1) {
                        this.hallarLixelsRelacionadosRec(mapaNetKDELixelsAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                        if (redLineaDetAux.getNumPunto2() > 1) {
                            //Segmento anterior
                            this.hallarLixelsRelacionadosRec(mapaNetKDELixelsAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                        }
                    }
                }

                //Si el segmento inicial tiene un nodo anterior, también se incluye en la búsqueda
                if (netKDELixelAux.getNumPunto() > 1) {
                    //Se halla la distancia del segmento anterior
                    if (mapaRedesLineasDet.containsKey(netKDELixelAux.getIdLinea()) && mapaRedesLineasDet.get(netKDELixelAux.getIdLinea()).containsKey(netKDELixelAux.getNumPunto())) {
                        RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(netKDELixelAux.getIdLinea()).get(netKDELixelAux.getNumPunto());
                        double distanciaAux = Utilidades.calcularDistanciaPuntos(netKDELixelAux.getLatLxCenter(), netKDELixelAux.getLonLxCenter(), redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red.getSistemaCoordenadas().getUnidadMedida(), 8);

                        this.hallarLixelsRelacionadosRec(mapaNetKDELixelsAux, distanciaAux, redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                    }
                }

                //Se hallan los nodos adyacentes al final del segmento
                ArrayList<RedLineaDet> listaNodosRed2 = mapaNodosRed.get(netKDELixelAux.getIdLinea() + "-" + (netKDELixelAux.getNumPunto() + 1));
                if (listaNodosRed2 != null && listaNodosRed2.size() > 0) {
                    //Se halla la distancia del lxCenter al final del segmento
                    double distanciaAux = Utilidades.calcularDistanciaPuntos(netKDELixelAux.getLatLxCenter(), netKDELixelAux.getLonLxCenter(), listaNodosRed2.get(0).getLatitud(), listaNodosRed2.get(0).getLongitud(), red.getSistemaCoordenadas().getUnidadMedida(), 8);

                    //Se hallan los puntos dentro de la distancia de red partiendo del inicio del segmento
                    for (RedLineaDet redLineaDetAux : listaNodosRed2) {
                        this.hallarLixelsRelacionadosRec(mapaNetKDELixelsAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                        if (redLineaDetAux.getNumPunto2() > 1) {
                            //Segmento anterior
                            this.hallarLixelsRelacionadosRec(mapaNetKDELixelsAux, distanciaAux, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                        }
                    }
                }

                mapaNetKDELixelsResul.put(netKDELixelAux, mapaNetKDELixelsAux);
            }
        }
        
        return mapaNetKDELixelsResul;
    }
    
    /**
     * Método recursivo que busca los lixels fuente junto con los lixels que se encuentran dentro de su ancho de banda
     * @param mapaNetKDELixels <code>LinkedHashMap</code> que contiene los lixels hallados hasta el momento con su respectiva distancia.
     * @param distanciaAcum Distancia acumulada anterior
     * @param idLinea Identificador de la línea
     * @param numPunto Número del segmento de línea
     * @param esAnterior Indica si se trata de un segmento anterior
     * @param latitud  Latitud del punto inicial del segmento
     * @param longitud Longitud inicial del punto del segmento
     * @param red Objeto que representa la red
     * @param mapaRedesLineasDet Mapa que contiene mapas de segmentos por línea
     * @param mapaNodosRed Listado de nodos que conforman la red
     * @param mapaNetKDELixelsSegmento <code>LinkedHashMap</code> con los lixels agrupados por segmento.
     */
    private void hallarLixelsRelacionadosRec(LinkedHashMap<Long, NetKDELixel> mapaNetKDELixels, double distanciaAcum, long idLinea, long numPunto, boolean esAnterior, double latitud, double longitud, Red red, LinkedHashMap<Long, LinkedHashMap<Long, RedLineaDet>> mapaRedesLineasDet, LinkedHashMap<String, ArrayList<RedLineaDet>> mapaNodosRed, LinkedHashMap<String, ArrayList<NetKDELixel>> mapaNetKDELixelsSegmento) {
        //Se hallan los lxCenter que se encuentran dentro del segmento
        ArrayList<NetKDELixel> listaNetKDELixelsSegmento = mapaNetKDELixelsSegmento.get(idLinea + "-" + numPunto);
        if (listaNetKDELixelsSegmento != null) {
            for (NetKDELixel netKDELixelAux : listaNetKDELixelsSegmento) {
                //Se halla la distancia hasta el lxCenter
                double distanciaAux = distanciaAcum + Utilidades.calcularDistanciaPuntos(latitud, longitud, netKDELixelAux.getLatLxCenter(), netKDELixelAux.getLonLxCenter(), red.getSistemaCoordenadas().getUnidadMedida(), 8);
                if (distanciaAux <= this.anchoBanda) {
                    //Se verifica si el lxCenter ya fue agregado al mapa y si la distancia actual es mayor
                    if (!mapaNetKDELixels.containsKey(netKDELixelAux.getIdLixel()) || mapaNetKDELixels.get(netKDELixelAux.getIdLixel()).getDistanciaRed() > distanciaAux) {
                        netKDELixelAux.setDistanciaRed(distanciaAux);
                        try {
                            mapaNetKDELixels.put(netKDELixelAux.getIdLixel(), netKDELixelAux.clone());
                        } catch (CloneNotSupportedException ex) {
                        }
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
            if (this.anchoBanda >= (distanciaAcum + distanciaSeg)) {
                //Se hallan los lxCenter dentro de la distancia de red dentro de los segmentos adyacentes
                for (RedLineaDet redLineaDetAux : listaNodosRed) {
                    this.hallarLixelsRelacionadosRec(mapaNetKDELixels, distanciaAcum + distanciaSeg, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                    if (redLineaDetAux.getNumPunto2() > 1) {
                        //Segmento anterior
                        this.hallarLixelsRelacionadosRec(mapaNetKDELixels, distanciaAcum + distanciaSeg, redLineaDetAux.getIdLinea2(), redLineaDetAux.getNumPunto2() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
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
                    double distanciaAux = Utilidades.calcularDistanciaPuntos(latitud, longitud, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red.getSistemaCoordenadas().getUnidadMedida(), 8);
                    
                    if (this.anchoBanda >= (distanciaAcum + distanciaAux)) {
                        this.hallarLixelsRelacionadosRec(mapaNetKDELixels, distanciaAcum + distanciaAux, redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto() - 1, true, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                    }
                }
            }
        } else {
            //Si el segmento tiene un nodo siguiente, también se incluye en la búsqueda
            if (mapaRedesLineasDet.containsKey(idLinea) && mapaRedesLineasDet.get(idLinea).containsKey(numPunto + 1)) {
                RedLineaDet redLineaDetAux = mapaRedesLineasDet.get(idLinea).get(numPunto + 1);
                
                if (this.anchoBanda >= (distanciaAcum + redLineaDetAux.getLargoSegmento())) {
                    this.hallarLixelsRelacionadosRec(mapaNetKDELixels, distanciaAcum + redLineaDetAux.getLargoSegmento(), redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto(), false, redLineaDetAux.getLatitud(), redLineaDetAux.getLongitud(), red, mapaRedesLineasDet, mapaNodosRed, mapaNetKDELixelsSegmento);
                }
            }
        }
    }
}
