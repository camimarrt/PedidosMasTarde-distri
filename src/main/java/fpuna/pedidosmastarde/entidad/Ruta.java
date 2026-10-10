package fpuna.pedidosmastarde.entidad;

public class Ruta {
    private Integer id;
    private String zona;
    private Long costo;
    private String tiempoEstimado;

    public Ruta(Integer id, String zona, Long costo, String tiempoEstimado) {
        this.id = id;
        this.zona = zona;
        this.costo = costo;
        this.tiempoEstimado = tiempoEstimado;
    }

    public Integer getId() { 
        return id; 
    }
    public String getZona() { 
        return zona; 
    }
    public Long getCosto() { 
        return costo; 
    }
    public String getTiempoEstimado() { 
        return tiempoEstimado; 
    }
}