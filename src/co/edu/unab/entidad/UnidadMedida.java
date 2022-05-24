package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla unidades_medida
 * @author Feisar Moreno
 * @date 24/04/2016
 */
public class UnidadMedida {
    private long idUnidad;
    private String nombreUnidad;
    private double factorMetros;
    private int indGrados;
    
    public UnidadMedida() {
    }
    
    public UnidadMedida(long idUnidad, String nombreUnidad, double factorMetros, int indGrados) {
        this.idUnidad = idUnidad;
        this.nombreUnidad = nombreUnidad;
        this.factorMetros = factorMetros;
        this.indGrados = indGrados;
    }
    
    public UnidadMedida(ResultSet rs) {
        try {
            idUnidad = rs.getLong("ID_UNIDAD");
        } catch (SQLException e) {
            idUnidad = 0;
        }
        
        try {
            nombreUnidad = rs.getString("NOMBRE_UNIDAD");
        } catch (SQLException e) {
            nombreUnidad = "";
        }
        
        try {
            factorMetros = rs.getDouble("FACTOR_METROS");
        } catch (SQLException e) {
            factorMetros = 0;
        }
        
        try {
            indGrados = rs.getInt("IND_GRADOS");
        } catch (SQLException e) {
            indGrados = 0;
        }
    }
    
    public long getIdUnidad() {
        return idUnidad;
    }
    
    public String getNombreUnidad() {
        return nombreUnidad;
    }
    
    public double getFactorMetros() {
        return factorMetros;
    }
    
    public int getIndGrados() {
        return indGrados;
    }
    
    @Override
    public String toString() {
        return this.nombreUnidad;
    }
}
