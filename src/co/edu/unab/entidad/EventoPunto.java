package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla eventos_puntos
 * @author Feisar Moreno
 * @date 24/04/2016
 */
public class EventoPunto {
    private long idEvento;
    private long idPunto;
    private String fechaPunto;
    private double latitud;
    private double longitud;
    private double latitudProy;
    private double longitudProy;
    private long idRed;
    private long idLinea;
    private long numPunto;
    private double distanciaRed;
    private boolean asignadoNetKDE = false;
    
    public EventoPunto() {
    }
    
    public EventoPunto(long idEvento, long idPunto, String fechaPunto, double latitud, double longitud) {
        this.idEvento = idEvento;
        this.idPunto = idPunto;
        this.fechaPunto = fechaPunto;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    public EventoPunto(long idEvento, long idPunto, String fechaPunto, double latitud, double longitud, double latitudProy, double longitudProy, long idRed, long idLinea, long numPunto) {
        this.idEvento = idEvento;
        this.idPunto = idPunto;
        this.fechaPunto = fechaPunto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.latitudProy = latitudProy;
        this.longitudProy = longitudProy;
        this.idRed = idRed;
        this.idLinea = idLinea;
        this.numPunto = numPunto;
    }
    
    public EventoPunto(ResultSet rs) {
        try {
            this.idEvento = rs.getLong("ID_EVENTO");
        } catch (SQLException e) {
            this.idEvento = 0L;
        }
        
        try {
            this.idPunto = rs.getLong("ID_PUNTO");
        } catch (SQLException e) {
            this.idPunto = 0L;
        }
        
        try {
            this.fechaPunto = rs.getString("FECHA_PUNTO_T");
        } catch (SQLException e) {
            this.fechaPunto = "";
        }
        
        try {
            this.latitud = rs.getDouble("LATITUD");
        } catch (SQLException e) {
            this.latitud = 0;
        }
        
        try {
            this.longitud = rs.getDouble("LONGITUD");
        } catch (SQLException e) {
            this.longitud = 0;
        }
        
        try {
            this.latitudProy = rs.getDouble("LATITUD_PROY");
        } catch (SQLException e) {
            this.latitudProy = 0;
        }
        
        try {
            this.longitudProy = rs.getDouble("LONGITUD_PROY");
        } catch (SQLException e) {
            this.longitudProy = 0;
        }
        
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
    }
    
    public long getIdEvento() {
        return idEvento;
    }
    
    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }
    
    public long getIdPunto() {
        return idPunto;
    }
    
    public void setIdPunto(long idPunto) {
        this.idPunto = idPunto;
    }
    
    public String getFechaPunto() {
        return fechaPunto;
    }
    
    public void setFechaPunto(String fechaPunto) {
        this.fechaPunto = fechaPunto;
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
    
    public double getLatitudProy() {
        return latitudProy;
    }
    
    public void setLatitudProy(double latitudProy) {
        this.latitudProy = latitudProy;
    }
    
    public double getLongitudProy() {
        return longitudProy;
    }
    
    public void setLongitudProy(double longitudProy) {
        this.longitudProy = longitudProy;
    }
    
    public long getIdRed() {
        return idRed;
    }
    
    public void setIdRed(long idRed) {
        this.idRed = idRed;
    }
    
    public long getIdLinea() {
        return idLinea;
    }
    
    public void setIdLinea(long idLinea) {
        this.idLinea = idLinea;
    }
    
    public long getNumPunto() {
        return numPunto;
    }
    
    public void setNumPunto(long numPunto) {
        this.numPunto = numPunto;
    }
    
    public double getDistanciaRed() {
        return distanciaRed;
    }
    
    public void setDistanciaRed(double distanciaRed) {
        this.distanciaRed = distanciaRed;
    }
    
    public boolean isAsignadoNetKDE() {
        return asignadoNetKDE;
    }
    
    public void setAsignadoNetKDE(boolean asignadoNetKDE) {
        this.asignadoNetKDE = asignadoNetKDE;
    }
}
