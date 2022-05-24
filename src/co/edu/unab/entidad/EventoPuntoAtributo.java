package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla eventos_puntos_atributos
 * @author Feisar Moreno
 * @date 02/07/2016
 */
public class EventoPuntoAtributo {
    private long idEvento;
    private long idPunto;
    private long idAtributo;
    private double valorNum;
    private String valorTex;
    
    public EventoPuntoAtributo() {
    }
    
    public EventoPuntoAtributo(long idEvento, long idPunto, long idAtributo, double valorNum, String valorTex) {
        this.idEvento = idEvento;
        this.idPunto = idPunto;
        this.idAtributo = idAtributo;
        this.valorNum = valorNum;
        this.valorTex = valorTex;
    }
    
    public EventoPuntoAtributo(ResultSet rs) {
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
            this.idPunto = rs.getLong("ID_ATRIBUTO");
        } catch (SQLException e) {
            this.idPunto = 0L;
        }
        
        try {
            this.valorNum = rs.getDouble("VALOR_NUM");
        } catch (SQLException e) {
            this.valorNum = 0;
        }
        
        try {
            this.valorTex = rs.getString("VALOR_TEX");
        } catch (SQLException e) {
            this.valorTex = "";
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
