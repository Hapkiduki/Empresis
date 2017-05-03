package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Programa-PC on 28/03/2017.
 */

public class Pedido extends SugarRecord implements Serializable {

    private Tercero tercero;

    private String vendedor;

    private double precioTotal;

    public Pedido() {
    }

    public Pedido(Tercero tercero, double precioTotal, String vendedor) {

        this.tercero = tercero;
        this.precioTotal = precioTotal;
        this.vendedor = vendedor;
    }

    public Tercero getTercero() {
        return tercero;
    }


    public void setTercero(Tercero tercero) {
        this.tercero = tercero;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
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

