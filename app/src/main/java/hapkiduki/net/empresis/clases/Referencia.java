package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Programa-PC on 28/02/2017.
 */

public class Referencia extends SugarRecord implements Serializable{

    private String codRef, nomref;
    private String price;
    private String quantity;
    private String cantPed;


    public Referencia() {
    }

    public Referencia(String codRef, String nomref, String price, String quantity, String cantPed) {
        this.codRef = codRef;
        this.nomref = nomref;
        this.price = price;
        this.quantity = quantity;
        this.cantPed = cantPed;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice(){return  price;}

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCodRef() {
        return codRef;
    }

    public void setCodRef(String codRef) {
        this.codRef = codRef;
    }

    public String getNomref() {
        return nomref;
    }

    public void setNomref(String nomref) {
        this.nomref = nomref;
    }


    /**
     * Relaciones de datos
     */
    public List<Pedido> getPedidos() {
        List<Pedido> result = new ArrayList<>();
        for (PedidoReferencia pedidoReferencia : PedidoReferencia.find(PedidoReferencia.class, "referencia = ?", getId().toString())){
            result.add(pedidoReferencia.getPedido());
        }
        return result;
    }

    public String getCantPed() {
        return cantPed;
    }

    public void setCantPed(String cantPed) {
        this.cantPed = cantPed;
    }
}
