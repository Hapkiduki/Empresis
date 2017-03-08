package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

/**
 * Created by Programa-PC on 28/02/2017.
 */

public class Referencia extends SugarRecord{

    private String codRef, nomref;
    //private double price;
    //private float quantity;

    public Referencia() {
    }

    public Referencia(String codRef, String nomref) {
        this.codRef = codRef;
        this.nomref = nomref;
      //  this.price = price;
       // this.quantity = quantity;
    }
/*
    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public double getPrice(){return  price;}

    public void setPrice(double price) {
        this.price = price;
    }
*/
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
}
