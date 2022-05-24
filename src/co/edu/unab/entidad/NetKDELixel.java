package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase que representa un registro de la tabla redes_lineas
 * @author Feisar Moreno
 * @date 24/04/2016
 */
public class NetKDELixel implements Cloneable {
    private long idLixel;
    private long idNetKDE;
    private long idRed;
    private long idLinea;
    private long numPunto;
    private double latLxCenter;
    private double lonLxCenter;
    private double largoLixel;
    private int cantidadPuntos;
    private double densidadLixel;
    private double densidadLixelParcial;
    private double distanciaRed;
    private ArrayList<NetKDELixelDet> listaNetKDELixelsDet = new ArrayList<>();
    
    public NetKDELixel() {
    }
    
    public NetKDELixel(long idLixel, long idNetKDE, long idRed, long idLinea, long numPunto, double latLxCenter, double lonLxCenter, double largoLixel, int cantidadPuntos, double densidadLixel, double densidadLixelParcial) {
        this.idLixel = idLixel;
        this.idNetKDE = idNetKDE;
        this.idRed = idRed;
        this.idLinea = idLinea;
        this.numPunto = numPunto;
        this.latLxCenter = latLxCenter;
        this.lonLxCenter = lonLxCenter;
        this.largoLixel = largoLixel;
        this.cantidadPuntos = cantidadPuntos;
        this.densidadLixel = densidadLixel;
        this.densidadLixelParcial = densidadLixelParcial;
    }
    
    public NetKDELixel(ResultSet rs) {
        try {
            this.idLixel = rs.getLong("ID_LIXEL");
        } catch (SQLException e) {
            this.idLixel = 0L;
        }
        
        try {
            this.idNetKDE = rs.getLong("ID_NETKDE");
        } catch (SQLException e) {
            this.idNetKDE = 0L;
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
        
        try {
            this.latLxCenter = rs.getDouble("LAT_LXCENTER");
        } catch (SQLException e) {
            this.latLxCenter = 0;
        }
        
        try {
            this.lonLxCenter = rs.getDouble("LON_LXCENTER");
        } catch (SQLException e) {
            this.lonLxCenter = 0;
        }
        
        try {
            this.cantidadPuntos = rs.getInt("CANTIDAD_PUNTOS");
        } catch (SQLException e) {
            this.cantidadPuntos = 0;
        }
        
        try {
            this.densidadLixel = rs.getDouble("DENSIDAD_LIXEL");
        } catch (SQLException e) {
            this.densidadLixel = 0;
        }
    }
    
    public long getIdLixel() {
        return idLixel;
    }
    
    public void setIdLixel(long idLixel) {
        this.idLixel = idLixel;
    }
    
    public long getIdNetKDE() {
        return idNetKDE;
    }
    
    public void setIdNetKDE(long idNetKDE) {
        this.idNetKDE = idNetKDE;
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
    
    public double getLatLxCenter() {
        return latLxCenter;
    }
    
    public void setLatLxCenter(double latLxCenter) {
        this.latLxCenter = latLxCenter;
    }
    
    public double getLonLxCenter() {
        return lonLxCenter;
    }
    
    public void setLonLxCenter(double lonLxCenter) {
        this.lonLxCenter = lonLxCenter;
    }
    
    public double getLargoLixel() {
        return largoLixel;
    }
    
    public void setLargoLixel(double largoLixel) {
        this.largoLixel = largoLixel;
    }
    
    public int getCantidadPuntos() {
        return cantidadPuntos;
    }
    
    public void setCantidadPuntos(int cantidadPuntos) {
        this.cantidadPuntos = cantidadPuntos;
    }
    
    public double getDensidadLixel() {
        return densidadLixel;
    }
    
    public void setDensidadLixel(double densidadLixel) {
        this.densidadLixel = densidadLixel;
    }
    
    public double getDensidadLixelParcial() {
        return densidadLixelParcial;
    }
    
    public void setDensidadLixelParcial(double densidadLixelParcial) {
        this.densidadLixelParcial = densidadLixelParcial;
    }
    
    public double getDistanciaRed() {
        return distanciaRed;
    }
    
    public void setDistanciaRed(double distanciaRed) {
        this.distanciaRed = distanciaRed;
    }
    
    public ArrayList<NetKDELixelDet> getListaNetKDELixelsDet() {
        return listaNetKDELixelsDet;
    }
    
    public void setListaNetKDELixelsDet(ArrayList<NetKDELixelDet> listaNetKDELixelsDet) {
        this.listaNetKDELixelsDet = listaNetKDELixelsDet;
    }
    
    public void addRedLineaDet(NetKDELixelDet netKDELixelDet) {
        this.listaNetKDELixelsDet.add(netKDELixelDet);
    }
    
    public void addDensidadLixel(double densidadLixel) {
        this.densidadLixel += densidadLixel;
    }
    
    public void addDensidadLixelParcial(double densidadLixelParcial) {
        this.densidadLixelParcial += densidadLixelParcial;
    }
    
    @Override
    public NetKDELixel clone() throws CloneNotSupportedException {
        return (NetKDELixel)super.clone();
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int)(this.idLixel ^ (this.idLixel >>> 32));
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NetKDELixel other = (NetKDELixel)obj;
        return this.idLixel == other.idLixel;
    }
}
