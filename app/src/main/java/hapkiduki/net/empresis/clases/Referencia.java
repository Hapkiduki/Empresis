package hapkiduki.net.empresis.clases;

import com.orm.SugarRecord;

/**
 * Created by Programa-PC on 28/02/2017.
 */

public class Referencia extends SugarRecord{

    private String codRef, nomref;

    public Referencia() {
    }

    public Referencia(String codRef, String nomref) {
        this.codRef = codRef;
        this.nomref = nomref;
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
}
