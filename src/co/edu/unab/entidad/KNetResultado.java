package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla knet_resultados
 * @author Feisar Moreno
 * @date 16/04/2016
 */
public class KNetResultado {
    private long idKNet;
    private Evento evento;
    private double distanciaIni;
    private double distanciaFin;
    private double incrementoDist;
    private int cantPuntos;
    private int cantAleatorios;
    private String fechaResultado;
    private String filtrosResultado;
    
    public KNetResultado() {
    }
    
    public KNetResultado(ResultSet rs) {
        try {
            this.idKNet = rs.getLong("ID_KNET");
        } catch (SQLException e) {
            this.idKNet = 0L;
        }
        
        this.evento = new Evento(rs);
        
        try {
            this.distanciaIni = rs.getDouble("DISTANCIA_INI");
        } catch (SQLException e) {
            this.distanciaIni = 0;
        }
        
        try {
            this.distanciaFin = rs.getDouble("DISTANCIA_FIN");
        } catch (SQLException e) {
            this.distanciaFin = 0;
        }
        
        try {
            this.incrementoDist = rs.getDouble("INCREMENTO_DIST");
        } catch (SQLException e) {
            this.incrementoDist = 0;
        }
        
        try {
            this.cantPuntos = rs.getInt("CANT_PUNTOS");
        } catch (SQLException e) {
            this.cantPuntos = 0;
        }
        
        try {
            this.cantAleatorios = rs.getInt("CANT_ALEATORIOS");
        } catch (SQLException e) {
            this.cantAleatorios = 0;
        }
        
        try {
            this.fechaResultado = rs.getString("FECHA_RESULTADO_T");
        } catch (SQLException e) {
            this.fechaResultado = "";
        }
        
        try {
            this.filtrosResultado = rs.getString("FILTROS_RESULTADO");
        } catch (SQLException e) {
            this.filtrosResultado = "";
        }
    }
    
    public long getIdKNet() {
        return idKNet;
    }
    
    public void setIdKNet(long idKNet) {
        this.idKNet = idKNet;
    }
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    
    public double getDistanciaIni() {
        return distanciaIni;
    }
    
    public void setDistanciaIni(double distanciaIni) {
        this.distanciaIni = distanciaIni;
    }
    
    public double getDistanciaFin() {
        return distanciaFin;
    }
    
    public void setDistanciaFin(double distanciaFin) {
        this.distanciaFin = distanciaFin;
    }
    
    public double getIncrementoDist() {
        return incrementoDist;
    }
    
    public void setIncrementoDist(double incrementoDist) {
        this.incrementoDist = incrementoDist;
    }
    
    public int getCantPuntos() {
        return cantPuntos;
    }
    
    public void setCantPuntos(int cantPuntos) {
        this.cantPuntos = cantPuntos;
    }
    
    public int getCantAleatorios() {
        return cantAleatorios;
    }
    
    public void setCantAleatorios(int cantAleatorios) {
        this.cantAleatorios = cantAleatorios;
    }
    
    public String getFechaResultado() {
        return fechaResultado;
    }
    
    public void setFechaResultado(String fechaResultado) {
        this.fechaResultado = fechaResultado;
    }
    
    public String getFiltrosResultado() {
        return filtrosResultado;
    }
    
    public void setFiltrosResultado(String filtrosResultado) {
        this.filtrosResultado = filtrosResultado;
    }
    
    public long getIdEvento() {
        if (this.evento != null) {
            return this.evento.getIdEvento();
        } else {
            return 0L;
        }
    }
}
