package hapkiduki.net.empresis.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.TerceroAdapter;
import hapkiduki.net.empresis.clases.DeleteListener;
import hapkiduki.net.empresis.clases.RecyclerClick;
import hapkiduki.net.empresis.clases.RecyclerTouch;
import hapkiduki.net.empresis.clases.Tercero;

public class TerceroActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    RecyclerView recyclerTerceros;
    ArrayList<Tercero> listaTerce;
    TerceroAdapter miAdapter;

    public interface Selecciona{
        public String Selected(int codigo);
    }

    Selecciona listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercero);

        listaTerce=new ArrayList<Tercero>();
        recyclerTerceros = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerTerceros.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerTerceros.setHasFixedSize(true);

        datosLocales();
        implemtaInterface();

    }

    private void implemtaInterface() {
        recyclerTerceros.addOnItemTouchListener(new RecyclerTouch(this, recyclerTerceros, new RecyclerClick() {
            @Override
            public void onClick(View view, int position) {
                listener = miAdapter;
                String codigo = listener.Selected(position);
                String tel = "";
                String dir = "";
                for (int i = 0; i < listaTerce.size(); i++) {
                    if (listaTerce.get(i).getDni().contains(codigo.trim())){
                        tel = listaTerce.get(i).getTelefono().trim();
                        dir = listaTerce.get(i).getDireccion().trim();
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("DNI", codigo);
                intent.putExtra("Tel", tel);
                intent.putExtra("Dir", dir);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void datosLocales() {
        /**
         * Traemos la lista de terceros de manera local con sugar y lo
         * Bindamos al Reciclerview
         */

        listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
        miAdapter=new TerceroAdapter(this,listaTerce);
        recyclerTerceros.setAdapter(miAdapter);
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
