package hapkiduki.net.empresis.clases;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Programa-PC on 28/03/2017.
 */

public class Pedido implements Serializable {

    private String tercero;
    private List<Referencia> productos;
    private double cost_total;

    public Pedido() {
    }

    public Pedido(String tercero, List<Referencia> productos, double cost_total) {
        this.tercero = tercero;
        this.productos = productos;
        this.cost_total = cost_total;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public List<Referencia> getProductos() {
        return productos;
    }

    public void setProductos(List<Referencia> productos) {
        this.productos = productos;
    }

    public double getCost_total() {
        return cost_total;
    }

    public void setCost_total(double cost_total) {
        this.cost_total = cost_total;
    }
}
