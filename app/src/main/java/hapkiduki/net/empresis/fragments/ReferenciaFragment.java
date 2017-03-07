package hapkiduki.net.empresis.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import java.util.zip.Inflater;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.clases.Referencia;

import static hapkiduki.net.empresis.R.id.container;


public class ReferenciaFragment extends Fragment implements SearchView.OnQueryTextListener{


    private OnFragmentInteractionListener mListener;
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

        cargarWebServiceImagenes();
        // Inflate the layout for this fragment
        return vista;
    }

    private void cargarWebServiceImagenes() {

        pDialog=new ProgressDialog(vista.getContext());
        pDialog.setMessage("Cargando Referencias...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                vista.getContext().getSystemService(vista.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = "http://192.168.0.105/empresis/WsJSONConsultaReferencia.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Referencia referencias;

                    /*
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
                            listaRefe.add(referencias);
                            //System.out.println(referencias.getNomref().toString());
                            /**
                             * Guardamos la lista de referencias de manera local con sugar
                             * */
                            referencias.save();
                        }
                        pDialog.dismiss();
                        //ReferenciaAdapter miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);


                        miAdapter=new ReferenciaAdapter(vista.getContext(),listaRefe);
                        recyclerReferencias.setAdapter(miAdapter);

                    } catch (JSONException e) {
                        System.out.println();
                        e.printStackTrace();
                        pDialog.hide();
                        System.out.println(response);
                        Toast.makeText(vista.getContext(), "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();
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

                    System.out.println();
                    Log.d("ERROR: ", error.toString());
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.add(jsonObjectRequest);
        } else {
            Toast.makeText(vista.getContext(), "No se pudo sincronizar, Verifique que " +
                    "cuenta con acceso a Internet", Toast.LENGTH_LONG).show();
            pDialog.dismiss();

            /**
             * Cargamos los datos al recicler view de forma local
             * con SUGAR
             */

            listaRefe = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
            miAdapter=new ReferenciaAdapter(vista.getContext(),listaRefe);
            recyclerReferencias.setAdapter(miAdapter);

        }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
