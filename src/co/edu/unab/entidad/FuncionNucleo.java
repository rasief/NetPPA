package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de función de núcleo
 * @author Feisar Moreno
 * @date 09/06/2016
 */
public class FuncionNucleo {
    private int idFuncion;
    private String nombreFuncion;
    
    public FuncionNucleo() {
    }
    
    public FuncionNucleo(ResultSet rs) {
        try {
            this.idFuncion = rs.getInt("ID_FUNCION");
        } catch (SQLException e) {
            this.idFuncion = 0;
        }
        
        try {
            this.nombreFuncion = rs.getString("NOMBRE_FUNCION");
        } catch (SQLException e) {
            this.nombreFuncion = "";
        }
    }
    
    public int getIdFuncion() {
        return idFuncion;
    }
    
    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }
    
    public String getNombreFuncion() {
        return nombreFuncion;
    }
    
    public void setNombreFuncion(String nombreFuncion) {
        this.nombreFuncion = nombreFuncion;
    }
    
    @Override
    public String toString() {
        return this.nombreFuncion;
    }
}
