package fpuna.pedidosmastarde.servicio;

import fpuna.pedidosmastarde.bd.PedidoDAO;

public class SolicitarTransporte {

    private PedidoDAO pedidoDAO = new PedidoDAO();

    // Guarda el pedido que llega de eBye. Devuelve el id generado, o 0 si fallo.
    public int registrarPedido(String nombre, String direccion, long montoTotal) {
        return pedidoDAO.registrarEnvio(nombre, direccion, montoTotal);
    }

    // Formato de texto: "SOLICITAR_TRANSPORTE,<id_pedido>" (opcion 2 del menu de eBye)
    public static String procesar(String peticion) {
        try {
            String[] partes = peticion.split(",");
            if (partes.length < 2) {
                return "ERROR: Formato incorrecto. Use SOLICITAR_TRANSPORTE,<id_pedido>";
            }

            int idPedido = Integer.parseInt(partes[1].trim());
            PedidoDAO dao = new PedidoDAO();

            if (dao.solicitarTransporteBd(idPedido)) {
                return "EXITO: Transporte solicitado correctamente para el pedido " + idPedido;
            } else {
                return "ERROR: No se pudo actualizar el pedido. Verifique que el ID exista.";
            }

        } catch (NumberFormatException e) {
            return "ERROR: El ID del pedido debe ser numerico.";
        } catch (Exception e) {
            return "ERROR: Fallo inesperado en el servidor - " + e.getMessage();
        }
    }
}