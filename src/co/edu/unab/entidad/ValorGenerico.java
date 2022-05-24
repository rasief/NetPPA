package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro genérico con valores
 * @author Feisar Moreno
 * @date 02/03/2016
 */
public class ValorGenerico {
    private long valorEntero;
    private double valorDecimal;
    private String valorTexto;
    
    public ValorGenerico() {
    }
    
    public ValorGenerico(ResultSet rs) {
        try {
            this.valorEntero = rs.getLong("VALOR_ENTERO");
        } catch (SQLException e) {
            this.valorEntero = 0L;
        }
        
        try {
            this.valorDecimal = rs.getDouble("VALOR_DECIMAL");
        } catch (SQLException e) {
            this.valorDecimal = 0L;
        }
        
        try {
            this.valorTexto = rs.getString("VALOR_TEXTO");
        } catch (SQLException e) {
            this.valorTexto = "";
        }
    }
    
    public long getValorEntero() {
        return valorEntero;
    }
    
    public void setValorEntero(long valorEntero) {
        this.valorEntero = valorEntero;
    }
    
    public double getValorDecimal() {
        return valorDecimal;
    }
    
    public void setValorDecimal(double valorDecimal) {
        this.valorDecimal = valorDecimal;
    }
    
    public String getValorTexto() {
        return valorTexto;
    }
    
    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }
}
