package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla eventos_atributos
 * @author Feisar Moreno
 * @date 04/03/2016
 */
public class EventoAtributo {
    private long idEvento;
    private long idAtributo;
    private String nombreAtributo;
    private String tipoAtributo;
    private int indNegacion;
    
    public EventoAtributo() {
    }
    
    public EventoAtributo(long idEvento, long idAtributo, String nombreAtributo, String tipoAtributo) {
        this.idEvento = idEvento;
        this.idAtributo = idAtributo;
        this.nombreAtributo = nombreAtributo;
        this.tipoAtributo = tipoAtributo;
    }
    
    public EventoAtributo(long idEvento, long idAtributo, String nombreAtributo, String tipoAtributo, int indNegacion) {
        this.idEvento = idEvento;
        this.idAtributo = idAtributo;
        this.nombreAtributo = nombreAtributo;
        this.tipoAtributo = tipoAtributo;
        this.indNegacion = indNegacion;
    }
    
    public EventoAtributo(ResultSet rs) {
        try {
            this.idEvento = rs.getLong("ID_EVENTO");
        } catch (SQLException e) {
            this.idEvento = 0L;
        }
        
        try {
            this.idAtributo = rs.getLong("ID_ATRIBUTO");
        } catch (SQLException e) {
            this.idAtributo = 0L;
        }
        
        try {
            this.nombreAtributo = rs.getString("NOMBRE_ATRIBUTO");
        } catch (SQLException e) {
            this.nombreAtributo = "";
        }
        
        try {
            this.tipoAtributo = rs.getString("TIPO_ATRIBUTO");
        } catch (SQLException e) {
            this.tipoAtributo = "";
        }
    }
    
    public long getIdEvento() {
        return idEvento;
    }
    
    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }
    
    public long getIdAtributo() {
        return idAtributo;
    }
    
    public void setIdAtributo(long idAtributo) {
        this.idAtributo = idAtributo;
    }
    
    public String getNombreAtributo() {
        return nombreAtributo;
    }
    
    public void setNombreAtributo(String nombreAtributo) {
        this.nombreAtributo = nombreAtributo;
    }
    
    public String getTipoAtributo() {
        return tipoAtributo;
    }
    
    public void setTipoAtributo(String tipoAtributo) {
        this.tipoAtributo = tipoAtributo;
    }
    
    public int getIndNegacion() {
        return indNegacion;
    }
    
    public void setIndNegacion(int indNegacion) {
        this.indNegacion = indNegacion;
    }
    
    @Override
    public String toString() {
        return this.nombreAtributo;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.idEvento ^ (this.idEvento >>> 32));
        hash = 23 * hash + (int) (this.idAtributo ^ (this.idAtributo >>> 32));
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventoAtributo other = (EventoAtributo) obj;
        if (this.idEvento != other.idEvento) {
            return false;
        } else if (this.idAtributo != other.idAtributo) {
            return false;
        }
        return true;
    }
}
