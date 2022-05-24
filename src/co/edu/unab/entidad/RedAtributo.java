package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla redes_atributos
 * @author Feisar Moreno
 * @date 02/03/2016
 */
public class RedAtributo {
    private long idRed;
    private long idAtributo;
    private String nombreAtributo;
    private String tipoAtributo;
    
    public RedAtributo() {
    }
    
    public RedAtributo(long idRed, long idAtributo, String nombreAtributo, String tipoAtributo) {
        this.idRed = idRed;
        this.idAtributo = idAtributo;
        this.nombreAtributo = nombreAtributo;
        this.tipoAtributo = tipoAtributo;
    }
    
    public RedAtributo(ResultSet rs) {
        try {
            this.idRed = rs.getLong("ID_RED");
        } catch (SQLException e) {
            this.idRed = 0L;
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
    
    public long getIdRed() {
        return idRed;
    }
    
    public void setIdRed(long idRed) {
        this.idRed = idRed;
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
    
    @Override
    public String toString() {
        return this.nombreAtributo;
    }
}
