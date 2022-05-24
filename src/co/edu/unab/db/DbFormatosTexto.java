package co.edu.unab.db;

import co.edu.unab.entidad.FormatoTexto;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase para el manejo de la tabla formatos_texto
 * @author Feisar Moreno
 * @date 07/07/2016
 */
public class DbFormatosTexto extends DbConexion {
    private ArrayList<FormatoTexto> getListaFormatosTexto(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<FormatoTexto> listaFormatosTexto = new ArrayList<>();
            while (rst.next()) {
                FormatoTexto formatoTextoAux = new FormatoTexto(rst);
                listaFormatosTexto.add(formatoTextoAux);
            }
            rst.close();
            
            return listaFormatosTexto;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que retorna los formatos de texto de un tipo específico.
     * @author Feisar Moreno
     * @date 07/07/2016
     * @param conectado Indicador de conectado a la base de datos
     * @param tipoFormato Tipo de formato
     * @param indActivo Indicador de activo (1. Sí - 0. No)
     * @return <code>ArrayList</code> con los formatos de texto.
     */
    public ArrayList<FormatoTexto> getListaFormatosTexto(boolean conectado, String tipoFormato, int indActivo) {
        try {
            String sql =
                    "SELECT * FROM formatos_texto " +
                    "WHERE tipo_formato='" + tipoFormato + "' ";
            switch (indActivo) {
                case 1:
                    sql += "AND ind_activo=1 ";
                    break;
                case 0:
                    sql += "AND ind_activo=0 ";
                    break;
            }
            sql += "ORDER BY orden";
            
            return getListaFormatosTexto(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
}
