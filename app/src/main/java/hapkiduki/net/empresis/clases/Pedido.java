package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Programa-PC on 28/03/2017.
 */

public class Pedido extends SugarRecord implements Serializable {

    private String tercero;
    private double cost_total;
    private List<Referencia> productos;

    public Pedido() {
    }

    public Pedido(String tercero, double cost_total, List<Referencia> producto) {
        this.tercero = tercero;
        this.cost_total = cost_total;
        this.productos = producto;
    }

    public List<Referencia> getProducto() {

        return productos;
    }

    public void setProducto(List<Referencia> producto) {
        this.productos = producto;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public double getCost_total() {
        return cost_total;
    }

    public void setCost_total(double cost_total) {
        this.cost_total = cost_total;
    }

}
