package fpuna.pedidosmastarde.servicio;

import fpuna.pedidosmastarde.bd.PedidoDAO;

public class SolicitarTransporte {
    private PedidoDAO pedidoDAO = new PedidoDAO();

    public boolean procesarSolicitud(Integer idProducto, Integer cantidad, String destino, String cliente) {
        if (idProducto == null || cantidad == null || cantidad <= 0) {
            return false;
        }

        // Llama al método registrarEnvio que agregaste en PedidoDAO (Paso 4)
        return pedidoDAO.registrarEnvio(idProducto, cantidad, destino, cliente);
    }
    public static String procesar(String peticion) {
        try {
            // Se espera el formato: "SOLICITAR_TRANSPORTE,123"
            String[] partes = peticion.split(",");
            if (partes.length < 2) {
                return "ERROR: Formato incorrecto. Use SOLICITAR_TRANSPORTE,<id_pedido>";
            }
            
            int idPedido = Integer.parseInt(partes[1].trim());
            PedidoDAO dao = new PedidoDAO();
            
            // Modifica la base de datos
            boolean exito = dao.solicitarTransporteBd(idPedido);
            
            if (exito) {
                return "EXITO: Transporte solicitado correctamente para el pedido " + idPedido;
            } else {
                return "ERROR: No se pudo actualizar el pedido. Verifique que el ID exista.";
            }
            
        } catch (NumberFormatException e) {
            return "ERROR: El ID del pedido debe ser numérico.";
        } catch (Exception e) {
            return "ERROR: Fallo inesperado en el servidor - " + e.getMessage();
        }
    }
}