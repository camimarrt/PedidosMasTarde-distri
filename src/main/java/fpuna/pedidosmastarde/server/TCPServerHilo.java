package fpuna.pedidosmastarde.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fpuna.pedidosmastarde.servicio.SolicitarTransporte;

public class TCPServerHilo extends Thread {
    private final Socket socket;

    public TCPServerHilo(Socket socket) {
        super("TCPServerHilo");
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                Socket cliente = socket;
                BufferedReader entrada = new BufferedReader(new InputStreamReader(
                        cliente.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(
                        cliente.getOutputStream(), StandardCharsets.UTF_8))) {
            // Una solicitud por conexion: lee una linea, responde y cierra
            String solicitud = entrada.readLine();
            if (solicitud != null) {
                if (solicitud.startsWith("SOLICITAR_TRANSPORTE")) {
                    // Formato de texto
                    salida.write(SolicitarTransporte.procesar(solicitud));
                } else {
                    // Formato JSON
                    salida.write(procesar(solicitud));
                }
                salida.write("\n");
                salida.flush();
            }
        } catch (IOException e) {
            System.err.println("Error atendiendo cliente TCP: " + e.getMessage());
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
                case "REGISTRAR_PEDIDO": {
                    int idCliente = enteroRequerido(solicitud, "id_cliente");
                    String nombre = textoRequerido(solicitud, "nombre");
                    String direccion = textoRequerido(solicitud, "direccion");
                    long montoTotal = numeroRequerido(solicitud, "monto_total");

                    int idPedido = new SolicitarTransporte().registrarPedido(idCliente, nombre, direccion, montoTotal);

                    System.out.println("Request REGISTRAR_PEDIDO atendido: " + requestId);

                    if (idPedido == 0) {
                        return error(requestId, "ERROR_BD", "No se pudo guardar el pedido");
                    }

                    JSONObject respuesta = new JSONObject();
                    respuesta.put("ok", true);
                    respuesta.put("requestId", requestId);
                    respuesta.put("idpedido", idPedido);
                    return respuesta.toJSONString();
                }

                default:
                    return error(requestId, "TIPO_NO_SOPORTADO", "Tipo de solicitud no soportado: " + tipo);
            }

        } catch (ParseException e) {
            return error(null, "JSON_INVALIDO", "No se pudo interpretar la solicitud");
        } catch (IllegalArgumentException e) {
            return error(requestId, "CAMPO_REQUERIDO", e.getMessage());
        }
    }

    private int enteroRequerido(JSONObject solicitud, String campo) {
    Object valor = solicitud.get(campo);
    if (!(valor instanceof Number)) {
        throw new IllegalArgumentException("Falta el campo " + campo + " o no es un numero");
    }
    return ((Number) valor).intValue();
    }

    private String textoRequerido(JSONObject solicitud, String campo) {
        Object valor = solicitud.get(campo);
        if (!(valor instanceof String) || ((String) valor).trim().isEmpty()) {
            throw new IllegalArgumentException("Falta el campo " + campo);
        }
        return (String) valor;
    }

    private long numeroRequerido(JSONObject solicitud, String campo) {
        Object valor = solicitud.get(campo);
        if (!(valor instanceof Number)) {
            throw new IllegalArgumentException("Falta el campo " + campo + " o no es un numero");
        }
        return ((Number) valor).longValue();
    }

    private String error(Object requestId, String codigo, String mensaje) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("ok", false);
        respuesta.put("requestId", requestId);
        respuesta.put("codigo", codigo);
        respuesta.put("mensaje", mensaje);
        return respuesta.toJSONString();
    }
}