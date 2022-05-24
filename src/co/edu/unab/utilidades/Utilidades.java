package co.edu.unab.utilidades;

import co.edu.unab.entidad.EventoAtributo;
import co.edu.unab.entidad.UnidadMedida;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JTable;

/**
 * Clase con métodos varios estático
 * @author Feisar Moreno
 * @date 27/02/2016
 */
public abstract class Utilidades {
    public static final double GEOIDE = 6372795.477598;
    
    /**
     * Método que retorna los caracteres a la izquierda de una cadena
     * @param cadena Cadena de la que se quiere extraer la parte izquierda
     * @param longitud Número de caracteres a extraer
     * @return <code>String</code> con los caracteres a la izquierda de la cadena original
     */
    public static String izquierda(String cadena, int longitud) {
        String retorno = cadena;
        if (cadena.length() > longitud) {
            retorno = cadena.substring(0, longitud);
        }
        
        return retorno;
    }
    
    /**
     * Método que retorna los caracteres a la derecha de una cadena
     * @param cadena Cadena de la que se quiere extraer la parte derecha
     * @param longitud Número de caracteres a extraer
     * @return <code>String</code> con los caracteres a la derecha de la cadena original
     */
    public static String derecha(String cadena, int longitud) {
        String retorno = cadena;
        int longCad = cadena.length();
        if (longCad > longitud) {
            retorno = cadena.substring(longCad - longitud);
        }
        
        return retorno;
    }
    
    /**
     * Método que calcula la distancia en metros entre dos puntos
     * @param latitudIni Latitud del primer punto
     * @param longitudIni Longitud del primer punto
     * @param latitudFin Latitud del segundo punto
     * @param longitudFin Longitud del segundo punto
     * @param unidadMedida Unidad de medida del sistema de puntos
     * @param numDecimales Cantidad máxima de números decimales a tener en cuenta
     * @return Distancia en metros entre los dos puntos.
     */
    public static double calcularDistanciaPuntos(double latitudIni, double longitudIni, double latitudFin, double longitudFin, UnidadMedida unidadMedida, int numDecimales) {
        double distancia;
        if (unidadMedida.getIndGrados() == 1) {
            distancia = GEOIDE * Math.acos(Math.sin(Math.toRadians(latitudIni)) * Math.sin(Math.toRadians(latitudFin)) + Math.cos(Math.toRadians(latitudIni)) * Math.cos(Math.toRadians(latitudFin)) * Math.cos(Math.toRadians(longitudIni) - Math.toRadians(longitudFin)));
        } else {
            distancia = Math.sqrt(Math.pow(latitudIni - latitudFin, 2) + Math.pow(longitudIni - longitudFin, 2)) * unidadMedida.getFactorMetros();
        }
        
        if (numDecimales >= 0) {
            distancia = Math.round(distancia * Math.pow(10, numDecimales)) / Math.pow(10, numDecimales);
        }
        
        return distancia;
    }
    
    /**
     * Método que calcula la distancia en metros entre dos puntos
     * @param latitudIni Latitud del primer punto
     * @param longitudIni Longitud del primer punto
     * @param latitudFin Latitud del segundo punto
     * @param longitudFin Longitud del segundo punto
     * @param unidadMedida Unidad de medida del sistema de puntos
     * @return Distancia en metros entre los dos puntos.
     */
    public static double calcularDistanciaPuntos(double latitudIni, double longitudIni, double latitudFin, double longitudFin, UnidadMedida unidadMedida) {
        return calcularDistanciaPuntos(latitudIni, longitudIni, latitudFin, longitudFin, unidadMedida, -1);
    }
    
    /**
     * Función que transforma a coordenadas planas las coordenadas geográficas recibidas con respecto a unas coordenadas base que se tomarán como el punto (0, 0).
     * @param latBase Latitud del punto base
     * @param lonBase Longitud del punto base
     * @param latPunto Latitud del punto a transformar
     * @param lonPunto Longitud del punto a transformar
     * @param unidadMedida Unidad de medidad de la red
     * @return Array con las coordenadas planas ([0] latitud - [1] Longitud) correspondientes a las coordenadas geográficas recibidas
     */
    public static double[] coordenadasGeograficasAPlanas(double latBase, double lonBase, double latPunto, double lonPunto, UnidadMedida unidadMedida) {
        double[] coordenadas = new double[2];
        
        coordenadas[0] = calcularDistanciaPuntos(latPunto, lonPunto, latBase, lonPunto, unidadMedida);
        if (latBase < latPunto) {
            coordenadas[0] = coordenadas[0] * -1;
        }
        
        coordenadas[1] = calcularDistanciaPuntos(latPunto, lonPunto, latPunto, lonBase, unidadMedida);
        if (lonBase < lonPunto) {
            coordenadas[1] = coordenadas[1] * -1;
        }
        
        return coordenadas;
    }
    
    /**
     * Método que valida si los valores aplicados a una tabla de filtros son correctos.
     * @date 25/05/2016
     * @param tablaAtributos Tabla que contiene los atributos
     * @return Cadena de texto nula (null) si no se encontraron errores, de lo contrario se entrega una cadena de texto con el error hallado.
     */
    public static String validarFiltros(JTable tablaAtributos) {
        for (int i = 0; i < tablaAtributos.getRowCount(); i++) {
            String filtroAux = tablaAtributos.getValueAt(i, 2).toString();
            if (!filtroAux.equals("")) {
                String nombreAtributo = tablaAtributos.getValueAt(i, 0).toString();
                String tipoAtributo = tablaAtributos.getValueAt(i, 1).toString();
                
                //Se quita el símbolo de negación si existe
                if (filtroAux.substring(0, 1).equals("!")) {
                    filtroAux = filtroAux.substring(1);
                }
                
                //Se valida si se trata de varios datos separados por coma o un rango de valores
                int posComa = filtroAux.indexOf(",");
                int posNumeral = filtroAux.indexOf("#");
                
                if (posComa >= 0 && posNumeral >= 0) {
                    return "Unable to combine individual values and ranges of values in the filter [" + nombreAtributo + "].";
                }
                
                List<String> listaValoresAux = new ArrayList<>();
                if (posComa >= 0) {
                    //Lista de valores
                    listaValoresAux = Arrays.asList(filtroAux.split(","));
                } else if (posNumeral >= 0) {
                    //Rango de valores
                    listaValoresAux = Arrays.asList(filtroAux.split("#"));
                } else {
                    //Valor individual
                    listaValoresAux.add(filtroAux);
                }
                
                //Se valida que cada uno de los componentes de la lista correspondan al tipo de dato del filtro
                for (String valorAux : listaValoresAux) {
                    valorAux = valorAux.trim();
                    
                    //Se validan los campos numéricos
                    try {
                        switch (tipoAtributo.toLowerCase()) {
                            case "int":
                                Integer.parseInt(valorAux);
                                break;
                            case "float":
                                Float.parseFloat(valorAux);
                                break;
                            case "double":
                                Double.parseDouble(valorAux);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        return "Filter [" + nombreAtributo + "] contains values not corresponding to type [" + tipoAtributo + "].";
                    }
                    
                    //Se validan los campos de fecha y hora
                    if (tipoAtributo.equals("dd/mm/yyyy") || tipoAtributo.equals("hh:mm")) {
                        boolean indFechaIncorrecta = false;
                        Date fechaAux = null;
                        String formatoAux = "dd/MM/yyyy";
                        if (tipoAtributo.equals("hh:mm")) {
                            formatoAux = "HH:mm";
                        }
                        try {
                            //Se validan los campos de fecha
                            fechaAux = new SimpleDateFormat(formatoAux).parse(valorAux);
                        } catch (ParseException ex) {
                            indFechaIncorrecta = true;
                        }
                        
                        if (fechaAux == null) {
                            indFechaIncorrecta = true;
                        }
                        
                        if (indFechaIncorrecta) {
                            return "Filter [" + nombreAtributo + "] contains values not corresponding to type [" + tipoAtributo + "].";
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Método que retorna una colección de atributos separados por valores individuales, listados y rangos a partir de una tabla.
     * @date 24/05/2016
     * @param tablaAtributos Tabla que contiene los atributos
     * @return Coleeción con los atributos clasificados por valores individuales, listados y rangos.
     */
    public static LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> obtenerFiltros(JTable tablaAtributos) {
        LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaAtributos = new LinkedHashMap<>();
        
        //Se recorre la tabla
        for (int i = 0; i < tablaAtributos.getRowCount(); i++) {
            String nombreAtributo = tablaAtributos.getValueAt(i, 0).toString();
            String tipoAtributo = tablaAtributos.getValueAt(i, 1).toString();
            String valorAtributo = tablaAtributos.getValueAt(i, 2).toString();
            
            if (!valorAtributo.equals("")) {
                int indNegacion = 0;
                if (valorAtributo.substring(0, 1).equals("!")) {
                    indNegacion = 1;
                    valorAtributo = valorAtributo.substring(1);
                }
                EventoAtributo eventoAtributoAux = new EventoAtributo(0, i, nombreAtributo, tipoAtributo, indNegacion);
                
                int posComa = valorAtributo.indexOf(",");
                int posNumeral = valorAtributo.indexOf("#");

                if (posComa >= 0) {
                    //Listado de valores
                    LinkedHashMap<EventoAtributo, List<String>> mapaValoresAux;
                    if (mapaAtributos.containsKey("listas")) {
                        mapaValoresAux = mapaAtributos.get("listas");
                    } else {
                        mapaValoresAux = new LinkedHashMap<>();
                    }

                    //Se obtiene el listado de valores
                    List<String> listaValoresAux = Arrays.asList(valorAtributo.split(","));
                    mapaValoresAux.put(eventoAtributoAux, listaValoresAux);
                    mapaAtributos.put("listas", mapaValoresAux);
                } else if (posNumeral >= 0) {
                    //Rango de valores
                    LinkedHashMap<EventoAtributo, List<String>> mapaValoresAux;
                    if (mapaAtributos.containsKey("rangos")) {
                        mapaValoresAux = mapaAtributos.get("rangos");
                    } else {
                        mapaValoresAux = new LinkedHashMap<>();
                    }

                    //Se obtienen los rangos de valores
                    List<String> listaValoresAux = Arrays.asList(valorAtributo.split("#"));
                    mapaValoresAux.put(eventoAtributoAux, listaValoresAux);
                    mapaAtributos.put("rangos", mapaValoresAux);
                } else {
                    //Valores individuales
                    LinkedHashMap<EventoAtributo, List<String>> mapaValoresAux;
                    if (mapaAtributos.containsKey("valores")) {
                        mapaValoresAux = mapaAtributos.get("valores");
                    } else {
                        mapaValoresAux = new LinkedHashMap<>();
                    }

                    //Se agrega el valor a la lista
                    List<String> listaValoresAux = new ArrayList<>();
                    listaValoresAux.add(valorAtributo);
                    mapaValoresAux.put(eventoAtributoAux, listaValoresAux);
                    mapaAtributos.put("valores", mapaValoresAux);
                }
            }
        }
        
        return mapaAtributos;
    }
    
    public static String mapaFiltrosACadena(LinkedHashMap<String, LinkedHashMap<EventoAtributo, List<String>>> mapaFiltros) {
        String cadenaFiltros = "";
        
        if (mapaFiltros != null) {
            for (String tipoValorAux : mapaFiltros.keySet()) {
                LinkedHashMap<EventoAtributo, List<String>> mapaValoresAux = mapaFiltros.get(tipoValorAux);
                if (mapaValoresAux != null) {
                    for (EventoAtributo eventoAtributoAux : mapaValoresAux.keySet()) {
                        List<String> listaValoresAux = mapaValoresAux.get(eventoAtributoAux);
                        String nombreAtributoAux = eventoAtributoAux.getNombreAtributo();
                        int indNegacion = eventoAtributoAux.getIndNegacion();
                        
                        if (listaValoresAux != null && listaValoresAux.size() > 0) {
                            if (!cadenaFiltros.equals("")) {
                                cadenaFiltros += "; ";
                            }
                            if (indNegacion == 0) {
                                cadenaFiltros += nombreAtributoAux + "=";
                            } else {
                                cadenaFiltros += nombreAtributoAux + "!=";
                            }
                            
                            switch (tipoValorAux) {
                                case "listas":
                                    cadenaFiltros += "(";
                                    for (int i = 0; i < listaValoresAux.size(); i++) {
                                        cadenaFiltros += listaValoresAux.get(i);
                                        if (i < listaValoresAux.size() - 1) {
                                            cadenaFiltros += ",";
                                        }
                                    }
                                    cadenaFiltros += ")";
                                    break;
                                    
                                case "rangos":
                                    cadenaFiltros += "[" + listaValoresAux.get(0) + "#" + listaValoresAux.get(1) + "]";
                                    break;
                                    
                                case "valores":
                                    cadenaFiltros += listaValoresAux.get(0);
                                    break;
                            }
                        }
                    }
                }
            }
        }
        
        return cadenaFiltros;
    }
    
    /**
     * Esta función halla la proyección de una coordenada de latitud en coordenadas geográficas con respecto a un punto base que en la proyección se toma como (0, 0).
     * @param latitud Latitud a convertir
     * @param latitudPunto Latitud del punto base
     * @param longitudPunto Longitud del punto base
     * @param unidadMedida Unidad de medida
     * @param numDecimales Número de decimales a tomar en cuenta
     * @return Latitud transformada a coordenadas planas
     */
    public static double transformarLatitudPlana(double latitud, double latitudPunto, double longitudPunto, UnidadMedida unidadMedida, int numDecimales) {
        double latitudConv = calcularDistanciaPuntos(latitudPunto, longitudPunto, latitud, longitudPunto, unidadMedida, numDecimales);
        if (latitud < latitudPunto) {
            latitudConv *= -1;
        }
        
        return latitudConv;
    }
    
    /**
     * Esta función halla la proyección de una coordenada de longitud en coordenadas geográficas con respecto a un punto base que en la proyección se toma como (0, 0).
     * @param longitud Longitud a convertir
     * @param latitudPunto Latitud del punto base
     * @param longitudPunto Longitud del punto base
     * @param unidadMedida Unidad de medida
     * @param numDecimales Número de decimales a tomar en cuenta
     * @return Longitud transformada a coordenadas planas
     */
    public static double transformarLongitudPlana(double longitud, double latitudPunto, double longitudPunto, UnidadMedida unidadMedida, int numDecimales) {
        double longitudConv = calcularDistanciaPuntos(latitudPunto, longitudPunto, latitudPunto, longitud, unidadMedida, numDecimales);
        if (longitud < longitudPunto) {
            longitudConv *= -1;
        }
        
        return longitudConv;
    }
}
