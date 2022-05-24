package co.edu.unab.db;

import co.edu.unab.entidad.*;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase para el manejo de la tabla funciones_nucleo
 * @author Feisar Moreno
 * @date 09/06/2016
 */
public class DbFuncionesNucleo extends DbConexion {
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de funciones de núcleo.
     * @author Feisar Moreno
     * @date 09/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<FuncionNucleo> getListaFuncionesNucleo(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<FuncionNucleo> listaFuncionesNucleo = new ArrayList<>();
            while (rst.next()) {
                FuncionNucleo funcionNucleoAux = new FuncionNucleo(rst);
                listaFuncionesNucleo.add(funcionNucleoAux);
            }
            rst.close();
            
            return listaFuncionesNucleo;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de funciones de nucleo.
     * @author Feisar Moreno
     * @date 09/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param indActivo Indicador de registro activo (1. Sí - 0. No)
     * @return <code>ArrayList</code> con los registros de funciones de núcleo.
     */
    public ArrayList<FuncionNucleo> getListaFuncionesNucleo(boolean conectado, int indActivo) {
        try {
            String sql = "SELECT * FROM funciones_nucleo ";
            switch (indActivo) {
                case 1:
                    sql += "WHERE ind_activo=1 ";
                    break;
                case 0:
                    sql += "WHERE ind_activo=0 ";
                    break;
            }
            sql += "ORDER BY nombre_funcion";
            
            return getListaFuncionesNucleo(conectado, sql);
        } catch (Exception e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
}
