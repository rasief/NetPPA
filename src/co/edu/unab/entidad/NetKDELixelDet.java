package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla netkde_lixels_det
 * @author Feisar Moreno
 * @date 09/06/2016
 */
public class NetKDELixelDet {
    private long idLixel;
    private long numPunto;
    private double latitud;
    private double longitud;
    private double largoSegmento;
    private long idLinea;
    
    public NetKDELixelDet() {
    }
    
    public NetKDELixelDet(long idLixel, long numPunto, double latitud, double longitud, double largoSegmento) {
        this.idLixel = idLixel;
        this.numPunto = numPunto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.largoSegmento = largoSegmento;
    }
    
    public NetKDELixelDet(ResultSet rs) {
        try {
            this.idLixel = rs.getLong("ID_LIXEL");
        } catch (SQLException e) {
            this.idLixel = 0L;
        }
        
        try {
            this.numPunto = rs.getLong("NUM_PUNTO");
        } catch (SQLException e) {
            this.numPunto = 0L;
        }
        
        try {
            this.latitud = rs.getDouble("LATITUD");
        } catch (SQLException e) {
            this.latitud = 0L;
        }
        
        try {
            this.longitud = rs.getDouble("LONGITUD");
        } catch (SQLException e) {
            this.longitud = 0L;
        }
        
        try {
            this.largoSegmento = rs.getDouble("LARGO_SEGMENTO");
        } catch (SQLException e) {
            this.largoSegmento = 0L;
        }
        
        try {
            this.idLinea = rs.getLong("ID_LINEA");
        } catch (SQLException e) {
            this.idLinea = 0L;
        }
    }
    
    public long getIdLixel() {
        return idLixel;
    }
    
    public void setIdLixel(long idLixel) {
        this.idLixel = idLixel;
    }
    
    public long getNumPunto() {
        return numPunto;
    }
    
    public void setNumPunto(long numPunto) {
        this.numPunto = numPunto;
    }
    
    public double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    
    public double getLongitud() {
        return longitud;
    }
    
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    
    public double getLargoSegmento() {
        return largoSegmento;
    }
    
    public void setLargoSegmento(double largoSegmento) {
        this.largoSegmento = largoSegmento;
    }
    
    public long getIdLinea() {
        return idLinea;
    }
    
    public void setIdLinea(long idLinea) {
        this.idLinea = idLinea;
    }
}
