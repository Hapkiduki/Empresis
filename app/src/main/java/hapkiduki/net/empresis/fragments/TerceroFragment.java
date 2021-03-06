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
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.TerceroAdapter;
import hapkiduki.net.empresis.clases.Tercero;
import hapkiduki.net.empresis.clases.VolleySingleton;


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

        SugarContext.init(getActivity());
        // Inflate the layout for this fragment
        listaTerce=new ArrayList<Tercero>();
        recyclerTerceros = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerTerceros.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerTerceros.setHasFixedSize(true);

        //request = Volley.newRequestQueue(this.getContext());

        try {
            //cargarWebService();
            datosLocales();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", "Ocasionado por: "+e.getMessage());
        }
        return vista;
    }

    private void cargarWebService() {

        try {
            //request = Volley.newRequestQueue(this.getContext());
            request = VolleySingleton.getInstance(vista.getContext()).getRequestQueue();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Cargando Terceros...");
            pDialog.show();


            ConnectivityManager connMgr = (ConnectivityManager)
                    vista.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //String url = "http://192.168.0.104:81/empresis/WsJSONConsultaTercero.php";
                String url = "https://empresis.000webhostapp.com/WsJSONConsultaTercero.php";

                jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Tercero terceros;
                        /**
                         * Traemos la lista local de terceros mediante la libreria SUGAR
                         */

                        //listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
                        /**
                         * Eliminamos todos los terceros de la lista local con el metodo de Sugar
                         */

                        Tercero.deleteAll(Tercero.class);

                        try {

                            JSONArray json = response.optJSONArray("Customer");

                            for (int i = 0; i < json.length(); i++) {
                                terceros = new Tercero();
                                JSONObject jsonArrayChild = json.getJSONObject(i);
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
                            miAdapter = new TerceroAdapter(getContext(), listaTerce);
                            recyclerTerceros.setAdapter(miAdapter);

                        } catch (JSONException e) {
                            System.out.println();
                            Log.e("Ocurrió un error: ", "El error es: " + e.getMessage());
                            e.printStackTrace();
                            pDialog.hide();
                            System.out.println(response);
                            Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                                    " " + response, Toast.LENGTH_LONG).show();

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
                }
                );

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                //request.add(jsonObjectRequest);
                VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
            } else {
                Toast.makeText(getActivity(), "No se pudo sincronizar, Verifique que " +
                        "cuenta con acceso a Internet", Toast.LENGTH_LONG).show();
                pDialog.dismiss();

                datosLocales();
            }
        }catch (Exception e){
            Log.e("Ocurrio un error: ", "El error es: "+e.getMessage());
        }
    }

    private void datosLocales() {
        /**
         * Traemos la lista de terceros de manera local con sugar y lo
         * Bindamos al Reciclerview
         */
        try {
            listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class) != null ? (ArrayList<Tercero>) Tercero.listAll(Tercero.class) : null;
            miAdapter = new TerceroAdapter(getContext(), listaTerce);
            recyclerTerceros.setAdapter(miAdapter);
        }catch (Exception e){
            Log.e("Ocurrió un Error", "El error es: "+e.getMessage());
        }
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

    public static TerceroFragment newInstance() {
        TerceroFragment fragment = new TerceroFragment();
        return fragment;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
