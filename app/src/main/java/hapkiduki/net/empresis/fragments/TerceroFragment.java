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
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.TerceroAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.Tercero;


public class TerceroFragment extends Fragment implements SearchView.OnQueryTextListener{

    private final String TIPO = "tercero";

    private OnFragmentInteractionListener mListener;
    View vista;

    RecyclerView recyclerTerceros;
    ArrayList<Tercero> listaTerce;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    TerceroAdapter miAdapter;

    public TerceroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista=inflater.inflate(R.layout.fragment_tercero, container, false);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        listaTerce=new ArrayList<Tercero>();
        recyclerTerceros = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerTerceros.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerTerceros.setHasFixedSize(true);

        //request = Volley.newRequestQueue(this.getContext());

        cargarWebService();
        return vista;
    }

    private void cargarWebService() {

        request = Volley.newRequestQueue(this.getContext());
        pDialog=new ProgressDialog(getContext());
        pDialog.setMessage("Cargando Terceros...");
        pDialog.show();


        ConnectivityManager connMgr = (ConnectivityManager)
                vista.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = "http://192.168.0.103:81/empresis/WsJSONConsultaTercero.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Tercero terceros;
                    /**
                     * Traemos la lista local de terceros mediante la libreria SUGAR
                     */
                    listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
                    /**
                     * Eliminamos todos los terceros de la lista local con el metodo de Sugar
                     */
                    Tercero.deleteAll(Tercero.class);
                    try {

                        JSONArray json=response.optJSONArray("Customer");

                        for (int i=0; i<json.length();i++){
                            terceros = new Tercero();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            terceros.setDni(jsonArrayChild.optString("CodigoCli"));
                            terceros.setDireccion(jsonArrayChild.optString("Direccion"));
                            terceros.setTercero(jsonArrayChild.optString("NombreCli"));
                            terceros.setTelefono(jsonArrayChild.optString("Telefono"));
                            listaTerce.add(terceros);
                            //System.out.println(referencias.getNomref().toString());

                            /**
                             * Guardamos la lista de terceros en la tabla local con Sugar
                             */
                            terceros.save();
                        }
                        pDialog.hide();
                        // TerceroAdapter miAdapter=new TerceroAdapter(getApplicationContext(),listaTerce);
                        miAdapter=new TerceroAdapter(getContext(),listaTerce);
                        recyclerTerceros.setAdapter(miAdapter);

                    } catch (JSONException e) {
                        System.out.println();
                        e.printStackTrace();
                        pDialog.hide();
                        System.out.println(response);
                        Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();

                        datosLocales();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                    System.out.println();
                    pDialog.hide();
                    Log.d("ERROR: ", error.toString());
                    datosLocales();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.add(jsonObjectRequest);
        } else {
            Toast.makeText(vista.getContext(), "No se pudo sincronizar, Verifique que " +
                    "cuenta con acceso a Internet", Toast.LENGTH_LONG).show();
            pDialog.hide();

            datosLocales();
        }
    }

    private void datosLocales() {
        /**
         * Traemos la lista de terceros de manera local con sugar y lo
         * Bindamos al Reciclerview
         */

        listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
        miAdapter=new TerceroAdapter(getContext(),listaTerce);
        recyclerTerceros.setAdapter(miAdapter);
    }

    //Agregamos el Menu o scope

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_items, menu);
        MenuItem itemBuscar = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemBuscar);
        searchView.setOnQueryTextListener(this);
    }

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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
