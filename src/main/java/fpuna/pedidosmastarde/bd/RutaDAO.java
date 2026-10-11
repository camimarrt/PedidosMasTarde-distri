package fpuna.pedidosmastarde.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fpuna.pedidosmastarde.entidad.Ruta;

public class RutaDAO {

    // Devuelve una lista con la ruta buscada.
    // Si el id no existe, la lista queda vacia.
    public List<Ruta> seleccionarPorId(int id) throws SQLException {
        String query = "SELECT id, zona, costo, tiempo_estimado FROM ruta WHERE id = ?;";
        List<Ruta> lista = new ArrayList<>();

        Connection conn = null;

        try {
            conn = Bd.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ruta r = new Ruta(
                    rs.getInt("id"),
                    rs.getString("zona"),
                    rs.getLong("costo"),
                    rs.getString("tiempo_estimado")
                );
                lista.add(r);
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
