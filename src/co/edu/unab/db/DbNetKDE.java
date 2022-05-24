package co.edu.unab.db;

import co.edu.unab.entidad.EventoPunto;
import co.edu.unab.entidad.NetKDELixel;
import co.edu.unab.entidad.NetKDELixelDet;
import co.edu.unab.entidad.NetKDEResultado;
import co.edu.unab.entidad.ParCoordenadas;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Clase para el manejo de las tablas NetKDE
 * @author Feisar Moreno
 * @date 12/06/2016
 */
public class DbNetKDE extends DbConexion {
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de resultados NetKDE.
     * @author Feisar Moreno
     * @date 12/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<NetKDEResultado> getListaNetKDEResultados(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<NetKDEResultado> listaNetKDEResultados = new ArrayList<>();
            while (rst.next()) {
                NetKDEResultado netKDEResultadoAux = new NetKDEResultado(rst);
                listaNetKDEResultados.add(netKDEResultadoAux);
            }
            rst.close();
            
            return listaNetKDEResultados;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un objeto de resultado NetKDE.
     * @author Feisar Moreno
     * @date 12/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto de resultado NetKDE.
     * @throws SQLException
     */
    private NetKDEResultado getNetKDEResultado(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            NetKDEResultado netKDEResultado = new NetKDEResultado();
            if (rst.next()) {
                netKDEResultado = new NetKDEResultado(rst);
            }
            rst.close();
            
            return netKDEResultado;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de lixels NetKDE.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<NetKDELixel> getListaNetKDELixels(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<NetKDELixel> listaNetKDELixels = new ArrayList<>();
            while (rst.next()) {
                NetKDELixel netKDELixelAux = new NetKDELixel(rst);
                listaNetKDELixels.add(netKDELixelAux);
            }
            rst.close();
            
            return listaNetKDELixels;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de detalle de lixels NetKDE.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<NetKDELixelDet> getListaNetKDELixelsDet(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<NetKDELixelDet> listaNetKDELixelsDet = new ArrayList<>();
            while (rst.next()) {
                NetKDELixelDet netKDELixelDetAux = new NetKDELixelDet(rst);
                listaNetKDELixelsDet.add(netKDELixelDetAux);
            }
            rst.close();
            
            return listaNetKDELixelsDet;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un objeto de par de coordenadas.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto de resultado ParCoordenadas.
     * @throws SQLException
     */
    private ParCoordenadas getParCoordenadas(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ParCoordenadas parCoordenadas = new ParCoordenadas();
            if (rst.next()) {
                parCoordenadas = new ParCoordenadas(rst);
            }
            rst.close();
            
            return parCoordenadas;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de resultados NetKDE.
     * @author Feisar Moreno
     * @date 12/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return <code>ArrayList</code> con todos los registros de resultados NetKDE.
     */
    public ArrayList<NetKDEResultado> getListaNetKDEResultados(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT NR.*, E.desc_evento, FN.nombre_funcion, " +
                "DATE_FORMAT(NR.fecha_resultado, '%d/%m/%Y %h:%i:%s %p') AS fecha_resultado_t " +
                "FROM netkde_resultados NR " +
                "INNER JOIN eventos E ON NR.id_evento=E.id_evento " +
                "INNER JOIN funciones_nucleo FN ON NR.id_funcion=FN.id_funcion " +
                "WHERE NR.id_evento=" + idEvento + " " +
                "ORDER BY NR.id_netkde";
            
            return getListaNetKDEResultados(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un registro de resultado NetKDE.
     * @author Feisar Moreno
     * @date 12/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro de resultado
     * @return Objeto de resultado NetKDE.
     */
    public NetKDEResultado getNetKDEResultado(boolean conectado, long idNetKDE) {
        try {
            String sql =
                "SELECT NR.*, E.desc_evento, FN.nombre_funcion, " +
                "DATE_FORMAT(NR.fecha_resultado, '%d/%m/%Y %h:%i:%s %p') AS fecha_resultado_t " +
                "FROM netkde_resultados NR " +
                "INNER JOIN eventos E ON NR.id_evento=E.id_evento " +
                "INNER JOIN funciones_nucleo FN ON NR.id_funcion=FN.id_funcion " +
                "WHERE NR.id_netkde=" + idNetKDE;
            
            return getNetKDEResultado(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new NetKDEResultado();
        }
    }
    
    /**
     * Método que retorna un ArrayList con los registros de resultados NetKDE correspondientes al mismo evento de un registro de resultados dado.
     * @author Feisar Moreno
     * @date 17/09/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del resultado de NetKDE
     * @return <code>ArrayList</code> con todos los registros de resultados NetKDE que cumplan la condición dada.
     */
    public ArrayList<NetKDEResultado> getListaNetKDEResultadosOtros(boolean conectado, long idNetKDE) {
        try {
            String sql =
                "SELECT R2.*, F.nombre_funcion, " +
                "DATE_FORMAT(R2.fecha_resultado, '%d/%m/%Y %h:%i:%s %p') AS fecha_resultado_t " +
                "FROM netkde_resultados R1 " +
                "INNER JOIN netkde_resultados R2 ON R1.id_evento=R2.id_evento AND R1.id_netkde<>R2.id_netkde " +
                "INNER JOIN funciones_nucleo F ON R2.id_funcion=F.id_funcion " +
                "WHERE R1.id_netkde=" + idNetKDE + " " +
                "ORDER BY R2.filtros_resultado";
            
            return getListaNetKDEResultados(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de lixels NetKDE.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del resultado NetKDE
     * @return <code>ArrayList</code> con los registros de lixels NetKDE del resultado dado.
     */
    public ArrayList<NetKDELixel> getListaNetKDELixels(boolean conectado, long idNetKDE) {
        try {
            String sql =
                "SELECT * " +
                "FROM netkde_lixels " +
                "WHERE id_netkde=" + idNetKDE + " " +
                "ORDER BY id_lixel";
            
            return getListaNetKDELixels(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de detalle de lixels NetKDE.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del resultado NetKDE
     * @return <code>ArrayList</code> con los registros de detalle de lixels NetKDE del resultado dado.
     */
    public ArrayList<NetKDELixelDet> getListaNetKDELixelsDet(boolean conectado, long idNetKDE) {
        try {
            String sql =
                "SELECT LD.*, L.id_linea " +
                "FROM netkde_lixels_det LD " +
                "INNER JOIN netkde_lixels L ON LD.id_netkde=L.id_netkde AND LD.id_lixel=L.id_lixel " +
                "WHERE LD.id_netkde=" + idNetKDE + " " +
                "ORDER BY L.id_linea, LD.id_lixel, LD.num_punto";
            
            return getListaNetKDELixelsDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de detalle de lixels NetKDE para un grupo de segmentos con coordenadas geográficas ajustadas a coordenadas planas.
     * @author Feisar Moreno
     * @date 07/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del resultado NetKDE
     * @param latitudMin Latitud mínima del grupo de datos
     * @param longitudMin Longitud mínima del grupo de datos
     * @return <code>ArrayList</code> con los registros de detalle de lixels NetKDE del resultado dado.
     */
    public ArrayList<NetKDELixelDet> getListaNetKDELixelsDet(boolean conectado, long idNetKDE, double latitudMin, double longitudMin) {
        try {
            String sql =
                "SELECT LD.id_netkde, LD.id_lixel, LD.num_punto, LD.largo_segmento, L.id_linea, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", LD.latitud, " + longitudMin + ", 0, 1) AS latitud, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", " + latitudMin + ", LD.longitud, 0, 1) AS longitud " +
                "FROM netkde_lixels_det LD " +
                "INNER JOIN netkde_lixels L ON LD.id_netkde=L.id_netkde AND LD.id_lixel=L.id_lixel " +
                "WHERE LD.id_netkde=" + idNetKDE + " " +
                "ORDER BY L.id_linea, LD.id_lixel, LD.num_punto";
            
            return getListaNetKDELixelsDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un registro de par de coordenadas de lixels.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro de resultado
     * @return Objeto de resultado ParCoordenadas.
     */
    public ParCoordenadas getParCoordenadasNetKDE(boolean conectado, long idNetKDE) {
        try {
            String sql =
                "SELECT MIN(latitud) AS latitud_min, MIN(longitud) AS longitud_min, " +
                "MAX(latitud) AS latitud_max, MAX(longitud) AS longitud_max " +
                "FROM netkde_lixels_det " +
                "WHERE id_netkde=" + idNetKDE;
            
            return getParCoordenadas(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ParCoordenadas();
        }
    }
    
    /**
     * Método que crea un registro temporal de resultado para NetKDE
     * @author Feisar Moreno
     * @date 12/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @param anchoBanda Ancho de banda
     * @param largoLixel Longitud de líxel
     * @param idFuncion Identificador de la función de núcleo
     * @param cantPuntos Cantidad de puntos utilizados en la aplicación del método
     * @param filtrosResultado Texto con los valores de los filtros aplicados sobre los eventos
     * @return Identificador del registro temporal de resultados.
     */
    public long crearTmpKNetResultado(boolean conectado, long idEvento, double anchoBanda, double largoLixel, int idFuncion, int cantPuntos, String filtrosResultado) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_tmp_netkde_resultado(?,?,?,?,?,?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
                cstmt.setDouble(2, anchoBanda);
                cstmt.setDouble(3, largoLixel);
                cstmt.setInt(4, idFuncion);
                cstmt.setInt(5, cantPuntos);
                cstmt.setString(6, filtrosResultado);
                cstmt.registerOutParameter(7, Types.BIGINT);
                cstmt.execute();
                resultado = cstmt.getLong(7);
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
    
    /**
     * Método que crea varios registros temporales de lixels
     * @author Feisar Moreno
     * @date 13/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro temporal de resultados
     * @param idRed Identificador de la red
     * @param mapaNetKDELixels <code>LinkedHashMap</code> que contiene los lixels a crear
     * @return <code>true</code> si se pudieron crear los registros, de lo contrario <code>false</code>.
     */
    public boolean crearTmpNetKDELixels(boolean conectado, long idNetKDE, long idRed, LinkedHashMap<Long, NetKDELixel> mapaNetKDELixels) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            //Se recorre el mapa de lixels
            String sqlBase =
                    "INSERT INTO tmp_netkde_lixels " +
                    "(id_netkde, id_lixel, id_red, id_linea, num_punto, lat_lxcenter, lon_lxcenter, largo_lixel, cantidad_puntos, densidad_lixel) VALUES ";
            String sql = "";
            int contAux = 0;
            for (long idLixelAux : mapaNetKDELixels.keySet()) {
                NetKDELixel netKDELixelAux = mapaNetKDELixels.get(idLixelAux);
                
                if (contAux % 100 == 0 && contAux != 0) {
                    try (CallableStatement cstmt = conn.prepareCall(sql)) {
                        cstmt.execute();
                    }
                    sql = "";
                }
                if (sql.equals("")) {
                    sql = sqlBase;
                } else {
                    sql += ", ";
                }
                sql +=
                        "(" + idNetKDE + ", " + idLixelAux + ", " + idRed + ", " +
                        netKDELixelAux.getIdLinea() + ", " + netKDELixelAux.getNumPunto() + ", " +
                        netKDELixelAux.getLatLxCenter() + ", " + netKDELixelAux.getLonLxCenter() + ", " +
                        netKDELixelAux.getLargoLixel() + ", " + netKDELixelAux.getCantidadPuntos() + ", " +
                        netKDELixelAux.getDensidadLixel() + ")";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que crea varios registros temporales de detalle de lixels
     * @author Feisar Moreno
     * @date 13/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro temporal de resultados
     * @param mapaNetKDELixelsDet <code>LinkedHashMap</code> que contiene los detalles de lixel a crear
     * @return <code>true</code> si se pudieron crear los registros, de lo contrario <code>false</code>.
     */
    public boolean crearTmpNetKDELixelsDet(boolean conectado, long idNetKDE, LinkedHashMap<Long, LinkedHashMap<Long, NetKDELixelDet>> mapaNetKDELixelsDet) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            //Se recorre el mapa de lixels
            String sqlBase =
                    "INSERT INTO tmp_netkde_lixels_det " +
                    "(id_netkde, id_lixel, num_punto, latitud, longitud, largo_segmento) VALUES ";
            String sql = "";
            int contAux = 0;
            for (long idLixelAux : mapaNetKDELixelsDet.keySet()) {
                LinkedHashMap<Long, NetKDELixelDet> mapaNetKDELixelsDetAux = mapaNetKDELixelsDet.get(idLixelAux);
                
                for (long numPuntoAux : mapaNetKDELixelsDetAux.keySet()) {
                    NetKDELixelDet netKDELixelDetAux = mapaNetKDELixelsDetAux.get(numPuntoAux);
                    
                    if (contAux % 100 == 0 && contAux != 0) {
                        try (CallableStatement cstmt = conn.prepareCall(sql)) {
                            cstmt.execute();
                        }
                        sql = "";
                    }
                    if (sql.equals("")) {
                        sql = sqlBase;
                    } else {
                        sql += ", ";
                    }
                    sql +=
                            "(" + idNetKDE + ", " + idLixelAux + ", " + numPuntoAux + ", " +
                            netKDELixelDetAux.getLatitud() + ", " + netKDELixelDetAux.getLongitud() + ", " +
                            netKDELixelDetAux.getLargoSegmento() + ")";
                    
                    contAux++;
                }
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que crea varios registros temporales de puntos (eventos) asociados a lixels
     * @author Feisar Moreno
     * @date 13/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro temporal de resultados
     * @param mapaNetKDELixelsPuntos <code>LinkedHashMap</code> que contiene los puntos (eventos) asociados a crear
     * @return <code>true</code> si se pudieron crear los registros, de lo contrario <code>false</code>.
     */
    public boolean crearTmpNetKDELixelsPuntos(boolean conectado, long idNetKDE, LinkedHashMap<Long, LinkedHashMap<Long, EventoPunto>> mapaNetKDELixelsPuntos) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            //Se recorre el mapa de lixels
            String sqlBase =
                    "INSERT INTO tmp_netkde_lixels_puntos " +
                    "(id_netkde, id_lixel, id_evento, id_punto) VALUES ";
            String sql = "";
            int contAux = 0;
            for (long idLixelAux : mapaNetKDELixelsPuntos.keySet()) {
                LinkedHashMap<Long, EventoPunto> mapaNetKDELixelsPuntosAux = mapaNetKDELixelsPuntos.get(idLixelAux);
                
                for (long idPuntoAux : mapaNetKDELixelsPuntosAux.keySet()) {
                    EventoPunto eventoPuntoAux = mapaNetKDELixelsPuntosAux.get(idPuntoAux);
                    
                    if (contAux % 100 == 0 && contAux != 0) {
                        try (CallableStatement cstmt = conn.prepareCall(sql)) {
                            cstmt.execute();
                        }
                        sql = "";
                    }
                    if (sql.equals("")) {
                        sql = sqlBase;
                    } else {
                        sql += ", ";
                    }
                    sql +=
                            "(" + idNetKDE + ", " + idLixelAux + ", " +
                            eventoPuntoAux.getIdEvento() + ", " + eventoPuntoAux.getIdPunto() + ")";
                    
                    contAux++;
                }
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que crea un registro de resultado para NetKDE
     * @author Feisar Moreno
     * @date 12/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro temporal de resultados
     * @return Identificador del registro de resultados.
     */
    public long crearNetKDEResultado(boolean conectado, long idNetKDE) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_netkde_resultado(?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idNetKDE);
                cstmt.registerOutParameter(2, Types.BIGINT);
                cstmt.execute();
                resultado = cstmt.getLong(2);
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
    
    /**
     * Método que borra un registro de resultado para NetKDE
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idNetKDE Identificador del registro temporal de resultados
     * @return 1 si se pudo borrar el registro, -1 si se presenta un error a nivel de programa, -2 si se presenta un error a nivel de base de datos.
     */
    public int borrarNetKDEResultado(boolean conectado, long idNetKDE) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_borrar_netkde_resultado(?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idNetKDE);
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
