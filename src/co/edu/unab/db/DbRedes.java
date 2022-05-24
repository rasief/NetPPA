package co.edu.unab.db;

import co.edu.unab.entidad.*;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Clase para el manejo de la tabla redes y sus tablas de detalle
 * @author Feisar Moreno
 * @date 25/02/2016
 */
public class DbRedes extends DbConexion {
    /**
     * Método privado que realiza una consulta sql y retorna un registro de red.
     * @author Feisar Moreno
     * @date 03/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto <code>Red</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private Red getRed(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            Red red = new Red();
            if (rst.next()) {
                red = new Red(rst);
            }
            rst.close();
            
            return red;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un registro de red_linea.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto <code>RedLinea</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private RedLinea getRedLinea(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            RedLinea redLinea = new RedLinea();
            if (rst.next()) {
                redLinea = new RedLinea(rst);
            }
            rst.close();
            
            return redLinea;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un registro de red_linea_det.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto <code>RedLineaDet</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private RedLineaDet getRedLineaDet(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            RedLineaDet redLineaDet = new RedLineaDet();
            if (rst.next()) {
                redLineaDet = new RedLineaDet(rst);
            }
            rst.close();
            
            return redLineaDet;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de red.
     * @author Feisar Moreno
     * @date 03/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<Red> getListaRedes(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<Red> listaRedes = new ArrayList<>();
            while (rst.next()) {
                Red redAux = new Red(rst);
                listaRedes.add(redAux);
            }
            rst.close();
            
            return listaRedes;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de atributos de red.
     * @author Feisar Moreno
     * @date 26/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<RedAtributo> getListaRedesAtributos(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<RedAtributo> listaRedesAtributos = new ArrayList<>();
            while (rst.next()) {
                RedAtributo redAtributoAux = new RedAtributo(rst);
                listaRedesAtributos.add(redAtributoAux);
            }
            rst.close();
            
            return listaRedesAtributos;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de líneas de red.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<RedLinea> getListaRedesLineas(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<RedLinea> listaRedesLineas = new ArrayList<>();
            while (rst.next()) {
                RedLinea redLineaAux = new RedLinea(rst);
                listaRedesLineas.add(redLineaAux);
            }
            rst.close();
            
            return listaRedesLineas;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de atributos de líneas de red.
     * @author Feisar Moreno
     * @date 26/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<RedLineaAtributo> getListaRedesLineasAtributos(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<RedLineaAtributo> listaRedesLineasAtributos = new ArrayList<>();
            while (rst.next()) {
                RedLineaAtributo redLineaAtributoAux = new RedLineaAtributo(rst);
                listaRedesLineasAtributos.add(redLineaAtributoAux);
            }
            rst.close();
            
            return listaRedesLineasAtributos;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de detalle de líneas de red.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<RedLineaDet> getListaRedesLineasDet(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<RedLineaDet> listaRedesLineasDet = new ArrayList<>();
            while (rst.next()) {
                RedLineaDet redLineaDetAux = new RedLineaDet(rst);
                listaRedesLineasDet.add(redLineaDetAux);
            }
            rst.close();
            
            return listaRedesLineasDet;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un atributo de red.
     * @author Feisar Moreno
     * @date 02/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto <code>RedAtributo</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private RedAtributo getRedAtributo(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            RedAtributo redAtributo = new RedAtributo();
            if (rst.next()) {
                redAtributo = new RedAtributo(rst);
            }
            rst.close();
            
            return redAtributo;
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
     * Método que retorna un registro de red.
     * @author Feisar Moreno
     * @date 03/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return Objeto <code>Red</code> de la red con el identificador dado.
     */
    public Red getRed(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT R.*, RA.cant_atributos, RL.cant_lineas, RL.largo_red, " +
                "SC.nombre_sistema, UM.id_unidad, UM.factor_metros, UM.ind_grados " +
                "FROM redes R " +
                "INNER JOIN sistemas_coordenadas SC ON R.id_sistema=SC.id_sistema " +
                "INNER JOIN unidades_medida UM ON SC.id_unidad=UM.id_unidad " +
                "INNER JOIN (" +
                "    SELECT id_red, COUNT(*) AS cant_atributos " +
                "    FROM redes_atributos " +
                "    GROUP BY id_red" +
                ") RA ON R.id_red=RA.id_red " +
                "INNER JOIN (" +
                "    SELECT id_red, MAX(largo_acumulado) AS largo_red, COUNT(*) AS cant_lineas " +
                "    FROM redes_lineas " +
                "    GROUP BY id_red" +
                ") RL ON R.id_red=RL.id_red " +
                "WHERE R.id_red=" + idRed;
            
            return getRed(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new Red();
        }
    }
    
    /**
     * Método que retorna un registro de red_linea.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param idLinea Identificador de la línea
     * @return Objeto <code>RedLinea</code> de la red con el identificador dado.
     */
    public RedLinea getRedLinea(boolean conectado, long idRed, long idLinea) {
        try {
            String sql =
                "SELECT RL.* " +
                "FROM redes_lineas RL " +
                "WHERE RL.id_red=" + idRed + " " +
                "AND RL.id_linea=" + idLinea;
            
            return getRedLinea(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new RedLinea();
        }
    }
    
    /**
     * Método que retorna un registro de red_linea_det.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param idLinea Identificador de la línea
     * @param numPunto Número del punto
     * @return Objeto <code>RedLineaDet</code> de la red con el identificador dado.
     */
    public RedLineaDet getRedLineaDet(boolean conectado, long idRed, long idLinea, long numPunto) {
        try {
            String sql =
                "SELECT LD.* " +
                "FROM redes_lineas_det LD " +
                "WHERE LD.id_red=" + idRed + " " +
                "AND LD.id_linea=" + idLinea + " " +
                "AND LD.num_punto=" + numPunto;
            
            return getRedLineaDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new RedLineaDet();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de red.
     * @author Feisar Moreno
     * @date 03/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @return <code>ArrayList</code> con todos los registros de red.
     */
    public ArrayList<Red> getListaRedes(boolean conectado) {
        try {
            String sql =
                "SELECT R.*, RA.cant_atributos, RL.cant_lineas " +
                "FROM redes R " +
                "INNER JOIN (" +
                "    SELECT id_red, COUNT(*) AS cant_atributos " +
                "    FROM redes_atributos " +
                "    GROUP BY id_red" +
                ") RA ON R.id_red=RA.id_red " +
                "INNER JOIN (" +
                "    SELECT id_red, COUNT(*) AS cant_lineas " +
                "    FROM redes_lineas " +
                "    GROUP BY id_red" +
                ") RL ON R.id_red=RL.id_red " +
                "ORDER BY R.desc_red";
            
            return getListaRedes(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de atributos de red.
     * @author Feisar Moreno
     * @date 26/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return <code>ArrayList</code> con todos los atributos de una red.
     */
    public ArrayList<RedAtributo> getListaRedesAtributos(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT * FROM redes_atributos " +
                "WHERE id_red=" + idRed + " " +
                "ORDER BY id_atributo";
            
            return getListaRedesAtributos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de líneas de red.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return <code>ArrayList</code> con todos los registros de red.
     */
    public ArrayList<RedLinea> getListaRedesLineas(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT * " +
                "FROM redes_lineas " +
                "WHERE id_red=" + idRed + " " +
                "ORDER BY id_linea";
            
            return getListaRedesLineas(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de atributos de líneas de red.
     * @author Feisar Moreno
     * @date 26/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param idAtributo Identificador del atributo
     * @return <code>ArrayList</code> con todos los registros de atributos.
     */
    public ArrayList<RedLineaAtributo> getListaRedesLineasAtributos(boolean conectado, long idRed, long idAtributo) {
        try {
            String sql =
                "SELECT * " +
                "FROM redes_lineas_atributos " +
                "WHERE id_red=" + idRed + " " +
                "AND id_atributo=" + idAtributo + " " +
                "ORDER BY id_linea";
            
            return getListaRedesLineasAtributos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de detalle de líneas de red.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return <code>ArrayList</code> con todos los registros de red.
     */
    public ArrayList<RedLineaDet> getListaRedesLineasDet(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT * " +
                "FROM redes_lineas_det " +
                "WHERE id_red=" + idRed + " " +
                "ORDER BY id_linea, num_punto";
            
            return getListaRedesLineasDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de detalle de líneas de red para un grupo de líneas con coordenadas geográficas ajustadas a coordenadas planas.
     * @author Feisar Moreno
     * @date 07/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param latitudMin Latitud mínima del grupo de datos
     * @param longitudMin Longitud mínima del grupo de datos
     * @return <code>ArrayList</code> con todos los registros de red.
     */
    public ArrayList<RedLineaDet> getListaRedesLineasDet(boolean conectado, long idRed, double latitudMin, double longitudMin) {
        try {
            String sql =
                "SELECT id_red, id_linea, num_punto, largo_segmento, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", latitud, " + longitudMin + ", 0, 1) AS latitud, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", " + latitudMin + ", longitud, 0, 1) AS longitud " +
                "FROM redes_lineas_det " +
                "WHERE id_red=" + idRed + " " +
                "ORDER BY id_linea, num_punto";
            
            return getListaRedesLineasDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de nodos en los que se encuentran dos o más registros de detalle de líneas de red.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return <code>ArrayList</code> con todos los registros de red.
     */
    public ArrayList<RedLineaDet> getListaRedesLineasDetNodos(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT LD.*, D2.id_linea AS id_linea_2, D2.num_punto AS num_punto_2 " +
                "FROM redes_lineas_det LD " +
                "INNER JOIN redes_lineas_det D2 ON LD.id_red=D2.id_red AND LD.latitud=D2.latitud AND LD.longitud=D2.longitud " +
                "WHERE LD.id_red=" + idRed + " " +
                "AND LD.id_linea<>D2.id_linea " +
                "ORDER BY LD.id_linea, LD.num_punto, D2.id_linea, D2.num_punto";
            
            return getListaRedesLineasDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de detalle de líneas de red, solo se retornan los puntos extremos de cada línea.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return <code>ArrayList</code> con todos los registros de red.
     */
    public ArrayList<RedLineaDet> getListaRedesLineasDetExtremos(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT LD.* " +
                "FROM redes_lineas_det LD " +
                "WHERE LD.id_red=" + idRed + " " +
                "AND LD.num_punto IN (1, (" +
                    "SELECT MAX(num_punto) " +
                    "FROM redes_lineas_det " +
                    "WHERE id_red=LD.id_red " +
                    "AND id_linea=LD.id_linea" +
                ")) " +
                "ORDER BY LD.id_linea, LD.num_punto";
            
            return getListaRedesLineasDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList con los nodos que se encuentran a una distancia dada o menor (pero mayor a cero) de una coordenada dada.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param idLinea Identificador de la línea base (no se tendrán en cuenta los nodos de esta línea)
     * @param latitud Latitud del punto base
     * @param longitud Longitud del punto base
     * @param distancia Distancia máxima de búsqueda en metros
     * @param factorMetros Factor de conversión a metros de la red
     * @param indGrados Indica si la red utiliza coordenadas en grados
     * @return <code>ArrayList</code> con los registros que cumplan las condiciones dadas.
     */
    public ArrayList<RedLineaDet> getListaRedesLineasDetDist(boolean conectado, long idRed, long idLinea, double latitud, double longitud, double distancia, double factorMetros, int indGrados) {
        try {
            String sql =
                "SELECT id_linea, num_punto " +
                "FROM (" +
                    "SELECT id_linea, num_punto, " +
                    "fu_calcular_largo_linea_puntos(" + latitud + ", " + longitud + ", latitud, longitud, " + factorMetros + ", " + indGrados + ") AS distancia " +
                    "FROM redes_lineas_det " +
                    "WHERE id_red=" + idRed + " " +
                    "AND id_linea<>" + idLinea + " " +
                ") T " +
                "WHERE distancia>0 " +
                "AND distancia<=" + distancia + " " +
                "ORDER BY id_linea, num_punto";
            
            return getListaRedesLineasDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList con las líneas que se encuentran a una distancia dada o menor de una coordenada dada.
     * @author Feisar Moreno
     * @date 08/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param latitud Latitud del punto base
     * @param longitud Longitud del punto base
     * @param distancia Distancia máxima de búsqueda en metros
     * @param factorLatitud Factor aproximado de conversión de la latitud a metros
     * @param factorLongitud Factor aproximado de conversión de la longitud a metros
     * @param factorMetros Factor de conversión a metros de la red
     * @param indGrados Indica si la red utiliza coordenadas en grados
     * @return <code>ArrayList</code> con los registros que cumplan las condiciones dadas.
     */
    public ArrayList<RedLineaDet> getListaRedesLineasDetDist(boolean conectado, long idRed, double latitud, double longitud, double distancia, double factorLatitud, double factorLongitud, double factorMetros, int indGrados) {
        try {
            String sql =
                "SELECT id_linea, num_punto, latitud, longitud, id_linea_2, num_punto_2, latitud_2, longitud_2, largo_segmento " +
                "FROM (" +
                    "SELECT LD1.id_linea, LD1.num_punto, LD1.latitud, LD1.longitud, LD2.id_linea AS id_linea_2, " +
                    "LD2.num_punto AS num_punto_2, LD2.latitud AS latitud_2, LD2.longitud AS longitud_2, " +
                    "fu_calcular_distancia_punto_linea(" + latitud + ", " + longitud + ", LD1.latitud, LD1.longitud, LD2.latitud, LD2.longitud, " + factorMetros + ", " + indGrados + ") AS largo_segmento " +
                    "FROM redes_lineas_det LD1 " +
                    "INNER JOIN redes_lineas_det LD2 ON LD1.id_red=LD2.id_red AND LD1.id_linea=LD2.id_linea " +
                    "INNER JOIN redes_lineas RL ON LD1.id_red=RL.id_red AND LD1.id_linea=RL.id_linea " +
                    "WHERE LD1.id_red=" + idRed + " " +
                    "AND LD2.num_punto=LD1.num_punto+1 " +
                    "AND RL.largo_linea>0 " +
                    "AND fu_estimar_distancia_punto_linea(" + latitud + ", " + longitud + ", LD1.latitud, LD1.longitud, LD2.latitud, LD2.longitud, " + factorLatitud + ", " + factorLongitud + ")<=(" + distancia + "*1.2)" +
                ") T " +
                "WHERE T.largo_segmento<=" + distancia + " " +
                "ORDER BY largo_segmento, id_linea, num_punto";
            
            return getListaRedesLineasDet(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que verifica si un nodo tiene conexión con una línea dada.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param idLinea Identificador de la línea del nodo
     * @param numPunto Número de punto del nodo
     * @param idLinea2 Identificador de la línea adyacente
     * @return <code>true</code> si el nodo tiene conexión con la línea, de lo contrario <code>false</code>.
     */
    public boolean isNodosConectados(boolean conectado, long idRed, long idLinea, long numPunto, long idLinea2) {
        try {
            String sql =
                "SELECT LD.id_linea " +
                "FROM redes_lineas_det LD " +
                "INNER JOIN redes_lineas_det LD2 ON LD.id_red=LD2.id_red AND LD.latitud=LD2.latitud AND LD.longitud=LD2.longitud " +
                "WHERE LD.id_red=" + idRed + " " +
                "AND LD.id_linea=" + idLinea + " " +
                "AND LD.num_punto=" + numPunto + " " +
                "AND LD2.id_linea=" + idLinea2 + " " +
                "AND LD.id_linea<>LD2.id_linea";
            
            ArrayList<RedLineaDet> listaAux = getListaRedesLineasDet(conectado, sql);
            return (listaAux.size() > 0);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
    /**
     * Método que retorna un atributo de red por su nombre.
     * @author Feisar Moreno
     * @date 02/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param nombreAtributo Nombre del atributo
     * @return Objeto <code>RedAtributo</code> de la red con el nombre dado.
     */
    public RedAtributo getTmpRedAtributo(boolean conectado, long idRed, String nombreAtributo) {
        try {
            String sql =
                "SELECT * FROM tmp_redes_atributos " +
                "WHERE id_red=" + idRed + " " +
                "AND nombre_atributo='" + nombreAtributo + "'";
            
            return getRedAtributo(true, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new RedAtributo();
        }
    }
    
    /**
     * Método que retorna la cantidad de líneas cargadas en un temporar de red.
     * @author Feisar Moreno
     * @date 02/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return Objeto <code>ValorGenerico</code> con los valores requeridos.
     */
    public ValorGenerico getCantidadTmpLineas(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT COUNT(*) AS valor_entero " +
                "FROM tmp_redes_lineas " +
                "WHERE id_red=" + idRed;
            
            return getValorGenerico(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ValorGenerico();
        }
    }
    
    /**
     * Método que retorna la cantidad de atributos de líneas cargados en un temporar de red.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return Objeto <code>ValorGenerico</code> con los valores requeridos.
     */
    public ValorGenerico getCantidadTmpLineasAtributos(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT COUNT(*) AS valor_entero " +
                "FROM tmp_redes_lineas_atributos " +
                "WHERE id_red=" + idRed;
            
            return getValorGenerico(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ValorGenerico();
        }
    }
    
    /**
     * Método que retorna la cantidad de detalles de líneas cargados en un temporar de red.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return Objeto <code>ValorGenerico</code> con los valores requeridos.
     */
    public ValorGenerico getCantidadTmpLineasDet(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT COUNT(*) AS valor_entero " +
                "FROM tmp_redes_lineas_det " +
                "WHERE id_red=" + idRed;
            
            return getValorGenerico(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ValorGenerico();
        }
    }
    
    /**
     * Método que retorna un registro de par de coordenadas de una red.
     * @author Feisar Moreno
     * @date 19/06/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return Objeto de resultado ParCoordenadas.
     */
    public ParCoordenadas getParCoordenadasRed(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT MIN(latitud) AS latitud_min, MIN(longitud) AS longitud_min, " +
                "MAX(latitud) AS latitud_max, MAX(longitud) AS longitud_max " +
                "FROM redes_lineas_det " +
                "WHERE id_red=" + idRed;
            
            return getParCoordenadas(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ParCoordenadas();
        }
    }
    
    /**
     * Método que crea un registro de la tabla tmp_red.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param red Red a crear
     * @return Identificador de la red temporal creada.
     */
    public long crearTmpRed(boolean conectado, Red red) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            //Se crea el registro temporal de la red
            String procAlmacenado = "{call pa_crear_tmp_red(?,?,?)}";
            long idRedTmp;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setString(1, red.getDescRed());
                cstmt.setLong(2, red.getSistemaCoordenadas().getIdSistema());
                cstmt.registerOutParameter(3, Types.BIGINT);
                cstmt.execute();
                idRedTmp = cstmt.getLong(3);
            }
            
            return idRedTmp;
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
     * Método que inserta los registros que conforman una red temporal.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRedTmp Identificador de la red temporal
     * @param listaTmpRedesAtributos ArrayList con los atributos de la red
     * @param listaTmpRedesLineas ArrayList con las líneas de la red
     * @param listaTmpRedesLineasAtributos ArrayList con los valores de atributos de cada línea de la red
     * @param listaTmpRedesLineasDet ArrayList con los segmentos de cada línea de la red
     * @return <code>true</code> si se crearon todos los registros, de lo contrario <code>false</code>.
     */
    public boolean crearTmpRedComponentes(boolean conectado, long idRedTmp, ArrayList<RedAtributo> listaTmpRedesAtributos, ArrayList<RedLinea> listaTmpRedesLineas, ArrayList<RedLineaAtributo> listaTmpRedesLineasAtributos, ArrayList<RedLineaDet> listaTmpRedesLineasDet) {
        String sql = "";
        try {
            if (!conectado) {
                crearConexion();
            }
            
            //Se agregan los atributos de la red
            String sqlBase =
                    "INSERT INTO tmp_redes_atributos " +
                    "(id_red, id_atributo, nombre_atributo, tipo_atributo) VALUES ";
            sql = "";
            int contAux = 0;
            for (RedAtributo redAtributoAux : listaTmpRedesAtributos) {
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
                        "(" + idRedTmp + ", " + redAtributoAux.getIdAtributo() + ", '" +
                        redAtributoAux.getNombreAtributo() + "', '" + redAtributoAux.getTipoAtributo() + "')";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            //Se agregan las líneas de la red
            sqlBase =
                    "INSERT INTO tmp_redes_lineas " +
                    "(id_red, id_linea) VALUES ";
            sql = "";
            contAux = 0;
            for (RedLinea redLineaAux : listaTmpRedesLineas) {
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
                        "(" + idRedTmp + ", " + redLineaAux.getIdLinea() + ")";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            //Se agregan los atributos de las líneas de la red
            sqlBase =
                    "INSERT INTO tmp_redes_lineas_atributos " +
                    "(id_red, id_linea, id_atributo, valor_num, valor_tex) VALUES ";
            sql = "";
            contAux = 0;
            for (RedLineaAtributo redLineaAtributoAux : listaTmpRedesLineasAtributos) {
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
                        "(" + idRedTmp + ", " + redLineaAtributoAux.getIdLinea() + ", " +
                        redLineaAtributoAux.getIdAtributo() + ", " + redLineaAtributoAux.getValorNum() + ", '" +
                        redLineaAtributoAux.getValorTex().replaceAll("'", " ") + "')";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            //Se agregan los detalles de las líneas de la red
            sqlBase =
                    "INSERT INTO tmp_redes_lineas_det " +
                    "(id_red, id_linea, num_punto, latitud, longitud) VALUES ";
            sql = "";
            contAux = 0;
            for (RedLineaDet redLineaDetAux : listaTmpRedesLineasDet) {
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
                        "(" + idRedTmp + ", " + redLineaDetAux.getIdLinea() + ", " +
                        redLineaDetAux.getNumPunto() + ", " + redLineaDetAux.getLatitud() + ", " +
                        redLineaDetAux.getLongitud() + ")";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            return true;
        } catch (SQLException e) {
            System.out.println(sql);
            System.out.println(e.toString());
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que crea una red a partir de la información guardada en las tablas temporales.
     * @author Feisar Moreno
     * @date 02/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red en las tablas temporales
     * @return Identificador de la red creada.
     */
    public long crearRed(boolean conectado, long idRed) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_red(?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idRed);
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
     * Método que edita las coordenadas de un detalle de línea de red
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red en las tablas temporales
     * @param idLinea Identificador de la línea
     * @param numPunto Número del punto
     * @param latitud Nueva latitud del punto
     * @param longitud Nueva longitud del punto
     * @return Valor <code>true</code> si se pudo modificar la información, de lo contrario <code>false</code>.
     */
    public boolean editarRedLineaDetCoordenadas(boolean conectado, long idRed, long idLinea, long numPunto, double latitud, double longitud) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_editar_red_linea_det_coordenadas(?,?,?,?,?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idRed);
                cstmt.setLong(2, idLinea);
                cstmt.setLong(3, numPunto);
                cstmt.setDouble(4, latitud);
                cstmt.setDouble(5, longitud);
                cstmt.registerOutParameter(6, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(6);
            }
            
            return (resultado > 0);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que calcula las longitudes de las líneas de la red
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red en las tablas temporales
     * @param indAutocommit Indicador de autocommit (1. Sí - 0. No)
     * @return Valor <code>true</code> si se pudo realizar el cálculo, de lo contrario <code>false</code>.
     */
    public boolean calcularLargoLineasRed(boolean conectado, long idRed, int indAutocommit) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_calcular_largos_lineas_red(?,?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idRed);
                cstmt.setInt(2, indAutocommit);
                cstmt.registerOutParameter(3, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(3);
            }
            
            return (resultado > 0);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que edita el estado de cierre de una red y quita la marca de proyectado de los eventos asociados si hubo modificaciones en la red.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red en las tablas temporales
     * @param indCierreNodos Indicador de nodos cerrados (1. Sí - 0. No)
     * @param distCierreNodos Distancia en metros utilizada en el cierre de los nodos
     * @param cantCierres Cantidad de cierres realizados
     * @return Valor <code>true</code> si se pudo realizar el cierre de nodos, de lo contrario <code>false</code>.
     */
    public boolean editarRedCierreNodos(boolean conectado, long idRed, int indCierreNodos, double distCierreNodos, int cantCierres) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_editar_red_cierre_nodos(?,?,?,?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idRed);
                cstmt.setInt(2, indCierreNodos);
                if (indCierreNodos != 0) {
                    cstmt.setDouble(3, distCierreNodos);
                } else {
                    cstmt.setNull(3, Types.DOUBLE);
                }
                cstmt.setInt(4, cantCierres);
                cstmt.registerOutParameter(5, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(5);
            }
            
            return (resultado > 0);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que borra una red.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return 1 si se pudo borrar el registro, -1 si se presenta un error a nivel de programa, -2 o menos si se presenta un error a nivel de base de datos.
     */
    public int borrarRed(boolean conectado, long idRed) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_borrar_red(?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idRed);
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
