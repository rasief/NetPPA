package co.edu.unab.procesos;

import co.edu.unab.db.DbRedes;
import co.edu.unab.entidad.Red;
import co.edu.unab.entidad.RedLineaDet;
import java.util.ArrayList;

/**
 * Clase para el cierre de nodos de red
 * @author Feisar Moreno
 * @date 23/05/2016
 */
public class PrCierreNodosRed {
    private final long idRed;
    private final double distCierreNodos;
    private boolean indInicioCierre;
    private int cantTotalLineas;
    private int lineaActProceso;
    
    /**
     * Constructor de la clase
     * @param red Objeto que representa la red
     * @param distCierreNodos Distancia máxima de la proyección en metros
     */
    public PrCierreNodosRed(Red red, double distCierreNodos) {
        this.idRed = red.getIdRed();
        this.distCierreNodos = distCierreNodos;
        this.indInicioCierre = false;
    }
    
    public long getIdRed() {
        return this.idRed;
    }
    
    public double getDistCierreNodos() {
        return this.distCierreNodos;
    }
    
    public boolean isIndInicioCierre() {
        return indInicioCierre;
    }
    
    public int getCantTotalLineas() {
        return cantTotalLineas;
    }
    
    public int getLineaActProceso() {
        return lineaActProceso;
    }
    
    /**
     * Método que realiza la proyeccion
     * @return Cantidad de cierres realizados, -1 en caso de error de procedimiento almacenado, -2 en caso de error de Java.
     */
    public int realizarCierreNodos() {
        DbRedes dbRedes = new DbRedes();
        int cantCierres = 0;
        
        try {
            dbRedes.crearConexion();

            //Se buscan los datos de la red
            Red red = dbRedes.getRed(true, this.idRed);
            double factorMetros = red.getSistemaCoordenadas().getUnidadMedida().getFactorMetros();
            int indGrados = red.getSistemaCoordenadas().getUnidadMedida().getIndGrados();
            
            //Se obtiene el detalle de las líneas de la red
            ArrayList<RedLineaDet> listaRedesLineasDet = dbRedes.getListaRedesLineasDetExtremos(true, this.idRed);
            
            this.cantTotalLineas = listaRedesLineasDet.size();
            this.lineaActProceso = 0;
            this.indInicioCierre = true;
            for (RedLineaDet redLineaDetAux : listaRedesLineasDet) {
                //Se obtienen las coordenadas del nodo actual
                RedLineaDet redLineaDetAux2 = dbRedes.getRedLineaDet(true, this.idRed, redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto());
                double latitudAux = redLineaDetAux2.getLatitud();
                double longitudAux = redLineaDetAux2.getLongitud();
                
                //Se buscan los nodos que se encuentren a una distancia menor o igual a la distancia de cierre
                ArrayList<RedLineaDet> listaRedesLineasDetAux = dbRedes.getListaRedesLineasDetDist(true, this.idRed, redLineaDetAux.getIdLinea(), latitudAux, longitudAux, this.distCierreNodos, factorMetros, indGrados);
                
                for (RedLineaDet redLineaDetAux3 : listaRedesLineasDetAux) {
                    //Se verifica si el nodo ya tiene un nodo adyacente de la misma línea
                    boolean conectadosAux = dbRedes.isNodosConectados(true, this.idRed, redLineaDetAux.getIdLinea(), redLineaDetAux.getNumPunto(), redLineaDetAux3.getIdLinea());
                    
                    if (!conectadosAux) {
                        //Se actualizan las coordenadas del nodo
                        boolean resultadoAux = dbRedes.editarRedLineaDetCoordenadas(true, this.idRed, redLineaDetAux3.getIdLinea(), redLineaDetAux3.getNumPunto(), latitudAux, longitudAux);
                        if (resultadoAux) {
                            cantCierres++;
                        } else {
                            cantCierres = -1;
                            break;
                        }
                    }
                }
                this.lineaActProceso++;
                if (cantCierres < 0) {
                    break;
                }
            }
            
            if (cantCierres >= 0) {
                //Se marca la red con nodos cerrados
                boolean resultadoAux = dbRedes.editarRedCierreNodos(true, this.idRed, 1, this.distCierreNodos, cantCierres);
                if (resultadoAux) {
                    if (cantCierres > 0) {
                        //Se actualizan las longitudes acumuladas de las líneas
                        resultadoAux = dbRedes.calcularLargoLineasRed(true, this.idRed, 1);
                        if (!resultadoAux) {
                            cantCierres = -1;
                        }
                    }
                } else {
                    cantCierres = -1;
                }
            }
        } catch (Exception e) {
            cantCierres = -1;
        } finally {
            dbRedes.cerrarConexion();
        }
        
        return cantCierres;
    }
}
