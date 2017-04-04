package hapkiduki.net.empresis.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.clases.RecyclerClick;
import hapkiduki.net.empresis.clases.RecyclerTouch;
import hapkiduki.net.empresis.clases.Referencia;

public class ProductosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerReferencias;
    ArrayList<Referencia> listaRefe;
    ProgressDialog pDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ReferenciaAdapter miAdapter;
    List<Integer> listaPosiciones;

    //Intent intent;
    int quantity;

    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);


        layoutManager = new LinearLayoutManager(this);
        listaRefe=new ArrayList<Referencia>();
        recyclerReferencias = (RecyclerView) findViewById(R.id.recycler_view);
        // recyclerReferencias.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerReferencias.setLayoutManager(layoutManager);
        recyclerReferencias.setHasFixedSize(true);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        quantity = 0;

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        request = Volley.newRequestQueue(this);
        cargarWebService();
        implementarInterfaz();


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            List<Referencia> items = new ArrayList<>();

            for (int posicion : listaPosiciones){
                items.add(listaRefe.get(posicion));
            }
            Intent intent = new Intent();
            intent.putExtra("Productos", (Serializable) items);
            setResult(RESULT_OK, intent);
            finish();
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void implementarInterfaz() {
        recyclerReferencias.addOnItemTouchListener(new RecyclerTouch(getApplication(), recyclerReferencias, new RecyclerClick() {
            @Override
            public void onClick(View view, int position) {

                obtenerSeleccionados(position);
                numberPickerDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Cristo te ama", Toast.LENGTH_SHORT).show();
            }
        }));
    }


    private void obtenerSeleccionados(int position) {
        listaPosiciones = new ArrayList<>();
        miAdapter.toggleSelection(position);

        boolean hasCheckedItems = miAdapter.getSelectedCount() > 0;


        SparseBooleanArray selected = miAdapter.getSelectedIds();

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                listaPosiciones.add(selected.keyAt(i));
            }
        }

        Snackbar.make(this.recyclerReferencias, selected.size() + " Productos seleccionados.", Snackbar.LENGTH_LONG).show();

    }

    private void cargarWebService() {

        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Cargando Productos...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = "http://192.168.0.102:81/Empresis/conexion.php";
            //String url = "http://192.168.0.102:81/empresis/WsJSONConsultaReferencia.php";
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

                        //JSONArray json=response.optJSONArray("fp_refer");
                        JSONArray json=response.optJSONArray("Product");

                        for (int i=0; i<json.length();i++){
                            referencias=new Referencia();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                           /* referencias.setNomref(jsonArrayChild.optString("NOMBREREF"));
                            referencias.setCodRef(jsonArrayChild.optString("CODIGOREF"));
                            referencias.setPrice("VR_VENIVA");*/
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
                        miAdapter=new ReferenciaAdapter(getApplicationContext(),listaRefe);
                        recyclerReferencias.setAdapter(miAdapter);

                    } catch (JSONException e) {

                        System.out.println();
                        e.printStackTrace();
                        pDialog.hide();
                        System.out.println(response);
                        Toast.makeText(getApplicationContext(), "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();
                        datosLocales();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    if (error.toString().contains("com.android.volley.NoConnectionError")){
                        Toast.makeText(getApplicationContext(), "No se puede conectar, verifique que el servidor se encuentre disponible", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                    }

                    datosLocales();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.add(jsonObjectRequest);
        } else {
            Toast.makeText(getApplicationContext(), "No se pudo sincronizar, Verifique que cuenta con acceso a Internet", Toast.LENGTH_SHORT).show();



            pDialog.dismiss();
            listaRefe= (ArrayList<Referencia>) Referencia.listAll(Referencia.class);

            datosLocales();
        }
    }

    private void datosLocales() {
        /**
         * Cargamos los datos al recicler view de forma local
         * con SUGAR
         */

        listaRefe = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
        miAdapter=new ReferenciaAdapter(getApplication(),listaRefe);
        recyclerReferencias.setAdapter(miAdapter);

    }

    //Agregamos los metodos necesarios para nuestro Scope

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
        //Esto es un foreach en java
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

    //Dialog para escojer la cantidad
    private void numberPickerDialog(final int position){

        final Referencia referencia = new Referencia();
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        NumberPicker.OnValueChangeListener changeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quantity = newVal;
                listaRefe.get(position).setQuantity(""+quantity);
            }
        };

        numberPicker.setOnValueChangedListener(changeListener);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setView(numberPicker).setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Cantidad de producto").setMessage("Seleccione la cantidad de producto a agregar entre 1 a 100");
        dialog.setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    //ESte metodo es el encargado de actualizar el recyclerView
    @Override
    public void onRefresh() {

        cargarWebService();
    }


}

