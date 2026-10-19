package fpuna.pedidosmastarde.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fpuna.pedidosmastarde.bd.PedidoDAO;
import fpuna.pedidosmastarde.entidad.Pedido;
import fpuna.pedidosmastarde.entidad.Ruta;
import fpuna.pedidosmastarde.servicio.ConsultaPedido;
import fpuna.pedidosmastarde.servicio.CotizarEnvio;

public class UDPServer implements Runnable {
    private final int puerto;

    public UDPServer(int puerto) {
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(puerto)) {
            System.out.println("PedidosMasTarde escuchando en el puerto UDP " + puerto);
            byte[] buffer = new byte[2048];

            while (true) {
                // Espera un datagrama (se queda bloqueado hasta que llegue uno)
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String solicitud = new String(paquete.getData(), 0,
                        paquete.getLength(), StandardCharsets.UTF_8);

                String respuesta = procesar(solicitud);

                // Responde a la misma direccion y puerto de donde vino el datagrama
                byte[] datos = respuesta.getBytes(StandardCharsets.UTF_8);
                DatagramPacket paqueteRespuesta = new DatagramPacket(datos,
                        datos.length, paquete.getAddress(), paquete.getPort());
                socket.send(paqueteRespuesta);
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor UDP: " + e.getMessage());
        }
    }

    private String procesar(String texto) {
        String requestId = null;

        try {
            Object valor = new JSONParser().parse(texto);
            if (!(valor instanceof JSONObject)) {
                return error(null, "SOLICITUD_INVALIDA", "La solicitud debe ser un objeto JSON");
            }

            JSONObject solicitud = (JSONObject) valor;
            String tipo = textoRequerido(solicitud, "tipo");
            requestId = textoRequerido(solicitud, "requestId");

            switch (tipo.toUpperCase()) {
                case "COTIZAR_ENVIO":{
                    int idRuta = enteroRequerido(solicitud, "id_ruta");

                    CotizarEnvio cotizar = new CotizarEnvio();
                    Ruta ruta = cotizar.getRuta(idRuta);

                    System.out.println("Request atendido: " + requestId);

                    if (ruta == null) {
                        return error(requestId, "RUTA_NO_ENCONTRADA",
                                "No existe la ruta " + idRuta);
                    }
                    return respuestaCotizar(requestId, ruta);
                }

                case "CONSULTAR_PEDIDO": {
                    int idPedido = enteroRequerido(solicitud, "id");

                    ConsultaPedido consulta = new ConsultaPedido();
                    Pedido p = consulta.getPedido(idPedido);

                    System.out.println("Request atendido: " + requestId);

                    if (p == null) {
                        return error(requestId, "PEDIDO_NO_ENCONTRADO",
                                "No existe el pedido " + idPedido);
                    }
                    return respuestaPedido(requestId, p);
                }

                // AGREGAR SERVICIOS ACA
                // case "SERVICIO":
                case "ACTUALIZAR_ESTADO":{
                    int idPedido = enteroRequerido(solicitud, "id");
                    String nuevoEstado = textoRequerido(solicitud, "estado");

                    PedidoDAO dao = new PedidoDAO();
                    if (dao.actualizarEstadoPedido(idPedido, nuevoEstado)) {
                        return respuestaExito(requestId, "Estado actualizado correctamente a " + nuevoEstado);
                    } else {
                        return error(requestId, "ERROR_ACTUALIZACION", "No se pudo actualizar el estado del pedido");
                    }
                }

                case "ELIMINAR_PEDIDO": {
                    int idPedido = enteroRequerido(solicitud, "id");

                    PedidoDAO dao = new PedidoDAO();
                    if (dao.eliminarPedido(idPedido)) {
                        return respuestaExito(requestId, "Pedido cancelado y eliminado exitosamente");
                    } else {
                        return error(requestId, "ERROR_ELIMINAR", "No se pudo eliminar el pedido " + idPedido);
                    }
                }

                default:
                    return error(requestId, "TIPO_NO_SOPORTADO", "Tipo de solicitud no soportado: " + tipo);
            }

        } catch (SQLException e) {
            System.err.println("Error consultando la base de datos: " + e.getMessage());
            return error(requestId, "ERROR_BD", "No se pudo consultar la base de datos");
        } catch (ParseException e) {
            return error(null, "JSON_INVALIDO", "No se pudo interpretar la solicitud");
        } catch (IllegalArgumentException e) {
            return error(requestId, "CAMPO_REQUERIDO", e.getMessage());
        }
    }

    private String textoRequerido(JSONObject solicitud, String campo) {
        Object valor = solicitud.get(campo);
        if (!(valor instanceof String) || ((String) valor).trim().isEmpty()) {
            throw new IllegalArgumentException("Falta el campo " + campo);
        }
        return (String) valor;
    }

    private int enteroRequerido(JSONObject solicitud, String campo) {
        Object valor = solicitud.get(campo);
        if (!(valor instanceof Number)) {
            throw new IllegalArgumentException("Falta el campo " + campo + " o no es un numero");
        }
        return ((Number) valor).intValue();
    }

    private String error(Object requestId, String codigo, String mensaje) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("ok", false);
        respuesta.put("requestId", requestId);
        respuesta.put("codigo", codigo);
        respuesta.put("mensaje", mensaje);
        return respuesta.toJSONString();
    }

    private String respuestaCotizar(String requestId, Ruta ruta) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("ok", true);
        respuesta.put("requestId", requestId);
        respuesta.put("zona", ruta.getZona());
        respuesta.put("costo", ruta.getCosto());
        respuesta.put("tiempo_estimado", ruta.getTiempoEstimado());
        return respuesta.toJSONString();
    }

    private String respuestaPedido(String requestId, Pedido p) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("ok", true);
        respuesta.put("requestId", requestId);
        respuesta.put("id_cliente", p.getIdCliente());
        respuesta.put("nombre_destinatario", p.getNombreDestinatario());
        respuesta.put("monto_total", p.getMontoTotal());
        respuesta.put("estado", p.getEstado());
        respuesta.put("direccion", p.getDireccion());
        respuesta.put("id_ruta", p.getIdRuta());
        return respuesta.toJSONString();
    }
    private String respuestaExito(Object requestId, String mensaje) {
    JSONObject respuesta = new JSONObject();
    respuesta.put("ok", true);
    respuesta.put("requestId", requestId);
    respuesta.put("mensaje", mensaje);
    return respuesta.toJSONString();
}
}