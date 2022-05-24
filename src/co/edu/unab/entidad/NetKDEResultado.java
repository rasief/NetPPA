package co.edu.unab.entidad;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa un registro de la tabla netkde_resultados
 * @author Feisar Moreno
 * @date 12/06/2016
 */
public class NetKDEResultado {
    private long idNetKDE;
    private Evento evento;
    private double anchoBanda;
    private double largoLixel;
    private FuncionNucleo funcionNucleo;
    private int cantPuntos;
    private String fechaResultado;
    private String filtrosResultado;
    
    public NetKDEResultado() {
    }
    
    public NetKDEResultado(ResultSet rs) {
        try {
            this.idNetKDE = rs.getLong("ID_NETKDE");
        } catch (SQLException e) {
            this.idNetKDE = 0L;
        }
        
        this.evento = new Evento(rs);
        
        try {
            this.anchoBanda = rs.getDouble("ANCHO_BANDA");
        } catch (SQLException e) {
            this.anchoBanda = 0;
        }
        
        try {
            this.largoLixel = rs.getDouble("LARGO_LIXEL");
        } catch (SQLException e) {
            this.largoLixel = 0;
        }
        
        this.funcionNucleo = new FuncionNucleo(rs);
        
        try {
            this.cantPuntos = rs.getInt("CANT_PUNTOS");
        } catch (SQLException e) {
            this.cantPuntos = 0;
        }
        
        try {
            this.fechaResultado = rs.getString("FECHA_RESULTADO_T");
        } catch (SQLException e) {
            this.fechaResultado = "";
        }
        
        try {
            this.filtrosResultado = rs.getString("FILTROS_RESULTADO");
        } catch (SQLException e) {
            this.filtrosResultado = "";
        }
    }
    
    public long getIdNetKDE() {
        return idNetKDE;
    }
    
    public void setIdNetKDE(long idNetKDE) {
        this.idNetKDE = idNetKDE;
    }
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    
    public double getAnchoBanda() {
        return anchoBanda;
    }
    
    public void setAnchoBanda(double anchoBanda) {
        this.anchoBanda = anchoBanda;
    }
    
    public double getLargoLixel() {
        return largoLixel;
    }
    
    public void setLargoLixel(double largoLixel) {
        this.largoLixel = largoLixel;
    }
    
    public FuncionNucleo getFuncionNucleo() {
        return funcionNucleo;
    }
    
    public void setFuncionNucleo(FuncionNucleo funcionNucleo) {
        this.funcionNucleo = funcionNucleo;
    }
    
    public int getCantPuntos() {
        return cantPuntos;
    }
    
    public void setCantPuntos(int cantPuntos) {
        this.cantPuntos = cantPuntos;
    }
    
    public String getFechaResultado() {
        return fechaResultado;
    }
    
    public void setFechaResultado(String fechaResultado) {
        this.fechaResultado = fechaResultado;
    }
    
    public String getFiltrosResultado() {
        return filtrosResultado;
    }
    
    public void setFiltrosResultado(String filtrosResultado) {
        this.filtrosResultado = filtrosResultado;
    }
    
    public long getIdEvento() {
        if (this.evento != null) {
            return this.evento.getIdEvento();
        } else {
            return 0L;
        }
    }
    
    public long getIdFuncion() {
        if (this.funcionNucleo != null) {
            return this.funcionNucleo.getIdFuncion();
        } else {
            return 0L;
        }
    }
    
    @Override
    public String toString() {
        String result = "Filters: " + (this.filtrosResultado.equals("") ? "(None)" : this.filtrosResultado) +
                " - Bandwidth: " + this.anchoBanda + " meters" +
                " - Lixel length: " + this.largoLixel + " meters" +
                " - Kernel function: " + this.funcionNucleo.getNombreFuncion();
        
        return result;
    }
}
