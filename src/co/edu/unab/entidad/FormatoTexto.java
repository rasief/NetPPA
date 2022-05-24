package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla formatos_texto
 * @author Feisar Moreno
 * @date 07/07/2016
 */
public class FormatoTexto {
    private long idFormato;
    private String textoFormato;
    private String formato;
    private String tipoFormato;
    private int orden;
    private int indActivo;
    
    public FormatoTexto() {
    }
    
    public FormatoTexto(long idFormato, String textoFormato, String formato, String tipoFormato, int orden, int indActivo) {
        this.idFormato = idFormato;
        this.textoFormato = textoFormato;
        this.formato = formato;
        this.tipoFormato = tipoFormato;
        this.orden = orden;
        this.indActivo = indActivo;
    }
    
    public FormatoTexto(ResultSet rs) {
        try {
            this.idFormato = rs.getLong("ID_FORMATO");
        } catch (SQLException e) {
            this.idFormato = 0L;
        }
        
        try {
            this.textoFormato = rs.getString("TEXTO_FORMATO");
        } catch (SQLException e) {
            this.textoFormato = "";
        }
        
        try {
            this.formato = rs.getString("FORMATO");
        } catch (SQLException e) {
            this.formato = "";
        }
        
        try {
            this.tipoFormato = rs.getString("TIPO_FORMATO");
        } catch (SQLException e) {
            this.tipoFormato = "";
        }
        
        try {
            this.orden = rs.getInt("ORDEN");
        } catch (SQLException e) {
            this.orden = 0;
        }
        
        try {
            this.indActivo = rs.getInt("IND_ACTIVO");
        } catch (SQLException e) {
            this.indActivo = 0;
        }
    }
    
    public long getIdFormato() {
        return idFormato;
    }
    
    public void setIdFormato(long idFormato) {
        this.idFormato = idFormato;
    }
    
    public String getTextoFormato() {
        return textoFormato;
    }
    
    public void setTextoFormato(String textoFormato) {
        this.textoFormato = textoFormato;
    }
    
    public String getFormato() {
        return formato;
    }
    
    public void setFormato(String formato) {
        this.formato = formato;
    }
    
    public String getTipoFormato() {
        return tipoFormato;
    }
    
    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }
    
    public int getOrden() {
        return orden;
    }
    
    public void setOrden(int orden) {
        this.orden = orden;
    }
    
    public int getIndActivo() {
        return indActivo;
    }
    
    public void setIndActivo(int indActivo) {
        this.indActivo = indActivo;
    }
    
    @Override
    public String toString() {
        return this.textoFormato;
    }
}
