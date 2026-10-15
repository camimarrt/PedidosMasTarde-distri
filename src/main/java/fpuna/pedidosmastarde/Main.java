package fpuna.pedidosmastarde;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import fpuna.pedidosmastarde.server.TCPServer;
import fpuna.pedidosmastarde.server.UDPServer;

public class Main {

    // Direccion del servidor de eBye (se puede cambiar con argumentos)
    private static String hostEbye = "127.0.0.1";
    private static int puertoEbye = 6000;

    public static void main(String[] args) {
        // Argumentos opcionales: puertoPropio hostEbye puertoEbye
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 6001;
        if (args.length > 1) {
            hostEbye = args[1];
        }
        if (args.length > 2) {
            puertoEbye = Integer.parseInt(args[2]);
        }

        // Servidores: cada uno en su propio hilo para que no se bloqueen entre si
        Thread hiloUdp = new Thread(new UDPServer(puerto), "Servidor-UDP");
        Thread hiloTcp = new Thread(new TCPServer(puerto), "Servidor-TCP");
        hiloUdp.start();
        hiloTcp.start();

        System.out.println("Servidores PedidosMasTarde iniciados en el puerto " + puerto + " (TCP y UDP).");

        // Menu: PedidosMasTarde como cliente de eBye
        Scanner teclado = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("=== PedidosMasTarde ===");
            System.out.println("1. Consultar stock de un producto (eBye, UDP)");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            String opcion = teclado.nextLine().trim();

            if (opcion.equals("1")) {
                consultarStock(teclado);
            } else if (opcion.equals("0")) {
                System.out.println("Hasta luego.");
                System.exit(0);
            } else {
                System.out.println("Opcion invalida.");
            }
        }
    }

    private static void consultarStock(Scanner teclado) {
        System.out.print("Id del producto: ");
        int idProducto;
        try {
            idProducto = Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("El id debe ser un numero.");
            return;
        }

        String requestId = UUID.randomUUID().toString();

        JSONObject solicitud = new JSONObject();
        solicitud.put("tipo", "GET_STOCK");
        solicitud.put("requestId", requestId);
        solicitud.put("idproducto", idProducto);

        String texto = enviarUDP(hostEbye, puertoEbye, solicitud.toJSONString());
        if (texto == null) {
            System.out.println("eBye no respondio en 3 segundos (servidor apagado o datagrama perdido).");
            return;
        }

        try {
            JSONObject respuesta = (JSONObject) new JSONParser().parse(texto);
            if (!requestId.equals(respuesta.get("requestId"))) {
                System.out.println("El requestId de la respuesta no coincide.");
            } else if (Boolean.TRUE.equals(respuesta.get("ok"))) {
                System.out.println("Stock disponible del producto " + idProducto
                        + ": " + respuesta.get("stock_disponible"));
            } else {
                System.out.println("eBye rechazo la consulta: " + respuesta.get("mensaje"));
            }
        } catch (Exception e) {
            System.out.println("Respuesta invalida de eBye: " + texto);
        }
    }

    // Envia un datagrama y espera la respuesta. Devuelve null si pasan 3 segundos sin respuesta.
    private static String enviarUDP(String host, int puerto, String mensaje) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);

            byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);
            DatagramPacket paquete = new DatagramPacket(datos, datos.length,
                    InetAddress.getByName(host), puerto);
            socket.send(paquete);

            byte[] buffer = new byte[2048];
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteRespuesta);

            return new String(paqueteRespuesta.getData(), 0,
                    paqueteRespuesta.getLength(), StandardCharsets.UTF_8);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (Exception e) {
            System.out.println("Error de red: " + e.getMessage());
            return null;
        }
    }
}