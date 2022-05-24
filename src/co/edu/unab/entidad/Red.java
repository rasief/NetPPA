package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla redes
 * @author Feisar Moreno
 * @date 03/03/2016
 */
public class Red {
    private long idRed;
    private String descRed;
    private SistemaCoordenadas sistemaCoordenadas;
    private String fechaCrea;
    private int indCierreNodos;
    private double distCierreNodos;
    private int cantAtributos;
    private int cantLineas;
    private double largoRed;
    
    public Red() {
    }
    
    public Red(long idRed, String descRed, SistemaCoordenadas sistemaCoordenadas) {
        this.idRed = idRed;
        this.descRed = descRed;
        this.sistemaCoordenadas = sistemaCoordenadas;
    }
    
    public Red(ResultSet rs) {
        try {
            this.idRed = rs.getLong("ID_RED");
        } catch (SQLException e) {
            this.idRed = 0L;
        }
        
        try {
            this.descRed = rs.getString("DESC_RED");
        } catch (SQLException e) {
            this.descRed = "";
        }
        
        try {
            this.fechaCrea = rs.getString("FECHA_CREA");
        } catch (SQLException e) {
            this.fechaCrea = "";
        }
        
        try {
            this.indCierreNodos = rs.getInt("IND_CIERRE_NODOS");
        } catch (SQLException e) {
            this.indCierreNodos = 0;
        }
        
        try {
            this.distCierreNodos = rs.getDouble("DIST_CIERRE_NODOS");
        } catch (SQLException e) {
            this.distCierreNodos = 0;
        }
        
        try {
            this.cantAtributos = rs.getInt("CANT_ATRIBUTOS");
        } catch (SQLException e) {
            this.cantAtributos = 0;
        }
        
        try {
            this.cantLineas = rs.getInt("CANT_LINEAS");
        } catch (SQLException e) {
            this.cantLineas = 0;
        }
        
        try {
            this.largoRed = rs.getDouble("LARGO_RED");
        } catch (SQLException e) {
            this.largoRed = 0;
        }
        
        this.sistemaCoordenadas = new SistemaCoordenadas(rs);
    }
    
    public long getIdRed() {
        return idRed;
    }
    
    public void setIdRed(long idRed) {
        this.idRed = idRed;
    }
    
    public String getDescRed() {
        return descRed;
    }
    
    public void setDescRed(String descRed) {
        this.descRed = descRed;
    }
    
    public SistemaCoordenadas getSistemaCoordenadas() {
        return sistemaCoordenadas;
    }
    
    public void setSistemaCoordenadas(SistemaCoordenadas sistemaCoordenadas) {
        this.sistemaCoordenadas = sistemaCoordenadas;
    }
    
    public String getFechaCrea() {
        return fechaCrea;
    }
    
    public void setFechaCrea(String fechaCrea) {
        this.fechaCrea = fechaCrea;
    }
    
    public int getIndCierreNodos() {
        return indCierreNodos;
    }
    
    public void setIndCierreNodos(int indCierreNodos) {
        this.indCierreNodos = indCierreNodos;
    }
    
    public double getDistCierreNodos() {
        return distCierreNodos;
    }
    
    public void setDistCierreNodos(double distCierreNodos) {
        this.distCierreNodos = distCierreNodos;
    }
    
    public int getCantAtributos() {
        return cantAtributos;
    }
    
    public void setCantAtributos(int cantAtributos) {
        this.cantAtributos = cantAtributos;
    }
    
    public int getCantLineas() {
        return cantLineas;
    }
    
    public void setCantLineas(int cantLineas) {
        this.cantLineas = cantLineas;
    }
    
    public double getLargoRed() {
        return largoRed;
    }
    
    @Override
    public String toString() {
        return this.descRed;
    }
}
