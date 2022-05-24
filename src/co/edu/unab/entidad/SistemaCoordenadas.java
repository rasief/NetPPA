package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla sistemas_coordenadas
 * @author Feisar Moreno
 * @date 24/02/2016
 */
public class SistemaCoordenadas {
    private long idSistema;
    private String nombreSistema;
    private UnidadMedida unidadMedida;
    
    public SistemaCoordenadas() {
    }
    
    public SistemaCoordenadas(ResultSet rs) {
        try {
            idSistema = rs.getLong("ID_SISTEMA");
        } catch (SQLException e) {
            idSistema = 0;
        }
        
        try {
            nombreSistema = rs.getString("NOMBRE_SISTEMA");
        } catch (SQLException e) {
            nombreSistema = "";
        }
        
        this.unidadMedida = new UnidadMedida(rs);
    }
    
    public long getIdSistema() {
        return idSistema;
    }
    
    public String getNombreSistema() {
        return nombreSistema;
    }
    
    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }
    
    @Override
    public String toString() {
        return this.getNombreSistema();
    }
}
