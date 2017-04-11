package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

/**
 * Created by Programa-PC on 07/04/2017.
 */

public class PedidoReferencia extends SugarRecord {

    Pedido pedido;
    Referencia referencia;

    public PedidoReferencia() {
    }

    public PedidoReferencia(Pedido pedido, Referencia referencia) {
        this.pedido = pedido;
        this.referencia = referencia;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Referencia getReferencia() {
        return referencia;
    }

    public void setReferencia(Referencia referencia) {
        this.referencia = referencia;
    }
}
