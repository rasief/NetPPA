package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla redes_lineas_atributos
 * @author Feisar Moreno
 * @date 26/06/2016
 */
public class RedLineaAtributo {
    private long idRed;
    private long idLinea;
    private long idAtributo;
    private double valorNum;
    private String valorTex;
    
    public RedLineaAtributo() {
    }
    
    public RedLineaAtributo(long idRed, long idLinea, long idAtributo, double valorNum, String valorTex) {
        this.idRed = idRed;
        this.idLinea = idLinea;
        this.idAtributo = idAtributo;
        this.valorNum = valorNum;
        this.valorTex = valorTex;
    }
    
    public RedLineaAtributo(ResultSet rs) {
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
            this.idAtributo = rs.getLong("ID_ATRIBUTO");
        } catch (SQLException e) {
            this.idAtributo = 0L;
        }
        
        try {
            this.valorNum = rs.getDouble("VALOR_NUM");
        } catch (SQLException e) {
            this.valorNum = 0L;
        }
        
        try {
            this.valorTex = rs.getString("VALOR_TEX");
        } catch (SQLException e) {
            this.valorTex = "";
        }
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
    
    public long getIdAtributo() {
        return idAtributo;
    }
    
    public void setIdAtributo(long idAtributo) {
        this.idAtributo = idAtributo;
    }
    
    public double getValorNum() {
        return valorNum;
    }
    
    public void setValorNum(double valorNum) {
        this.valorNum = valorNum;
    }
    
    public String getValorTex() {
        return valorTex;
    }
    
    public void setValorTex(String valorTex) {
        this.valorTex = valorTex;
    }
}
