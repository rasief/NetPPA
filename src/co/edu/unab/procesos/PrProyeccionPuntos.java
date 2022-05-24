package co.edu.unab.procesos;

import co.edu.unab.db.DbEventos;
import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.Evento;
import co.edu.unab.entidad.EventoPunto;
import co.edu.unab.entidad.Red;
import co.edu.unab.entidad.RedLineaDet;
import co.edu.unab.entidad.UnidadMedida;
import co.edu.unab.utilidades.Utilidades;
import java.util.ArrayList;

/**
 * Clase para la proyección de puntos sobre redes
 * @author Feisar Moreno
 * @date 11/03/2016
 */
public class PrProyeccionPuntos {
    private final long idEvento;
    private final double distProy;
    private boolean indInicioProyeccion;
    private int cantTotalPuntos;
    private int puntoActProceso;
    
    /**
     * Constructor de la clase
     * @param evento Objeto que representa el evento
     * @param distProy Distancia máxima de la proyección en metros
     */
    public PrProyeccionPuntos(Evento evento, double distProy) {
        this.idEvento = evento.getIdEvento();
        this.distProy = distProy;
        this.indInicioProyeccion = false;
    }
    
    public long getIdEvento() {
        return this.idEvento;
    }
    
    public double getDistProy() {
        return this.distProy;
    }
    
    public boolean isIndInicioProyeccion() {
        return indInicioProyeccion;
    }
    
    public int getCantTotalPuntos() {
        return cantTotalPuntos;
    }
    
    public int getPuntoActProceso() {
        return puntoActProceso;
    }
    
    /**
     * Método que realiza la proyeccion
     * @return <code>true</code> si se logra realizar la proyección con éxito, de lo contrario <code>false</code>.
     */
    public boolean realizarProyeccion() {
        boolean resultado = false;
        
        //Se obtienen los datos del evento
        DbEventos dbEventos = new DbEventos();
        Evento evento = dbEventos.getEvento(false, this.idEvento);
        
        //Se obtienen los datos de la red
        DbRedes dbRedes = new DbRedes();
        Red red = dbRedes.getRed(false, evento.getRed().getIdRed());
        long idRed = red.getIdRed();
        UnidadMedida unidadMedida = red.getSistemaCoordenadas().getUnidadMedida();
        
        //Se obtiene el listado de puntos del evento
        ArrayList<EventoPunto> listaEventosPuntos = dbEventos.getListaEventosPuntos(false, this.idEvento);
        
        this.cantTotalPuntos = listaEventosPuntos.size();
        this.puntoActProceso = 0;
        this.indInicioProyeccion = true;
        
        //De acuerdo con la unidad de medida se calculan los factores aproximados de distancia para la latitud y la longitud
        double factorLatitud, factorLongitud;
        if (unidadMedida.getIndGrados() == 1) {
            EventoPunto eventoPuntoAux = listaEventosPuntos.get(0);
            factorLatitud = Utilidades.calcularDistanciaPuntos(eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), eventoPuntoAux.getLatitud() + 1, eventoPuntoAux.getLongitud(), unidadMedida, 8);
            factorLongitud = Utilidades.calcularDistanciaPuntos(eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud() + 1, unidadMedida, 8);
        } else {
            factorLatitud = unidadMedida.getFactorMetros();
            factorLongitud = unidadMedida.getFactorMetros();
        }
        
        //Lista que contendrá los puntos ajustados
        ArrayList<EventoPunto> listaEventosPuntosResul = new ArrayList<>();
        
        //Se recorren los puntos del evento
        for (EventoPunto eventoPuntoAux : listaEventosPuntos) {
            //Se obtiene el listado de segmentos cercanos al punto
            ArrayList<RedLineaDet> listaRedesLineasDetAux = dbRedes.getListaRedesLineasDetDist(false, idRed, eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), this.distProy, factorLatitud, factorLongitud, unidadMedida.getFactorMetros(), unidadMedida.getIndGrados());
            if (listaRedesLineasDetAux.size() > 0) {
                long idLinea = 0, numPunto = 0;
                double latitud = Double.NaN, longitud = Double.NaN;
                double distancia = this.distProy + 1;
                double latitudProy, longitudProy;
                for (RedLineaDet redLineaDetAux : listaRedesLineasDetAux) {
                    double xPunto, yPunto, xIni, yIni, xFin, yFin;
                    
                    if (unidadMedida.getIndGrados() == 1) {
                        //Se deben convertir las coordenadas geográficas a coordenadas cartesianas, se toma como punto de origen el punto recibido
                        xPunto = 0;
                        yPunto = 0;
                        xIni = Utilidades.transformarLongitudPlana(redLineaDetAux.getLongitud(), eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), unidadMedida, 8);
                        yIni = Utilidades.transformarLatitudPlana(redLineaDetAux.getLatitud(), eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), unidadMedida, 8);
                        xFin = Utilidades.transformarLongitudPlana(redLineaDetAux.getLongitud2(), eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), unidadMedida, 8);
                        yFin = Utilidades.transformarLatitudPlana(redLineaDetAux.getLatitud2(), eventoPuntoAux.getLatitud(), eventoPuntoAux.getLongitud(), unidadMedida, 8);
                    } else {
                        xPunto = eventoPuntoAux.getLongitud() * factorLongitud;
                        yPunto = eventoPuntoAux.getLatitud() * factorLatitud;
                        xIni = redLineaDetAux.getLongitud() * factorLongitud;
                        yIni = redLineaDetAux.getLatitud() * factorLatitud;
                        xFin = redLineaDetAux.getLongitud2() * factorLongitud;
                        yFin = redLineaDetAux.getLatitud2() * factorLatitud;
                    }
                    
                    if (redLineaDetAux.getLargoSegmento() == 0) {
                        latitudProy = eventoPuntoAux.getLatitud();
                        longitudProy = eventoPuntoAux.getLongitud();
                    } else {
                        //Se buscan las coordenadas de la proyección
                        if (redLineaDetAux.getLongitud() == redLineaDetAux.getLongitud2()) {
                            //Se trata de una línea vertical
                            latitudProy = eventoPuntoAux.getLatitud();
                            longitudProy = redLineaDetAux.getLongitud();
                        } else if (redLineaDetAux.getLatitud() == redLineaDetAux.getLatitud2()) {
                            //Se trata de una línea horizontal
                            latitudProy = redLineaDetAux.getLatitud();
                            longitudProy = eventoPuntoAux.getLongitud();
                        } else {
                            double pendiente = (yFin - yIni) / (xFin - xIni);
                            double cruceY = yIni - pendiente * xIni;
                            
                            double xProy = (xPunto + pendiente * yPunto - pendiente * cruceY) / (Math.pow(pendiente, 2) + 1);
                            double yProy = (pendiente * xPunto + Math.pow(pendiente, 2) * yPunto + cruceY) / (Math.pow(pendiente, 2) + 1);
                            
                            if (unidadMedida.getIndGrados() == 1) {
                                latitudProy = redLineaDetAux.getLatitud() + ((yProy - yIni) / (yFin - yIni)) * (redLineaDetAux.getLatitud2() - redLineaDetAux.getLatitud());
                                longitudProy = redLineaDetAux.getLongitud() + ((xProy - xIni) / (xFin - xIni)) * (redLineaDetAux.getLongitud2() - redLineaDetAux.getLongitud());
                            } else {
                                latitudProy = yProy / factorLatitud;
                                longitudProy = xProy / factorLongitud;
                            }
                        }
                    }
                    
                    //Si la distancia actual es menor que la distancia menor hallada
                    if (redLineaDetAux.getLargoSegmento() < distancia) {
                        //Se verifica que la proyección se encuentre sobre la línea
                        if ((latitudProy >= redLineaDetAux.getLatitud() && latitudProy <= redLineaDetAux.getLatitud2()) ||
                                (latitudProy >= redLineaDetAux.getLatitud2() && latitudProy <= redLineaDetAux.getLatitud()) &&
                                ((longitudProy >= redLineaDetAux.getLongitud() && longitudProy <= redLineaDetAux.getLongitud2()) ||
                                (longitudProy >= redLineaDetAux.getLongitud2() && longitudProy <= redLineaDetAux.getLongitud()))) {
                            //Está adentro, es la proyección de menor distancia
                            idLinea = redLineaDetAux.getIdLinea();
                            numPunto = redLineaDetAux.getNumPunto();
                            latitud = latitudProy;
                            longitud = longitudProy;
                            //distancia = redLineaDetAux.getLargoSegmento();
                            break;
                        } else {
                            //Se hallan las distancias a los extremos, se escoge la menor y se comprueba si está dentro del rango y si es menor que la distancia hallada
                            double distanciaIni = Math.sqrt(Math.pow(xPunto - xIni, 2) + Math.pow(yPunto - yIni, 2));
                            double distanciaFin = Math.sqrt(Math.pow(xPunto - xFin, 2) + Math.pow(yPunto - yFin, 2));
                            
                            if (distanciaIni <= distanciaFin && distanciaIni < distancia && distanciaIni <= this.distProy) {
                                idLinea = redLineaDetAux.getIdLinea();
                                numPunto = redLineaDetAux.getNumPunto();
                                latitud = redLineaDetAux.getLatitud();
                                longitud = redLineaDetAux.getLongitud();
                                distancia = distanciaIni;
                            } else if (distanciaFin < distanciaIni && distanciaFin < distancia && distanciaFin <= this.distProy) {
                                idLinea = redLineaDetAux.getIdLinea();
                                numPunto = redLineaDetAux.getNumPunto();
                                latitud = redLineaDetAux.getLatitud2();
                                longitud = redLineaDetAux.getLongitud2();
                                distancia = distanciaFin;
                            }
                        }
                    } else {
                        break;
                    }
                }
                
                //Si se halló una proyección, se actualiza en el punto
                if (idLinea > 0) {
                    EventoPunto eventoPuntoResulAux = new EventoPunto(this.idEvento, eventoPuntoAux.getIdPunto(), null, latitud, longitud, latitud, longitud, idRed, idLinea, numPunto);
                    listaEventosPuntosResul.add(eventoPuntoResulAux);
                }
            }
            
            this.puntoActProceso++;
        }
        
        //Se actualizan los valores
        if (listaEventosPuntosResul.size() > 0) {
            resultado = dbEventos.realizarProyeccion(false, idRed, this.idEvento, this.distProy, listaEventosPuntosResul);
        }
        
        return resultado;
    }
}
