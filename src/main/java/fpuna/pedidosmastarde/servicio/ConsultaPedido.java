package fpuna.pedidosmastarde.servicio;

import java.sql.SQLException;
import java.util.List;
import fpuna.pedidosmastarde.bd.PedidoDAO;
import fpuna.pedidosmastarde.entidad.Pedido;

public class ConsultaPedido {
    private PedidoDAO pedidoDAO;

    public ConsultaPedido() {
        this.pedidoDAO = new PedidoDAO();
    }

    public Pedido getPedido(int id) throws SQLException {
        List<Pedido> lista = pedidoDAO.seleccionarPorId(id);
        
        if (lista.isEmpty()) {
            return null;
        }

        return lista.get(0);
    }
}
