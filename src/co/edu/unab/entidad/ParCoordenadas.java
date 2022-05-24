package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de pares de coordenadas geográficas
 * @author Feisar Moreno
 * @date 19/06/2016
 */
public class ParCoordenadas {
    private double latitudMin;
    private double longitudMin;
    private double latitudMax;
    private double longitudMax;
    
    public ParCoordenadas() {
    }
    
    public ParCoordenadas(double latitudMin, double longitudMin, double latitudMax, double longitudMax) {
        this.latitudMin = latitudMin;
        this.longitudMin = longitudMin;
        this.latitudMax = latitudMax;
        this.longitudMax = longitudMax;
    }
    
    public ParCoordenadas(ResultSet rs) {
        try {
            this.latitudMin = rs.getDouble("LATITUD_MIN");
        } catch (SQLException e) {
            this.latitudMin = 0;
        }
        
        try {
            this.longitudMin = rs.getDouble("LONGITUD_MIN");
        } catch (SQLException e) {
            this.longitudMin = 0;
        }
        
        try {
            this.latitudMax = rs.getDouble("LATITUD_MAX");
        } catch (SQLException e) {
            this.latitudMax = 0;
        }
        
        try {
            this.longitudMax = rs.getDouble("LONGITUD_MAX");
        } catch (SQLException e) {
            this.longitudMax = 0;
        }
    }
    
    public double getLatitudMin() {
        return latitudMin;
    }
    
    public void setLatitudMin(double latitudMin) {
        this.latitudMin = latitudMin;
    }
    
    public double getLongitudMin() {
        return longitudMin;
    }
    
    public void setLongitudMin(double longitudMin) {
        this.longitudMin = longitudMin;
    }
    
    public double getLatitudMax() {
        return latitudMax;
    }
    
    public void setLatitudMax(double latitudMax) {
        this.latitudMax = latitudMax;
    }
    
    public double getLongitudMax() {
        return longitudMax;
    }
    
    public void setLongitudMax(double longitudMax) {
        this.longitudMax = longitudMax;
    }
}
