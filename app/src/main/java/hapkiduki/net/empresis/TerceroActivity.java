package hapkiduki.net.empresis;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hapkiduki.net.empresis.adapters.TerceroAdapter;
import hapkiduki.net.empresis.clases.Tercero;

public class TerceroActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    RecyclerView recyclerTerceros;
    ArrayList<Tercero> listaTerce;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    TerceroAdapter miAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercero);

        listaTerce=new ArrayList<Tercero>();
        recyclerTerceros = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerTerceros.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerTerceros.setHasFixedSize(true);

        request = Volley.newRequestQueue(this);

        cargarWebServiceImagenes();

    }

   private void cargarWebServiceImagenes() {
        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Cargando Terceros...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = "http://192.168.0.103:80/empresis/WsJSONConsultaTercero.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Tercero terceros;
                    /*
                    * Traemos la lista local de terceros mediante la libreria SUGAR
                    */
                    listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
                    /*
                    * Eliminamos todos los terceros de la lista local con el metodo de Sugar
                     */
                    Tercero.deleteAll(Tercero.class);
                    try {

                        JSONArray json=response.optJSONArray("fp_terce");

                        for (int i=0; i<json.length();i++){
                            terceros = new Tercero();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            terceros.setDni(jsonArrayChild.optString("CODIGOCLI"));
                            terceros.setDireccion(jsonArrayChild.optString("DIRECCION"));
                            terceros.setTercero(jsonArrayChild.optString("NOMBRECLI"));
                            terceros.setTelefono(jsonArrayChild.optString("TELEFONO"));
                            listaTerce.add(terceros);
                            //System.out.println(referencias.getNomref().toString());

                            /**
                             * Guardamos la lista de terceros en la tabla local con Sugar
                             */
                            terceros.save();
                        }
                        pDialog.hide();
                        TerceroAdapter miAdapter=new TerceroAdapter(getApplicationContext(),listaTerce);
                        miAdapter=new TerceroAdapter(getApplicationContext(),listaTerce);
                        recyclerTerceros.setAdapter(miAdapter);
                        //////////////
                        miAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        System.out.println();
                        e.printStackTrace();
                        pDialog.hide();
                        System.out.println(response);
                        Toast.makeText(getApplication(), "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplication(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                    System.out.println();
                    pDialog.hide();
                    Log.d("ERROR: ", error.toString());
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.add(jsonObjectRequest);
        } else {
            Toast.makeText(getApplication(), "No se pudo sincronizar, Verifique que " +
                    "cuenta con acceso a Internet", Toast.LENGTH_LONG).show();
            pDialog.hide();
            /*
             * Traemos la lista de terceros de manera local con sugar y lo
             * Bindamos al Reciclerview
             */

            listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
            miAdapter=new TerceroAdapter(getApplicationContext(),listaTerce);
            recyclerTerceros.setAdapter(miAdapter);
        }
    }

    //Agregamos el Menu o scope
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem itemBuscar = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemBuscar);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Tercero> query = new ArrayList<>();
        //Esto es un foreach en java
        for (Tercero tercero: listaTerce){
            String nomTercero = tercero.getTercero().toLowerCase();
            String dni = tercero.getDni().toLowerCase();
            if (nomTercero.contains(newText) || dni.contains(newText)){
                query.add(tercero);
            }
        }


        miAdapter.setFilter(query);
        return true;
    }
}
