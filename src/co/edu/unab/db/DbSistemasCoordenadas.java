package co.edu.unab.db;

import co.edu.unab.entidad.SistemaCoordenadas;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase para el manejo de la tabla sistemas_coordenadas
 * @author Feisar Moreno
 * @date 24/02/2016
 */
public class DbSistemasCoordenadas extends DbConexion {
    /**
     * Método privado que realiza una consulta sql y retorna una lista de registros.
     * @author Feisar Moreno
     * @date 24/02/2016
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> con los registros que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<SistemaCoordenadas> getListaSistemasCoordenadas(String sql) throws SQLException {
        try {
            crearConexion();
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<SistemaCoordenadas> listaSistemasCoordenadas = new ArrayList<>();
            while (rst.next()) {
                SistemaCoordenadas sistemaAux = new SistemaCoordenadas(rst);
                listaSistemasCoordenadas.add(sistemaAux);
            }
            rst.close();
            
            return listaSistemasCoordenadas;
        } finally {
            cerrarConexion();
        }
    }
    
    /**
     * Método que retorna los sistemas de coordenadas.
     * @author Feisar Moreno
     * @date 24/02/2016
     * @return <code>ArrayList</code> con los sistemas de coordenadas.
     */
    public ArrayList<SistemaCoordenadas> getListaSistemasCoordenadas() {
        try {
            String sql =
                "SELECT * FROM sistemas_coordenadas " +
                "ORDER BY nombre_sistema";
            
            return getListaSistemasCoordenadas(sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
}
