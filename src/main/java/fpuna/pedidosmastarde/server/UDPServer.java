package fpuna.pedidosmastarde.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import fpuna.pedidosmastarde.entidad.Ruta;
import fpuna.pedidosmastarde.servicio.CotizarEnvio;

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
                JSONObject json = (JSONObject) new JSONParser().parse(solicitud);

                String tipo = (String) json.get("tipo");
                String requestId = (String) json.get("requestId");

                JSONObject respuesta = new JSONObject();
                
                if ("COTIZAR_ENVIO".equalsIgnoreCase(tipo)) {
                    int idRuta = ((Number) json.get("id_ruta")).intValue();
                    CotizarEnvio envio = new CotizarEnvio();
                    Ruta ruta = envio.getRuta(idRuta);

                    if (ruta != null) {
                        respuesta.put("ok", true);
                        respuesta.put("requestId", requestId);
                        respuesta.put("zona", ruta.getZona());
                        respuesta.put("costo", ruta.getCosto());
                        respuesta.put("tiempo_estimado", ruta.getTiempoEstimado());
                    } else {
                        respuesta.put("ok", false);
                        respuesta.put("requestId", requestId);
                        respuesta.put("mensaje", "Ruta no encontrada");
                    }
                }

                byte[] datos = respuesta.toJSONString().getBytes(StandardCharsets.UTF_8);
                DatagramPacket paqueteRespuesta = new DatagramPacket(datos, datos.length, paquete.getAddress(), paquete.getPort());
                socket.send(paqueteRespuesta);
            }
        } finally {
            socket.close();
        }
    }
}