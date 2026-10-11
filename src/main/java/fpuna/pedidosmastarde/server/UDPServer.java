package fpuna.pedidosmastarde.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import fpuna.pedidosmastarde.entidad.*;
import fpuna.pedidosmastarde.servicio.ConsultaPedido;
import fpuna.pedidosmastarde.servicio.CotizarEnvio;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class UDPServer {
    private final int puerto;

    public UDPServer(int puerto) {
        this.puerto = puerto;
    }

    public void ejecutar() throws Exception {
        DatagramSocket socket = new DatagramSocket(puerto);
        System.out.println("PedidosMasTarde escuchando en el puerto UDP " + puerto);
        byte[] buffer = new byte[2048];

        try {
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                String solicitud = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8);

//              JSONObject respuesta = new JSONObject();
                String respuesta = procesar(solicitud);

                byte[] datos = respuesta.getBytes(StandardCharsets.UTF_8);
                DatagramPacket paqueteRespuesta = new DatagramPacket(datos, datos.length, paquete.getAddress(), paquete.getPort());
                socket.send(paqueteRespuesta);
            }
        } finally {
            socket.close();
        }
    }

    private String procesar(String texto) throws Exception {
        String requestID = null;
        try {
            Object valor = new JSONParser().parse(texto);
            if (!(valor instanceof JSONObject)) {
                return error(null, "SOLICITUD_INVALIDA", "La solicitud debe ser un objeto JSON");
            }
            JSONObject solicitud = (JSONObject) valor;
            String tipo = textoRequerido(solicitud, "tipo");
            requestID = textoRequerido(solicitud, "requestId");

            JSONObject respuesta = new JSONObject();
            switch (tipo) {
                case "COTIZAR_ENVIO":
                    int idRuta = ((Number) solicitud.get("id_ruta")).intValue();
                    CotizarEnvio envio = new CotizarEnvio();
                    Ruta ruta = envio.getRuta(idRuta);

                    if (ruta != null) {
                        respuesta.put("ok", true);
                        respuesta.put("requestId", requestID);
                        respuesta.put("zona", ruta.getZona());
                        respuesta.put("costo", ruta.getCosto());
                        respuesta.put("tiempo_estimado", ruta.getTiempoEstimado());
                    } else {
                        respuesta.put("ok", false);
                        respuesta.put("requestId", requestID);
                        respuesta.put("mensaje", "Ruta no encontrada");
                    }          
                    return respuesta.toJSONString();
                case "CONSULTAR_PEDIDO":
                    int idPedido = ((Number) solicitud.get("id")).intValue();
                    ConsultaPedido consulta = new ConsultaPedido();
                    Pedido p = consulta.getPedido(idPedido);

                    if (p != null) {
                        respuesta.put("id",requestID);
                        respuesta.put("id_cliente",p.getIdCliente());
                        respuesta.put("fecha_programada",p.getFechaProgramada());
                        respuesta.put("monto_total",p.getMontoTotal());
                        respuesta.put("estado",p.getEstado());
                        respuesta.put("direccion", p.getDireccion());
                        respuesta.put("email",p.getEmail());
                        respuesta.put("nombre_destinatario", p.getNombreDestinatario());
                        respuesta.put("telefono",p.getTelefono());
                        respuesta.put("remitente",p.getRemitente());
                        respuesta.put("cant_paquetes", p.getCantPaquetes());
                        respuesta.put("id_conductor", p.getIdConductor());
                        respuesta.put("id_ruta", p.getIdRuta());
                    }


                default:
                    return error(requestID, "TIPO_NO_SOPORTADO", "Solo se admite GET_STOCK");
            }

        } catch (SQLException e) {
            System.err.println("Error consultando stock: " + e.getMessage());
            return error(requestID, "ERROR_BD", "No se pudo consultar el stock");
        } catch (ParseException e) {
            return error(null, "JSON_INVALIDO", "No se pudo interpretar la solicitud");
        } catch (IllegalArgumentException e) {
            return error(requestID, "CAMPO_REQUERIDO", e.getMessage());
        }
    }

    private String textoRequerido(JSONObject solicitud, String campo) {
        Object valor = solicitud.get(campo);
        if (!(valor instanceof String) || ((String) valor).trim().isEmpty()) {
            throw new IllegalArgumentException("Falta el campo " + campo);
        }
        return (String) valor;
    }

/* 
    private int enteroRequerido(JSONObject solicitud, String campo) {
        Object valor = solicitud.get(campo);
        if (!(valor instanceof Number)) {
            throw new IllegalArgumentException("Falta el campo " + campo + " o no es un numero");
        }
        return ((Number) valor).intValue();
    }
*/
    private String error(Object requestId, String codigo, String mensaje) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("ok", false);
        respuesta.put("requestId", requestId);
        respuesta.put("codigo", codigo);
        respuesta.put("mensaje", mensaje);
        return respuesta.toJSONString();
    }

}