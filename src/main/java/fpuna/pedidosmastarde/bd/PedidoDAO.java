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
        String query = "SELECT id, id_cliente, nombre_destinatario, telefono, direccion, email, remitente, cant_paquetes, estado, fecha_programada, id_conductor, id_ruta FROM envio WHERE id = ?;";
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
                    rs.getDate("fecha_programada"),
                    rs.getDouble("monto_total"),
                    rs.getString("estado"),
                    rs.getString("direccion"),
                    rs.getString("email"),
                    rs.getString("nombre_destinatario"),
                    rs.getString("telefono"),
                    rs.getString("remitente"),
                    rs.getInt("cant_paquetes"),
                    rs.getInt("id_conductor"),
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
}
