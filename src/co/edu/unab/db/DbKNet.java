package co.edu.unab.db;

import co.edu.unab.entidad.KNetResultado;
import co.edu.unab.entidad.KNetValor;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Clase para el manejo de las tablas knet_resultado y knet_detalle
 * @author Feisar Moreno
 * @date 16/04/2016
 */
public class DbKNet extends DbConexion {
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de resultados KNet.
     * @author Feisar Moreno
     * @date 16/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<KNetResultado> getListaKNetResultados(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<KNetResultado> listaKNetResultados = new ArrayList<>();
            while (rst.next()) {
                KNetResultado kNetResultadoAux = new KNetResultado(rst);
                listaKNetResultados.add(kNetResultadoAux);
            }
            rst.close();
            
            return listaKNetResultados;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un objeto de resultado KNet.
     * @author Feisar Moreno
     * @date 09/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto de resultado KNet.
     * @throws SQLException
     */
    private KNetResultado getKNetResultado(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            KNetResultado kNetResultado = new KNetResultado();
            if (rst.next()) {
                kNetResultado = new KNetResultado(rst);
            }
            rst.close();
            
            return kNetResultado;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de valores KNet.
     * @author Feisar Moreno
     * @date 16/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<KNetValor> getListaKNetValores(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<KNetValor> listaKNetValores = new ArrayList<>();
            while (rst.next()) {
                KNetValor kNetValorAux = new KNetValor(rst);
                listaKNetValores.add(kNetValorAux);
            }
            rst.close();
            
            return listaKNetValores;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de resultados KNet.
     * @author Feisar Moreno
     * @date 16/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return <code>ArrayList</code> con todos los registros de resultados KNet.
     */
    public ArrayList<KNetResultado> getListaKNetResultados(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT KR.*, E.desc_evento, " +
                "DATE_FORMAT(KR.fecha_resultado, '%d/%m/%Y %h:%i:%s %p') AS fecha_resultado_t " +
                "FROM knet_resultados KR " +
                "INNER JOIN eventos E ON KR.id_evento=E.id_evento " +
                "WHERE KR.id_evento=" + idEvento + " " +
                "ORDER BY KR.id_knet";
            
            return getListaKNetResultados(conectado, sql);
        } catch (Exception e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un registro de resultado KNet.
     * @author Feisar Moreno
     * @date 09/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idKNet Identificador del registro de resultado
     * @return Objeto de resultado KNet.
     */
    public KNetResultado getKNetResultado(boolean conectado, long idKNet) {
        try {
            String sql =
                "SELECT KR.*, E.desc_evento, " +
                "DATE_FORMAT(KR.fecha_resultado, '%d/%m/%Y %h:%i:%s %p') AS fecha_resultado_t " +
                "FROM knet_resultados KR " +
                "INNER JOIN eventos E ON KR.id_evento=E.id_evento " +
                "WHERE KR.id_knet=" + idKNet;
            
            return getKNetResultado(conectado, sql);
        } catch (Exception e) {
            System.out.println(e.toString());
            return new KNetResultado();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de valores KNet.
     * @author Feisar Moreno
     * @date 09/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idKNet Identificador del registro de resultado
     * @return <code>ArrayList</code> con todos los registros de valores KNet.
     */
    public ArrayList<KNetValor> getListaKNetValores(boolean conectado, long idKNet) {
        try {
            String sql =
                "SELECT * FROM knet_valores " +
                "WHERE id_knet=" + idKNet + " " +
                "ORDER BY distancia_knet";
            
            return getListaKNetValores(conectado, sql);
        } catch (Exception e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que crea un registro temporal de resultado para la función K para redes
     * @author Feisar Moreno
     * @date 08/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @param distanciaIni Distancia inicial de cálculo
     * @param distanciaFin Distancia final de cálculo
     * @param incrementoDist Incremento de distancia entre cálculos
     * @param cantPuntos Cantidad de puntos utilizados en el cálculo
     * @param cantAleatorios Cantidad de grupos de eventos aleatorios a generar
     * @param filtrosResultado Texto con los valores de los filtros aplicados sobre los eventos
     * @return Identificador del registro temporal de resultados.
     */
    public long crearTmpKNetResultado(boolean conectado, long idEvento, double distanciaIni, double distanciaFin, double incrementoDist, int cantPuntos, int cantAleatorios, String filtrosResultado) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_tmp_knet_resultado(?,?,?,?,?,?,?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
                cstmt.setDouble(2, distanciaIni);
                cstmt.setDouble(3, distanciaFin);
                cstmt.setDouble(4, incrementoDist);
                cstmt.setInt(5, cantPuntos);
                cstmt.setInt(6, cantAleatorios);
                cstmt.setString(7, filtrosResultado);
                cstmt.registerOutParameter(8, Types.BIGINT);
                cstmt.execute();
                resultado = cstmt.getLong(8);
            }
            
            return resultado;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -1;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que crea un registro temporal de valores para la función K para redes
     * @author Feisar Moreno
     * @date 08/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idKNet Identificador del registro temporal de resultados
     * @param distanciaKNet Distancia utilizada para el cálculo
     * @param valor Valor del cálculo de la funcion K para redes
     * @param limiteMin Valor del cálculo de la funcion K para el valor aleatorio ubicado en el 2,5%
     * @param limiteMax Valor del cálculo de la funcion K para el valor aleatorio ubicado en el 97,5%
     * @return <code>true</code> si se pudo crear el registro, de lo contrario <code>false</code>.
     */
    public boolean crearTmpKNetValores(boolean conectado, long idKNet, double distanciaKNet, double valor, double limiteMin, double limiteMax) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_tmp_knet_valor(?,?,?,?,?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idKNet);
                cstmt.setDouble(2, distanciaKNet);
                cstmt.setDouble(3, valor);
                cstmt.setDouble(4, limiteMin);
                cstmt.setDouble(5, limiteMax);
                cstmt.registerOutParameter(6, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(6);
            }
            
            return resultado > 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que crea un registro de resultado para la función K para redes
     * @author Feisar Moreno
     * @date 08/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idKNet Identificador del registro temporal de resultados
     * @return Identificador del registro de resultados.
     */
    public long crearKNetResultado(boolean conectado, long idKNet) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_knet_resultado(?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idKNet);
                cstmt.registerOutParameter(2, Types.BIGINT);
                cstmt.execute();
                resultado = cstmt.getLong(2);
            }
            
            return resultado;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -1;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que borra un registro de resultado para la función K para redes
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idKNet Identificador del registro de resultados
     * @return 1 si se pudo borrar el registro, -1 si se presenta un error a nivel de programa, -2 si se presenta un error a nivel de base de datos.
     */
    public int borrarKNetResultado(boolean conectado, long idKNet) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_borrar_knet_resultado(?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idKNet);
                cstmt.registerOutParameter(2, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(2);
            }
            
            return resultado;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return -1;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
}
