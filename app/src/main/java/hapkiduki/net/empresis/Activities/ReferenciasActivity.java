package hapkiduki.net.empresis.Activities;

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

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.clases.Referencia;

public class ReferenciasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


    RecyclerView recyclerReferencias;
    ArrayList<Referencia> listaRefe;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ReferenciaAdapter miAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referencias);

        listaRefe=new ArrayList<Referencia>();
        recyclerReferencias = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerReferencias.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerReferencias.setHasFixedSize(true);

        request = Volley.newRequestQueue(this);

        cargarWebServiceImagenes();


    }

    private void cargarWebServiceImagenes() {
        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Cargando Referencias...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = "http://192.168.1.54/empresis/WsJSONConsultaReferencia.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Referencia referencias;

                    /**
                    * Traemos la lista local de referencias mediante la librería Sugar
                    * y los eliminamos
                    * */
                    listaRefe = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
                    Referencia.deleteAll(Referencia.class);
                    try {

                        JSONArray json=response.optJSONArray("fp_refer");

                        for (int i=0; i<json.length();i++){
                            referencias=new Referencia();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            referencias.setNomref(jsonArrayChild.optString("NOMBREREF"));
                            referencias.setCodRef(jsonArrayChild.optString("CODIGOREF"));
                            referencias.setPrice("2500");
                            referencias.setQuantity("15");
                            referencias.setState(false);
                            listaRefe.add(referencias);
                            //System.out.println(referencias.getNomref().toString());
                            /**
                             * Guardamos la lista de referencias de manera local con sugar
                             * */
                            referencias.save();
                        }
                        pDialog.dismiss();
                        //ReferenciaAdapter miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);

                        ReferenciaAdapter miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);
                        miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);
                        recyclerReferencias.setAdapter(miAdapter);
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

            listaRefe = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
            miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);
            recyclerReferencias.setAdapter(miAdapter);
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
        ArrayList<Referencia> query = new ArrayList<>();
        //Foreach
        for (Referencia referencia : listaRefe){
            String nomRefe = referencia.getNomref().toLowerCase();
            String codRef = referencia.getCodRef().toLowerCase();

            if (nomRefe.contains(newText) || codRef.contains(newText)){
                query.add(referencia);
            }
        }
        miAdapter.filter(query);
        return true;
    }




}
