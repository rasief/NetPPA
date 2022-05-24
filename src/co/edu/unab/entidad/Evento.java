package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla eventos
 * @author Feisar Moreno
 * @date 04/03/2016
 */
public class Evento {
    private long idEvento;
    private String descEvento;
    private Red red;
    private String fechaCrea;
    private int indProy;
    private double distProy;
    private long idAtributoFecha;
    private long idAtributoHora;
    private int cantAtributos;
    private int cantPuntos;
    
    public Evento() {
    }
    
    public Evento(long idEvento, String descEvento, Red red) {
        this.idEvento = idEvento;
        this.descEvento = descEvento;
        this.red = red;
    }
    
    public Evento(ResultSet rs) {
        try {
            this.idEvento = rs.getLong("ID_EVENTO");
        } catch (SQLException e) {
            this.idEvento = 0L;
        }
        
        try {
            this.descEvento = rs.getString("DESC_EVENTO");
        } catch (SQLException e) {
            this.descEvento = "";
        }
        
        try {
            this.fechaCrea = rs.getString("FECHA_CREA");
        } catch (SQLException e) {
            this.fechaCrea = "";
        }
        
        try {
            this.indProy = rs.getInt("IND_PROY");
        } catch (SQLException e) {
            this.indProy = 0;
        }
        
        try {
            this.distProy = rs.getInt("DIST_PROY");
        } catch (SQLException e) {
            this.distProy = 0;
        }
        
        try {
            this.idAtributoFecha = rs.getLong("ID_ATRIBUTO_FECHA");
        } catch (SQLException e) {
            this.idAtributoFecha = 0;
        }
        
        try {
            this.idAtributoHora = rs.getLong("ID_ATRIBUTO_HORA");
        } catch (SQLException e) {
            this.idAtributoHora = 0;
        }
        
        try {
            this.cantAtributos = rs.getInt("CANT_ATRIBUTOS");
        } catch (SQLException e) {
            this.cantAtributos = 0;
        }
        
        try {
            this.cantPuntos = rs.getInt("CANT_PUNTOS");
        } catch (SQLException e) {
            this.cantPuntos = 0;
        }
        
        this.red = new Red(rs);
    }
    
    public long getIdEvento() {
        return idEvento;
    }
    
    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }
    
    public String getDescEvento() {
        return descEvento;
    }
    
    public void setDescEvento(String descEvento) {
        this.descEvento = descEvento;
    }
    
    public Red getRed() {
        return red;
    }
    
    public void setRed(Red red) {
        this.red = red;
    }
    
    public String getFechaCrea() {
        return fechaCrea;
    }
    
    public void setFechaCrea(String fechaCrea) {
        this.fechaCrea = fechaCrea;
    }
    
    public int getIndProy() {
        return indProy;
    }
    
    public void setIndProy(int indProy) {
        this.indProy = indProy;
    }
    
    public double getDistProy() {
        return distProy;
    }
    
    public void setDistProy(double distProy) {
        this.distProy = distProy;
    }
    
    public long getIdAtributoFecha() {
        return idAtributoFecha;
    }
    
    public void setIdAtributoFecha(long idAtributoFecha) {
        this.idAtributoFecha = idAtributoFecha;
    }
    
    public long getIdAtributoHora() {
        return idAtributoHora;
    }
    
    public void setIdAtributoHora(long idAtributoHora) {
        this.idAtributoHora = idAtributoHora;
    }
    
    public int getCantAtributos() {
        return cantAtributos;
    }
    
    public void setCantAtributos(int cantAtributos) {
        this.cantAtributos = cantAtributos;
    }
    
    public int getCantPuntos() {
        return cantPuntos;
    }
    
    public void setCantPuntos(int cantPuntos) {
        this.cantPuntos = cantPuntos;
    }
    
    @Override
    public String toString() {
        return this.descEvento;
    }
}
