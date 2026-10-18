package fpuna.pedidosmastarde.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fpuna.pedidosmastarde.entidad.Pedido;

public class PedidoDAO {

    // Devuelve una lista con el pedido buscado
    // Si el id no existe, la lista queda vacia.
    public List<Pedido> seleccionarPorId(int id) throws SQLException {
        String query = "SELECT id, id_cliente, nombre_destinatario, monto_total, estado, direccion, id_ruta FROM envio WHERE id = ?;";
        List<Pedido> lista = new ArrayList<>();

        Connection conn = null;

        try {
            conn = Bd.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Pedido p = new Pedido(
                    rs.getInt("id"),
                    rs.getInt("id_cliente"),
                    rs.getString("nombre_destinatario"),
                    rs.getDouble("monto_total"),
                    rs.getString("estado"),
                    rs.getString("direccion"),
                    rs.getInt("id_ruta")
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
        String sql = "UPDATE envio SET estado = 'SOLICITADO' WHERE id = ?";
        
        try (java.sql.Connection conn = Bd.connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPedido);
            int filasAfectadas = pstmt.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error al solicitar transporte en la BD: " + e.getMessage());
            return false;
        }
    }
    
    // Guarda un pedido nuevo. Devuelve el id generado, o 0 si fallo.
    public int registrarEnvio(int idCliente, String nombre, String direccion, long montoTotal) {
        String sql = "INSERT INTO envio (id_cliente, nombre_destinatario, direccion, estado, id_ruta, monto_total) "
                + "VALUES (?, ?, ?, 'Pendiente', 1, ?) RETURNING id";

        try (Connection conn = Bd.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            ps.setString(2, nombre);
            ps.setString(3, direccion);
            ps.setLong(4, montoTotal);

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
}
