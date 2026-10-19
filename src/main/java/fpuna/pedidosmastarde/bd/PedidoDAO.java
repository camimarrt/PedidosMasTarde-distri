package fpuna.pedidosmastarde.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fpuna.pedidosmastarde.entidad.Pedido;

public class PedidoDAO {

    // Devuelve una lista con el pedido buscado, si el id no existe, la lista queda vacia.
    // arma el query y busca en la BD por ID
    public List<Pedido> seleccionarPorId(int id) throws SQLException {
        String query = "SELECT id, id_cliente, nombre_destinatario, monto_total, estado, direccion, id_ruta, id_producto, cantidad FROM envio WHERE id = ?;";
        List<Pedido> lista = new ArrayList<>();

        Connection conn = null;

        try {
            conn = Bd.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Pedido p = new Pedido(
                    rs.getInt("id"),
                    rs.getInt("id_cliente"),
                    rs.getString("nombre_destinatario"),
                    rs.getDouble("monto_total"),
                    rs.getString("estado"),
                    rs.getString("direccion"),
                    rs.getInt("id_ruta"),
                    rs.getInt("id_producto"),
                    rs.getInt("cantidad")
                );
                lista.add(p);
            }
            return lista;

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("No se pudo cerrar la conexion a BD: " + e.getMessage());
                }
            }
        }
    } 
////////////////////////
    public boolean solicitarTransporteBd(int idPedido) {
        String query = "UPDATE envio SET estado = 'SOLICITADO' WHERE id = ?";
        
        try {
            java.sql.Connection conn = Bd.connect();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idPedido);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error al solicitar transporte en la BD: " + e.getMessage());
            return false;
        }
    }
    
    // guarda un pedido nuevo, devuelve el id generado o 0 si falla
    public int registrarEnvio(int idCliente, String nombre, String direccion, long montoTotal, int idProducto, int cantidad) {
        String sql = "INSERT INTO envio (id_cliente, nombre_destinatario, direccion, estado, id_ruta, id_producto, cantidad, monto_total) "
                + "VALUES (?, ?, ?, 'Solicitado', 1, ?, ?, ?) RETURNING id";

        try {
            Connection conn = Bd.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            // prepara el query con los datos que recibió
            ps.setInt(1, idCliente);
            ps.setString(2, nombre);
            ps.setString(3, direccion);
            ps.setInt(4, idProducto);
            ps.setInt(5, cantidad);
            ps.setLong(6, montoTotal);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
        // Metodo para eliminar el pedido si el cliente lo cancela
        public int[] eliminarPedido(int idPedido) {
            String sql = "DELETE FROM envio WHERE id = ? RETURNING id_producto, cantidad";

            try {
                Connection conn = Bd.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, idPedido);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new int[] { rs.getInt("id_producto"), rs.getInt("cantidad") };
                    }
                }
                return null;

            } catch (SQLException e) {
                System.err.println("Error al eliminar el pedido: " + e.getMessage());
                return null;
            }
        }

    // Método para actualizar el estado del pedido (ej. de Pendiente a Solicitado)
    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) {
        // Asumiendo que tienes una columna "estado" en tu tabla "pedido"
        String sql = "UPDATE envio SET estado = ? WHERE id = ?";
        
        try (Connection conn = Bd.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idPedido);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado: " + e.getMessage());
            return false;
        }
    }
}
