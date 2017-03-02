package hapkiduki.net.empresis;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import hapkiduki.net.empresis.clases.Referencia;

public class ReferenciaActivity extends AppCompatActivity {

    RecyclerView recyclerReferencias;
    ArrayList<Referencia> listaRefe;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referencia);

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
            String url = "http://192.168.0.105/empresis/WsJSONConsultaReferencia.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Referencia referencias;
                    try {

                        JSONArray json=response.optJSONArray("fp_refer");

                        for (int i=0; i<json.length();i++){
                            referencias=new Referencia();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            referencias.setNomref(jsonArrayChild.optString("NOMBREREF"));
                            referencias.setCodRef(jsonArrayChild.optString("CODIGOREF"));
                            listaRefe.add(referencias);
                            //System.out.println(referencias.getNomref().toString());
                        }
                        pDialog.hide();
                        ReferenciaAdapter miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);
                        recyclerReferencias.setAdapter(miAdapter);

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
