package hapkiduki.net.empresis.fragments;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


public class ReferenciaFragment extends Fragment implements SearchView.OnQueryTextListener{

    View vista;

    RecyclerView recyclerReferencias;
    ArrayList<Referencia> listaRefe;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ReferenciaAdapter miAdapter;



    public ReferenciaFragment() {

    }


    public static ReferenciaFragment newInstance(String param1, String param2) {
        ReferenciaFragment fragment = new ReferenciaFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_referencia, container, false);
        setHasOptionsMenu(true);

        listaRefe=new ArrayList<Referencia>();
        recyclerReferencias = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerReferencias.setLayoutManager(new LinearLayoutManager(vista.getContext()));
        recyclerReferencias.setHasFixedSize(true);

        request = Volley.newRequestQueue(vista.getContext());
        cargarWebService();
        return vista;
    }

    private void cargarWebService() {

        pDialog=new ProgressDialog(vista.getContext());
        pDialog.setMessage("Cargando Referencias...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                vista.getContext().getSystemService(vista.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //String url = "http://192.168.0.102:81/empresis/WsJSONConsultaReferencia.php";
            String url = "http://192.168.0.104:81/Empresis/conexion.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Referencia referencias;

                    /**
                     * Traemos la lista local de referencias mediante la librería Sugar
                     * y los eliminamos
                     */
                    listaRefe = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
                    Referencia.deleteAll(Referencia.class);
                    try {

                       /* JSONArray json=response.optJSONArray("fp_refer");

                        for (int i=0; i<json.length();i++){
                            referencias=new Referencia();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            referencias.setNomref(jsonArrayChild.optString("NOMBREREF"));
                            referencias.setCodRef(jsonArrayChild.optString("CODIGOREF"));*/
                        JSONArray json=response.optJSONArray("Product");

                        for (int i=0; i<json.length();i++){
                            referencias=new Referencia();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            referencias.setNomref(jsonArrayChild.optString("CodRef"));
                            referencias.setCodRef(jsonArrayChild.optString("NameRef"));
                            referencias.setPrice(jsonArrayChild.optString("Vr_Veniva"));
                            referencias.setQuantity("1");
                            referencias.setState(false);
                            listaRefe.add(referencias);
                            /**
                             * Guardamos la lista de referencias de manera local con sugar
                             * */
                            referencias.save();
                        }
                        pDialog.dismiss();
                        miAdapter=new ReferenciaAdapter(vista.getContext(),listaRefe);
                        recyclerReferencias.setAdapter(miAdapter);

                    } catch (JSONException e) {

                        System.out.println();
                        e.printStackTrace();
                        pDialog.hide();
                        System.out.println(response);
                        Snackbar.make(getView(), "No se ha podido establecer conexión con el servidor" +" "+response, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(vista.getContext(), "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();
                        datosLocales();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    if (error.toString().contains("com.android.volley.NoConnectionError")){
                        Toast.makeText(vista.getContext(), "No se puede conectar, verifique que el servidor se encuentre disponible", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(vista.getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                    }

                    datosLocales();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.add(jsonObjectRequest);
        } else {
            Toast.makeText(vista.getContext(), "No se pudo sincronizar, Verifique que cuenta con acceso a Internet", Toast.LENGTH_SHORT).show();



            pDialog.dismiss();
            listaRefe= (ArrayList<Referencia>) Referencia.listAll(Referencia.class);

            datosLocales();
        }
    }

    private void datosLocales() {
        listaRefe = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
        miAdapter=new ReferenciaAdapter(getContext(),listaRefe);
        recyclerReferencias.setAdapter(miAdapter);
    }

    //Agregamos los metodos necesarios para nuestro Scope
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_items,menu);
        MenuItem itemBuscar = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemBuscar);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<Referencia> query = new ArrayList<>();

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
