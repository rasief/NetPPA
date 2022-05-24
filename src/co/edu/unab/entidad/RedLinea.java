package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase que representa un registro de la tabla redes_lineas
 * @author Feisar Moreno
 * @date 24/04/2016
 */
public class RedLinea {
    private long idRed;
    private long idLinea;
    private double largoLinea;
    private double largoAcumulado;
    private ArrayList<RedLineaDet> listaRedesLineasDet = new ArrayList<>();
    
    public RedLinea() {
    }
    
    public RedLinea(long idRed, long idLinea) {
        this.idRed = idRed;
        this.idLinea = idLinea;
    }
    
    public RedLinea(ResultSet rs) {
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
            this.largoLinea = rs.getDouble("LARGO_LINEA");
        } catch (SQLException e) {
            this.largoLinea = 0L;
        }
        
        try {
            this.largoAcumulado = rs.getDouble("LARGO_ACUMULADO");
        } catch (SQLException e) {
            this.largoAcumulado = 0L;
        }
    }
    
    public long getIdRed() {
        return idRed;
    }
    
    public long getIdLinea() {
        return idLinea;
    }
    
    public double getLargoLinea() {
        return largoLinea;
    }
    
    public double getLargoAcumulado() {
        return largoAcumulado;
    }
    
    public ArrayList<RedLineaDet> getListaRedesLineasDet() {
        return listaRedesLineasDet;
    }
    
    public void setListaRedesLineasDet(ArrayList<RedLineaDet> listaRedesLineasDet) {
        this.listaRedesLineasDet = listaRedesLineasDet;
    }
    
    public void addRedLineaDet(RedLineaDet redLineaDet) {
        this.listaRedesLineasDet.add(redLineaDet);
    }
}
