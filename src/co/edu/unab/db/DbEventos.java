package co.edu.unab.db;

import co.edu.unab.entidad.*;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Clase para el manejo de la tabla eventos y sus tablas de detalle
 * @author Feisar Moreno
 * @date 04/03/2016
 */
public class DbEventos extends DbConexion {
    /**
     * Método privado que realiza una consulta sql y retorna un registro de evento.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto <code>Evento</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private Evento getEvento(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            Evento evento = new Evento();
            if (rst.next()) {
                evento = new Evento(rst);
            }
            rst.close();
            
            return evento;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de eventos.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<Evento> getListaEventos(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<Evento> listaEventos = new ArrayList<>();
            while (rst.next()) {
                Evento eventoAux = new Evento(rst);
                listaEventos.add(eventoAux);
            }
            rst.close();
            
            return listaEventos;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de eventos_atributos.
     * @author Feisar Moreno
     * @date 24/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<EventoAtributo> getListaEventosAtributos(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<EventoAtributo> listaEventosAtributos = new ArrayList<>();
            while (rst.next()) {
                EventoAtributo eventoAtributoAux = new EventoAtributo(rst);
                listaEventosAtributos.add(eventoAtributoAux);
            }
            rst.close();
            
            return listaEventosAtributos;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un ArrayList de registros de puntos de eventos.
     * @author Feisar Moreno
     * @date 24/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return <code>ArrayList</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private ArrayList<EventoPunto> getListaEventosPuntos(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            ArrayList<EventoPunto> listaEventosPuntos = new ArrayList<>();
            while (rst.next()) {
                EventoPunto eventoPuntoAux = new EventoPunto(rst);
                listaEventosPuntos.add(eventoPuntoAux);
            }
            rst.close();
            
            return listaEventosPuntos;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método privado que realiza una consulta sql y retorna un atributo de evento.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param sql Consulta SQL a ejecutar
     * @return Objeto <code>EventoAtributo</code> que cumplen con la consulta SQL.
     * @throws SQLException
     */
    private EventoAtributo getEventoAtributo(boolean conectado, String sql) throws SQLException {
        try {
            if (!conectado) {
                crearConexion();
            }
            pstm = conn.prepareStatement(sql);
            rst = pstm.executeQuery();
            
            EventoAtributo eventoAtributo = new EventoAtributo();
            if (rst.next()) {
                eventoAtributo = new EventoAtributo(rst);
            }
            rst.close();
            
            return eventoAtributo;
        } finally {
            if (!conectado) {
                cerrarConexion();
            }
        }
    }
    
    /**
     * Método que retorna un registro de evento.
     * @author Feisar Moreno
     * @date 03/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return Objeto <code>Evento</code> con el identificador dado.
     */
    public Evento getEvento(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT E.*, R.desc_red, R.ind_cierre_nodos, R.dist_cierre_nodos, EA.cant_atributos, EP.cant_puntos " +
                "FROM eventos E " +
                "INNER JOIN redes R ON E.id_red=R.id_red " +
                "INNER JOIN (" +
                "    SELECT id_evento, COUNT(*) AS cant_atributos " +
                "    FROM eventos_atributos " +
                "    GROUP BY id_evento" +
                ") EA ON E.id_evento=EA.id_evento " +
                "INNER JOIN (" +
                "    SELECT id_evento, COUNT(*) AS cant_puntos " +
                "    FROM eventos_puntos " +
                "    GROUP BY id_evento" +
                ") EP ON E.id_evento=EP.id_evento " +
                "WHERE E.id_evento=" + idEvento;
            
            return getEvento(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new Evento();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de eventos asociados a una red.
     * @author Feisar Moreno
     * @date 23/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @return <code>ArrayList</code> con todos los registros de eventos.
     */
    public ArrayList<Evento> getListaEventos(boolean conectado, long idRed) {
        try {
            String sql =
                "SELECT E.*, R.desc_red, R.ind_cierre_nodos, R.dist_cierre_nodos, EA.cant_atributos, EP.cant_puntos " +
                "FROM eventos E " +
                "INNER JOIN redes R ON E.id_red=R.id_red " +
                "INNER JOIN (" +
                "    SELECT id_evento, COUNT(*) AS cant_atributos " +
                "    FROM eventos_atributos " +
                "    GROUP BY id_evento" +
                ") EA ON E.id_evento=EA.id_evento " +
                "INNER JOIN (" +
                "    SELECT id_evento, COUNT(*) AS cant_puntos " +
                "    FROM eventos_puntos " +
                "    GROUP BY id_evento" +
                ") EP ON E.id_evento=EP.id_evento " +
                "WHERE E.id_red=" + idRed + " " +
                "ORDER BY E.desc_evento";
            
            return getListaEventos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de eventos.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @return <code>ArrayList</code> con todos los registros de eventos.
     */
    public ArrayList<Evento> getListaEventos(boolean conectado) {
        try {
            String sql =
                "SELECT E.*, R.desc_red, R.ind_cierre_nodos, R.dist_cierre_nodos, EA.cant_atributos, EP.cant_puntos " +
                "FROM eventos E " +
                "INNER JOIN redes R ON E.id_red=R.id_red " +
                "INNER JOIN (" +
                "    SELECT id_evento, COUNT(*) AS cant_atributos " +
                "    FROM eventos_atributos " +
                "    GROUP BY id_evento" +
                ") EA ON E.id_evento=EA.id_evento " +
                "INNER JOIN (" +
                "    SELECT id_evento, COUNT(*) AS cant_puntos " +
                "    FROM eventos_puntos " +
                "    GROUP BY id_evento" +
                ") EP ON E.id_evento=EP.id_evento " +
                "ORDER BY E.desc_evento";
            
            return getListaEventos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de puntos de eventos.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return <code>ArrayList</code> con todos los registros de eventos.
     */
    public ArrayList<EventoPunto> getListaEventosPuntos(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT * " +
                "FROM eventos_puntos " +
                "WHERE id_evento=" + idEvento + " " +
                "ORDER BY id_punto";
            
            return getListaEventosPuntos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de puntos de eventos para un grupo de puntos con coordenadas geográficas ajustadas a coordenadas planas.
     * @author Feisar Moreno
     * @date 07/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @param latitudMin Latitud mínima del grupo de datos
     * @param longitudMin Longitud mínima del grupo de datos
     * @return <code>ArrayList</code> con todos los registros de eventos.
     */
    public ArrayList<EventoPunto> getListaEventosPuntos(boolean conectado, long idEvento, double latitudMin, double longitudMin) {
        try {
            String sql =
                "SELECT id_evento, id_punto, fecha_punto, id_red, id_linea, num_punto, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", latitud, " + longitudMin + ", 0, 1) AS latitud, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", " + latitudMin + ", longitud, 0, 1) AS longitud, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", latitud_proy, " + longitudMin + ", 0, 1) AS latitud_proy, " +
                "fu_calcular_largo_linea_puntos(" + latitudMin + ", " + longitudMin + ", " + latitudMin + ", longitud_proy, 0, 1) AS longitud_proy " +
                "FROM eventos_puntos " +
                "WHERE id_evento=" + idEvento + " " +
                "ORDER BY id_punto";
            
            return getListaEventosPuntos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de puntos de eventos aplicando los filtros dados. Solo se retornan puntos con proyecciones válidas.
     * @author Feisar Moreno
     * @date 24/04/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @param mapaFiltros LinkedHashMap con los filtros a aplicar el la consulta SQL
     * @return <code>ArrayList</code> con todos los registros de eventos.
     */
    public ArrayList<EventoPunto> getListaEventosPuntosFiltros(boolean conectado, long idEvento, LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros) {
        //Se obtienen los datos del evento
        Evento evento = this.getEvento(conectado, idEvento);
        
        //Se obtienen los atributos de fecha y hora
        EventoAtributo eventoAtributoFecha = this.getEventoAtributo(conectado, idEvento, evento.getIdAtributoFecha());
        EventoAtributo eventoAtributoHora = this.getEventoAtributo(conectado, idEvento, evento.getIdAtributoHora());
        
        try {
            String sql =
                "SELECT EP.* " +
                "FROM eventos_puntos EP " +
                "WHERE EP.id_evento=" + idEvento + " " +
                "AND EP.latitud_proy IS NOT NULL " +
                "AND EP.longitud_proy IS NOT NULL ";
            if (mapaFiltros != null) {
                for (String tipoFiltro : mapaFiltros.keySet()) {
                    LinkedHashMap<EventoAtributo, List<String>> mapaValoresAux = mapaFiltros.get(tipoFiltro);
                    if (mapaValoresAux != null) {
                        for (EventoAtributo eventoAtributoAux : mapaValoresAux.keySet()) {
                            List<String> listaValoresAux = mapaValoresAux.get(eventoAtributoAux);
                            String nombreAtributoAux = eventoAtributoAux.getNombreAtributo();
                            String tipoAtributoAux = eventoAtributoAux.getTipoAtributo();
                            int indNegacion = eventoAtributoAux.getIndNegacion();
                            
                            if (listaValoresAux != null && listaValoresAux.size() > 0) {
                                if ((eventoAtributoFecha != null && nombreAtributoAux.equalsIgnoreCase(eventoAtributoFecha.getNombreAtributo())) ||
                                         (eventoAtributoHora != null && nombreAtributoAux.equalsIgnoreCase(eventoAtributoHora.getNombreAtributo()))) {
                                    String sqlFecha = "";
                                    if (eventoAtributoFecha != null && nombreAtributoAux.equalsIgnoreCase(eventoAtributoFecha.getNombreAtributo())) {
                                        switch (tipoFiltro) {
                                            case "listas":
                                                if (indNegacion == 0) {
                                                    sqlFecha += "DATE(EP.fecha_punto) IN (";
                                                } else {
                                                    sqlFecha += "DATE(EP.fecha_punto) NOT IN (";
                                                }
                                                String cadenaAux = "";
                                                for (String valorAux : listaValoresAux) {
                                                    if (!cadenaAux.equals("")) {
                                                        cadenaAux += ", ";
                                                    }
                                                    cadenaAux += "STR_TO_DATE('" + valorAux + "', '%d/%m/%Y')";
                                                }
                                                sqlFecha += cadenaAux + ")";
                                                break;
                                                
                                            case "rangos":
                                                if (indNegacion == 0) {
                                                    sqlFecha += "DATE(EP.fecha_punto) BETWEEN STR_TO_DATE('" + listaValoresAux.get(0) + "', '%d/%m/%Y') AND STR_TO_DATE('" + listaValoresAux.get(1) + "', '%d/%m/%Y')";
                                                } else {
                                                    sqlFecha += "DATE(EP.fecha_punto) NOT BETWEEN STR_TO_DATE('" + listaValoresAux.get(0) + "', '%d/%m/%Y') AND STR_TO_DATE('" + listaValoresAux.get(1) + "', '%d/%m/%Y')";
                                                }
                                                break;
                                                
                                            case "valores":
                                                if (indNegacion == 0) {
                                                    sqlFecha += "DATE(EP.fecha_punto)=STR_TO_DATE('" + listaValoresAux.get(0) + "', '%d/%m/%Y')";
                                                } else {
                                                    sqlFecha += "DATE(EP.fecha_punto)<>STR_TO_DATE('" + listaValoresAux.get(0) + "', '%d/%m/%Y')";
                                                }
                                                break;
                                        }
                                    }
                                    
                                    String sqlHora = "";
                                    if (eventoAtributoHora != null && nombreAtributoAux.equalsIgnoreCase(eventoAtributoHora.getNombreAtributo())) {
                                        switch (tipoFiltro) {
                                            case "listas":
                                                if (indNegacion == 0) {
                                                    sqlHora += "TIME(EP.fecha_punto) IN (";
                                                } else {
                                                    sqlHora += "TIME(EP.fecha_punto) NOT IN (";
                                                }
                                                String cadenaAux = "";
                                                for (String valorAux : listaValoresAux) {
                                                    if (!cadenaAux.equals("")) {
                                                        cadenaAux += ", ";
                                                    }
                                                    cadenaAux += "STR_TO_DATE('" + valorAux + "', '%H:%i')";
                                                }
                                                sqlHora += cadenaAux + ")";
                                                break;
                                                
                                            case "rangos":
                                                if (indNegacion == 0) {
                                                    sqlHora += "TIME(EP.fecha_punto) BETWEEN STR_TO_DATE('" + listaValoresAux.get(0) + "', '%H:%i') AND STR_TO_DATE('" + listaValoresAux.get(1) + "', '%H:%i')";
                                                } else {
                                                    sqlHora += "TIME(EP.fecha_punto) NOT BETWEEN STR_TO_DATE('" + listaValoresAux.get(0) + "', '%H:%i') AND STR_TO_DATE('" + listaValoresAux.get(1) + "', '%H:%i')";
                                                }
                                                break;
                                                
                                            case "valores":
                                                if (indNegacion == 0) {
                                                    sqlHora += "TIME(EP.fecha_punto)=STR_TO_DATE('" + listaValoresAux.get(0) + "', '%H:%i')";
                                                } else {
                                                    sqlHora += "TIME(EP.fecha_punto)<>STR_TO_DATE('" + listaValoresAux.get(0) + "', '%H:%i')";
                                                }
                                                break;
                                        }
                                    }
                                    
                                    if (!sqlFecha.equals("") && ! sqlHora.equals("")) {
                                        sql += "AND (" + sqlFecha + " OR " + sqlHora + ") ";
                                    } else if (!sqlFecha.equals("")) {
                                        sql += "AND " + sqlFecha + " ";
                                    } else if (!sqlHora.equals("")) {
                                        sql += "AND " + sqlHora + " ";
                                    }
                                } else {
                                    String cadenaAux = "";
                                    switch (tipoFiltro) {
                                        case "listas":
                                            for (String valorAux : listaValoresAux) {
                                                if (!cadenaAux.equals("")) {
                                                    cadenaAux += ", ";
                                                }
                                                if (tipoAtributoAux.equalsIgnoreCase("string")) {
                                                    cadenaAux += "'" + valorAux + "'";
                                                } else {
                                                    cadenaAux += valorAux;
                                                }
                                            }
                                            if (tipoAtributoAux.equalsIgnoreCase("string")) {
                                                if (indNegacion == 0) {
                                                    cadenaAux = "AND PA.valor_tex IN (" + cadenaAux + ") ";
                                                } else {
                                                    cadenaAux = "AND PA.valor_tex NOT IN (" + cadenaAux + ") ";
                                                }
                                            } else {
                                                if (indNegacion == 0) {
                                                    cadenaAux = "AND PA.valor_num IN (" + cadenaAux + ") ";
                                                } else {
                                                    cadenaAux = "AND PA.valor_num NOT IN (" + cadenaAux + ") ";
                                                }
                                            }
                                            break;
                                            
                                        case "rangos":
                                            if (tipoAtributoAux.equalsIgnoreCase("string")) {
                                                if (indNegacion == 0) {
                                                    cadenaAux = "AND PA.valor_tex BETWEEN '" + listaValoresAux.get(0) + "' AND '" + listaValoresAux.get(1) + "' ";
                                                } else {
                                                    cadenaAux = "AND PA.valor_tex NOT BETWEEN '" + listaValoresAux.get(0) + "' AND '" + listaValoresAux.get(1) + "' ";
                                                }
                                            } else {
                                                if (indNegacion == 0) {
                                                    cadenaAux = "AND PA.valor_num BETWEEN " + listaValoresAux.get(0) + " AND " + listaValoresAux.get(1) + " ";
                                                } else {
                                                    cadenaAux = "AND PA.valor_num NOT BETWEEN " + listaValoresAux.get(0) + " AND " + listaValoresAux.get(1) + " ";
                                                }
                                            }
                                            break;
                                            
                                        case "valores":
                                            if (tipoAtributoAux.equalsIgnoreCase("string")) {
                                                if (indNegacion == 0) {
                                                    cadenaAux = "AND PA.valor_tex='" + listaValoresAux.get(0) + "' ";
                                                } else {
                                                    cadenaAux = "AND PA.valor_tex<>'" + listaValoresAux.get(0) + "' ";
                                                }
                                            } else {
                                                if (indNegacion == 0) {
                                                    cadenaAux = "AND PA.valor_num=" + listaValoresAux.get(0) + " ";
                                                } else {
                                                    cadenaAux = "AND PA.valor_num<>" + listaValoresAux.get(0) + " ";
                                                }
                                            }
                                            break;
                                    }
                                    sql +=
                                        "AND EXISTS (" +
                                            "SELECT PA.id_evento " +
                                            "FROM eventos_puntos_atributos PA " +
                                            "INNER JOIN eventos_atributos EA ON PA.id_evento=EA.id_evento AND PA.id_atributo=EA.id_atributo " +
                                            "WHERE PA.id_evento=EP.id_evento " +
                                            "AND PA.id_punto=EP.id_punto " +
                                            "AND EA.nombre_atributo='" + nombreAtributoAux + "' " +
                                            cadenaAux +
                                        ") ";
                                }
                            }
                        }
                    }
                }
            }
            sql += "ORDER BY EP.id_punto";
            
            return getListaEventosPuntos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que retorna un atributo de evento.
     * @author Feisar Moreno
     * @date 10/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador de la red
     * @param idAtributo Identificador del atributo
     * @return Objeto <code>EventoAtributo</code> con el identificador dado.
     */
    public EventoAtributo getEventoAtributo(boolean conectado, long idEvento, long idAtributo) {
        try {
            String sql =
                "SELECT * FROM eventos_atributos " +
                "WHERE id_evento=" + idEvento + " " +
                "AND id_atributo=" + idAtributo;
            
            return getEventoAtributo(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new EventoAtributo();
        }
    }
    
    /**
     * Método que retorna un atributo de evento por su nombre.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador de la red
     * @param nombreAtributo Nombre del atributo
     * @return Objeto <code>EventoAtributo</code> con el nombre dado.
     */
    public EventoAtributo getTmpEventoAtributo(boolean conectado, long idEvento, String nombreAtributo) {
        try {
            String sql =
                "SELECT * FROM tmp_eventos_atributos " +
                "WHERE id_evento=" + idEvento + " " +
                "AND nombre_atributo='" + nombreAtributo + "'";
            
            return getEventoAtributo(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new EventoAtributo();
        }
    }
    
    /**
     * Método que retorna la cantidad de puntos cargados en un temporal de eventos.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return Objeto <code>ValorGenerico</code> con los valores requeridos.
     */
    public ValorGenerico getCantidadTmpPuntos(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT COUNT(*) AS valor_entero " +
                "FROM tmp_eventos_puntos " +
                "WHERE id_evento=" + idEvento;
            
            return getValorGenerico(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ValorGenerico();
        }
    }
    
    /**
     * Método que retorna la cantidad de atributos cargados en un temporal de eventos.
     * @author Feisar Moreno
     * @date 04/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return Objeto <code>ValorGenerico</code> con los valores requeridos.
     */
    public ValorGenerico getCantidadTmpPuntosAtributos(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT COUNT(*) AS valor_entero " +
                "FROM tmp_eventos_puntos_atributos " +
                "WHERE id_evento=" + idEvento;
            
            return getValorGenerico(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ValorGenerico();
        }
    }
    
    /**
     * Método que retorna un ArrayList de registros de atributos de eventos.
     * @author Feisar Moreno
     * @date 24/05/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return <code>ArrayList</code> con todos los registros de atributos de eventos.
     */
    public ArrayList<EventoAtributo> getListaEventosAtributos(boolean conectado, long idEvento) {
        try {
            String sql =
                "SELECT * " +
                "FROM eventos_atributos " +
                "WHERE id_evento=" + idEvento + " " +
                "ORDER BY id_atributo";
            
            return getListaEventosAtributos(conectado, sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }
    
    /**
     * Método que inserta un registro en la tabla tmp_eventos.
     * @author Feisar Moreno
     * @date 05/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param descEvento Descripción del evento
     * @param idRed Identificador de la red
     * @return Identificador del evento creado.
     */
    public long crearTmpEvento(boolean conectado, String descEvento, long idRed) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_tmp_evento(?,?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setString(1, descEvento);
                cstmt.setLong(2, idRed);
                cstmt.registerOutParameter(3, Types.BIGINT);
                cstmt.execute();
                resultado = cstmt.getLong(3);
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
     * Método que inserta los registros que conforman un evento temporal.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEventoTmp Identificador del evento temporal
     * @param formatoFechaHora Formato de fecha/hora
     * @param listaTmpEventosAtributos ArrayList con los atributos del evento
     * @param listaTmpEventosPuntos ArrayList con los puntos del evento
     * @param listaTmpEventosPuntosAtributos ArrayList con los valores de atributos de cada punto del evento
     * @return <code>true</code> si se crearon todos los registros, de lo contrario <code>false</code>.
     */
    public boolean crearTmpEventoComponentes(boolean conectado, long idEventoTmp, String formatoFechaHora, ArrayList<EventoAtributo> listaTmpEventosAtributos, ArrayList<EventoPunto> listaTmpEventosPuntos, ArrayList<EventoPuntoAtributo> listaTmpEventosPuntosAtributos) {
        String sql = "";
        try {
            if (!conectado) {
                crearConexion();
            }
            
            //Se agregan los atributos del evento
            String sqlBase =
                    "INSERT INTO tmp_eventos_atributos " +
                    "(id_evento, id_atributo, nombre_atributo, tipo_atributo) VALUES ";
            sql = "";
            int contAux = 0;
            for (EventoAtributo eventoAtributoAux : listaTmpEventosAtributos) {
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
                        "(" + idEventoTmp + ", " + eventoAtributoAux.getIdAtributo() + ", '" +
                        eventoAtributoAux.getNombreAtributo() + "', '" + eventoAtributoAux.getTipoAtributo() + "')";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            //Se agregan los puntos del evento
            sqlBase =
                    "INSERT INTO tmp_eventos_puntos " +
                    "(id_evento, id_punto, fecha_punto, latitud, longitud) VALUES ";
            sql = "";
            contAux = 0;
            for (EventoPunto eventoPuntoAux : listaTmpEventosPuntos) {
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
                if (!eventoPuntoAux.getFechaPunto().equals("")) {
                sql +=
                        "(" + idEventoTmp + ", " + eventoPuntoAux.getIdPunto()+ ", STR_TO_DATE('" +
                        eventoPuntoAux.getFechaPunto() + "', '" + formatoFechaHora + "'), ";
                } else {
                    sql += "NOW(), ";
                }
                sql += eventoPuntoAux.getLatitud()+ ", " + eventoPuntoAux.getLongitud()+ ")";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            //Se agregan los atributos de los puntos del evento
            sqlBase =
                    "INSERT INTO tmp_eventos_puntos_atributos " +
                    "(id_evento, id_punto, id_atributo, valor_num, valor_tex) VALUES ";
            sql = "";
            contAux = 0;
            for (EventoPuntoAtributo eventoPuntoAtributoAux : listaTmpEventosPuntosAtributos) {
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
                        "(" + idEventoTmp + ", " + eventoPuntoAtributoAux.getIdPunto()+ ", " +
                        eventoPuntoAtributoAux.getIdAtributo()+ ", " + eventoPuntoAtributoAux.getValorNum()+ ", '" +
                        eventoPuntoAtributoAux.getValorTex().replaceAll("'", " ") + "')";
                
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
     * Método que crea un evento a partir de la información guardada en las tablas temporales.
     * @author Feisar Moreno
     * @date 05/03/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento en las tablas temporales
     * @param nombreAtributoFecha Atributo de fecha
     * @param nombreAtributoHora Atributo de hora
     * @return Identificador del evento creado.
     */
    public long crearEvento(boolean conectado, long idEvento, String nombreAtributoFecha, String nombreAtributoHora) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_crear_evento(?,?,?,?)}";
            long resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
                if (!nombreAtributoFecha.equals("")) {
                    cstmt.setString(2, nombreAtributoFecha);
                } else {
                    cstmt.setNull(2, Types.VARCHAR);
                }
                if (!nombreAtributoHora.equals("")) {
                    cstmt.setString(3, nombreAtributoHora);
                } else {
                    cstmt.setNull(3, Types.VARCHAR);
                }
                cstmt.registerOutParameter(4, Types.BIGINT);
                cstmt.execute();
                resultado = cstmt.getLong(4);
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
     * Método que realiza la proyección de los puntos de un evento.
     * @author Feisar Moreno
     * @date 08/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idRed Identificador de la red
     * @param idEvento Identificador del evento
     * @param distProy Distancia máxima de proyección
     * @param listaEventosPuntos ArrayList con los puntos del evento
     * @return <code>true</code> si se realizó el registro de proyecciones completo, de lo contrario <code>false</code>.
     */
    public boolean realizarProyeccion(boolean conectado, long idRed, long idEvento, double distProy, ArrayList<EventoPunto> listaEventosPuntos) {
        String sql = "";
        try {
            if (!conectado) {
                crearConexion();
            }
            
            long idEventoTmp = this.crearTmpEvento(true, "Temporary projection", idRed);
            
            //Se agregan los puntos del evento
            String sqlBase =
                    "INSERT INTO tmp_eventos_puntos " +
                    "(id_evento, id_punto, fecha_punto, latitud, longitud, id_red, id_linea, num_punto) VALUES ";
            sql = "";
            int contAux = 0;
            for (EventoPunto eventoPuntoAux : listaEventosPuntos) {
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
                        "(" + idEventoTmp + ", " + eventoPuntoAux.getIdPunto() + ", NOW(), " + eventoPuntoAux.getLatitud() + ", " +
                        eventoPuntoAux.getLongitud() + ", " + idRed + ", " + eventoPuntoAux.getIdLinea() + ", " + eventoPuntoAux.getNumPunto() + ")";
                
                contAux++;
            }
            
            if (!sql.equals("")) {
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.execute();
                }
            }
            
            //Se registra la proyección
            String procAlmacenado = "{call pa_realizar_proyeccion_evento(?,?,?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
                cstmt.setLong(2, idEventoTmp);
                cstmt.setDouble(3, distProy);
                cstmt.registerOutParameter(4, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(4);
            }
            
            return (resultado > 0);
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
     * Método que realiza la proyección de los puntos de un evento.
     * @author Feisar Moreno
     * @date 11/03/2016
     * @param idEvento Identifiador del evento
     * @param distProy Distancia máxima de proyección
     * @return <code>true</code> si se pudo realizar la proyección de los puntos, de lo contrario <code>false</code>.
     */
    public boolean realizarProyeccion(long idEvento, double distProy) {
        try {
            crearConexion();
            
            String procAlmacenado = "{call pa_realizar_proyeccion_evento(?,?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
                cstmt.setDouble(2, distProy);
                cstmt.registerOutParameter(3, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(3);
            }
            
            return (resultado > 0);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            cerrarConexion();
        }
    }
    
    /**
     * Método que realiza el cálculo de la función K para redes para un evento dado.
     * @author Feisar Moreno
     * @date 17/07/2016
     * @param idEvento Identifiador del evento
     * @param distanciaIni Distancia inicial de cálculo
     * @param distanciaFin Distancia final de cálculo
     * @param incrementoDist Incremento de la distancia
     * @param cantAleatorios Cantidad de grupos de puntos aleatorios a generar
     * @return <code>true</code> si se pudo realizar el cálculo de la función K para redes , de lo contrario <code>false</code>.
     */
    public boolean realizarCalculoKNet(long idEvento, double distanciaIni, double distanciaFin, double incrementoDist, int cantAleatorios) {
        try {
            crearConexion();
            
            String procAlmacenado = "{call pa_calcular_knet(?,?,?,?,?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
                cstmt.setDouble(2, distanciaIni);
                cstmt.setDouble(3, distanciaFin);
                cstmt.setDouble(4, incrementoDist);
                cstmt.setInt(5, cantAleatorios);
                cstmt.registerOutParameter(6, Types.INTEGER);
                cstmt.execute();
                resultado = cstmt.getInt(6);
            }
            
            return (resultado > 0);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            cerrarConexion();
        }
    }
    
    /**
     * Método que borra un evento.
     * @author Feisar Moreno
     * @date 02/07/2016
     * @param conectado Indicador de conexión a base de datos existente
     * @param idEvento Identificador del evento
     * @return 1 si se pudo borrar el registro, -1 si se presenta un error a nivel de programa, -2 o menos si se presenta un error a nivel de base de datos.
     */
    public int borrarEvento(boolean conectado, long idEvento) {
        try {
            if (!conectado) {
                crearConexion();
            }
            
            String procAlmacenado = "{call pa_borrar_evento(?,?)}";
            int resultado;
            try (CallableStatement cstmt = conn.prepareCall(procAlmacenado)) {
                cstmt.setLong(1, idEvento);
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
