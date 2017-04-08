package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Programa-PC on 28/03/2017.
 */

public class Pedido extends SugarRecord implements Serializable {

    private Tercero tercero;

    private double precioTotal;

    public Pedido() {
    }

    public Pedido(Tercero tercero, double precioTotal) {

        this.tercero = tercero;
        this.precioTotal = precioTotal;
    }



    public Tercero getTercero() {
        return tercero;
    }


    public void setTercero(Tercero tercero) {
        this.tercero = tercero;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public List<Referencia> getProducts() {
        List<Referencia> result = new ArrayList<>();
        for (PedidoReferencia pedidoReferencia : PedidoReferencia.find(PedidoReferencia.class, "pedido = ?", getId().toString())){
            result.add(pedidoReferencia.getReferencia());
        }
        return result;
    }
}

