package hapkiduki.net.empresis;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.adapters.TerceroAdapter;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

public class TerceroActivity extends AppCompatActivity {

    RecyclerView recyclerTerceros;
    ArrayList<Tercero> listaTerce;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

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
            String url = "http://192.168.0.105/empresis/WsJSONConsultaTercero.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Tercero terceros;
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
                        }
                        pDialog.hide();
                        TerceroAdapter miAdapter=new TerceroAdapter(getApplicationContext(),listaTerce);
                        recyclerTerceros.setAdapter(miAdapter);

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
            ///////////
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            ////////////////
            request.add(jsonObjectRequest);
        } else {
            Toast.makeText(getApplication(), "No se puede conectar, Verifique que " +
                    "cuenta con acceso a Internet", Toast.LENGTH_LONG).show();
            pDialog.hide();
        }
    }

}
