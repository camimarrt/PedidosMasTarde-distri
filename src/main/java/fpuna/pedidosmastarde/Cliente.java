
package fpuna.pedidosmastarde;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente {

    public static String enviarTCP(String host, int puerto, String mensaje) {
        try (Socket socket = new Socket(host, puerto);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(mensaje);
            return in.readLine();
            
        } catch (Exception e) {
            return "ERROR_TCP: " + e.getMessage();
        }
    }

    public static String enviarUDP(String host, int puerto, String mensaje) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] bufferEnvio = mensaje.getBytes();
            InetAddress direccion = InetAddress.getByName(host);
            
            DatagramPacket paqueteEnvio = new DatagramPacket(bufferEnvio, bufferEnvio.length, direccion, puerto);
            socket.send(paqueteEnvio);

            byte[] bufferRecepcion = new byte[1024];
            DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
            socket.receive(paqueteRecepcion);
            
            return new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength());
            
        } catch (Exception e) {
            return "ERROR_UDP: " + e.getMessage();
        }
    }
}