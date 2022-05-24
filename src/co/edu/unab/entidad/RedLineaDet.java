package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla redes_lineas_det
 * @author Feisar Moreno
 * @date 24/04/2016
 */
public class RedLineaDet {
    private long idRed;
    private long idLinea;
    private long numPunto;
    private double latitud;
    private double longitud;
    private long idLinea2;
    private long numPunto2;
    private double latitud2;
    private double longitud2;
    private double largoSegmento;
    
    public RedLineaDet() {
    }
    
    public RedLineaDet(long idRed, long idLinea, long numPunto, double latitud, double longitud) {
        this.idRed = idRed;
        this.idLinea = idLinea;
        this.numPunto = numPunto;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    public RedLineaDet(ResultSet rs) {
        try {
            this.idRed = rs.getLong("ID_RED");
        } catch (SQLException e) {
            this.idRed = 0L;
        }
        
        try {
            this.idLinea = rs.getLong("ID_LINEA");
        } catch (SQLException e) {
            this.idLinea = 0L;
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
            this.idLinea2 = rs.getLong("ID_LINEA_2");
        } catch (SQLException e) {
            this.idLinea2 = 0L;
        }
        
        try {
            this.numPunto2 = rs.getLong("NUM_PUNTO_2");
        } catch (SQLException e) {
            this.numPunto2 = 0L;
        }
        
        try {
            this.latitud2 = rs.getDouble("LATITUD_2");
        } catch (SQLException e) {
            this.latitud2 = 0L;
        }
        
        try {
            this.longitud2 = rs.getDouble("LONGITUD_2");
        } catch (SQLException e) {
            this.longitud2 = 0L;
        }
        
        try {
            this.largoSegmento = rs.getDouble("LARGO_SEGMENTO");
        } catch (SQLException e) {
            this.largoSegmento = 0L;
        }
    }
    
    public long getIdRed() {
        return idRed;
    }
    
    public long getIdLinea() {
        return idLinea;
    }
    
    public long getNumPunto() {
        return numPunto;
    }
    
    public double getLatitud() {
        return latitud;
    }
    
    public double getLongitud() {
        return longitud;
    }
    
    public long getIdLinea2() {
        return idLinea2;
    }
    
    public long getNumPunto2() {
        return numPunto2;
    }
    
    public double getLatitud2() {
        return latitud2;
    }
    
    public double getLongitud2() {
        return longitud2;
    }
    
    public double getLargoSegmento() {
        return largoSegmento;
    }
}
