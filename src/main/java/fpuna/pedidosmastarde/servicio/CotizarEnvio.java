package fpuna.pedidosmastarde.servicio;

import java.sql.SQLException;
import java.util.List;

import fpuna.pedidosmastarde.bd.RutaDAO;
import fpuna.pedidosmastarde.entidad.Ruta;

public class CotizarEnvio {

    private RutaDAO rutaDAO;

    public CotizarEnvio() {
        this.rutaDAO = new RutaDAO();
    }

    // Devuelve la ruta con ese id, o null si no existe.
    public Ruta getRuta(int id) throws SQLException {
        List<Ruta> lista = rutaDAO.seleccionarPorId(id);

        if (lista.isEmpty()) {
            return null;
        }
        
        return lista.get(0);
    }
}