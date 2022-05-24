package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla knet_valores
 * @author Feisar Moreno
 * @date 09/05/2016
 */
public class KNetValor {
    private long idKNet;
    private double distanciaKNet;
    private double valor;
    private double limiteMin;
    private double limiteMax;
    
    public KNetValor() {
    }
    
    public KNetValor(ResultSet rs) {
        try {
            this.idKNet = rs.getLong("ID_KNET");
        } catch (SQLException e) {
            this.idKNet = 0L;
        }
        
        try {
            this.distanciaKNet = rs.getDouble("DISTANCIA_KNET");
        } catch (SQLException e) {
            this.distanciaKNet = 0;
        }
        
        try {
            this.valor = rs.getDouble("VALOR");
        } catch (SQLException e) {
            this.valor = 0;
        }
        
        try {
            this.limiteMin = rs.getDouble("LIMITE_MIN");
        } catch (SQLException e) {
            this.limiteMin = 0;
        }
        
        try {
            this.limiteMax = rs.getDouble("LIMITE_MAX");
        } catch (SQLException e) {
            this.limiteMax = 0;
        }
    }
    
    public long getIdKNet() {
        return idKNet;
    }
    
    public double getDistanciaKNet() {
        return distanciaKNet;
    }
    
    public double getValor() {
        return valor;
    }
    
    public double getLimiteMin() {
        return limiteMin;
    }
    
    public double getLimiteMax() {
        return limiteMax;
    }
}
