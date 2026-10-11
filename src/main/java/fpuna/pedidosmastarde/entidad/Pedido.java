package fpuna.pedidosmastarde.entidad;

import java.util.Date;

public class Pedido {
    private Integer id;
    private Integer id_cliente;
    private Date fecha_programada;
    private Double monto_total;
    private String estado;
    private String direccion;
    private String email;
    private String nombre_destinatario;
    private String telefono;
    private String remitente;
    private Integer cant_paquetes;
    private Integer id_conductor;
    private Integer id_ruta;

    public Pedido(Integer id, Integer id_cliente, Date fecha_programada, Double monto_total, String estado, String direccion, String email, String nombre_destinatario, String telefono, String remitente, Integer cant_paquetes, Integer id_conductor, Integer id_ruta) {
        this.id = id;
        this.id_cliente = id_cliente;
        this.fecha_programada = fecha_programada;
        this.monto_total = monto_total;
        this.estado = estado;
        this.direccion = direccion;
        this.email = email;
        this.nombre_destinatario = nombre_destinatario;
        this.telefono = telefono;
        this.remitente = remitente;
        this.cant_paquetes = cant_paquetes;
        this.id_conductor = id_conductor;
        this.id_ruta = id_ruta; 
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCliente() {
        return id_cliente;
    }

    public void setIdCliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Date getFechaProgramada() {
        return fecha_programada;
    }

    public void setFechaProgramada(Date fecha_programada) {
        this.fecha_programada = fecha_programada;
    }

    public Double getMontoTotal() {
        return monto_total;
    }

    public void setMontoTotal(Double monto_total) {
        this.monto_total = monto_total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreDestinatario() {
        return nombre_destinatario;
    }

    public void setNombreDestinatario(String nombre_destinatario) {
        this.nombre_destinatario = nombre_destinatario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public Integer getCantPaquetes() {
        return cant_paquetes;
    }

    public void setCantPaquetes(Integer cant_paquetes) {
        this.cant_paquetes = cant_paquetes;
    }

    public Integer getIdConductor() {
        return id_conductor;
    }

    public void setIdConductor(Integer id_conductor) {
        this.id_conductor = id_conductor;
    }

    public Integer getIdRuta() {
        return id_ruta;
    }

    public void setIdRuta(Integer id_ruta) {
        this.id_ruta = id_ruta;
    }
}