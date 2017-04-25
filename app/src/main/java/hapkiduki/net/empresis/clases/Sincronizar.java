package hapkiduki.net.empresis.clases;

import android.widget.Toast;

import java.util.ArrayList;

import hapkiduki.net.empresis.Activities.PruebaActivity;

/**
 * Created by Programa-PC on 25/04/2017.
 */

public class Sincronizar {

    public static void iniciarSincronizacion(PruebaActivity pruebaActivity, ArrayList seletedItems) {
        //sincronizarPedidos();
        int seleccion = seletedItems.size();
        if (seleccion > 0){
            if (seleccion > 1){
                Toast.makeText(pruebaActivity, "elementos: "+seletedItems.size(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(pruebaActivity, "elemento: "+seletedItems.get(0), Toast.LENGTH_SHORT).show();
            }
        }



    }
}
