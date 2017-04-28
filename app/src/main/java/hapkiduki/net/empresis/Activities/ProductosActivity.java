package hapkiduki.net.empresis.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.clases.RecyclerClick;
import hapkiduki.net.empresis.clases.RecyclerTouch;
import hapkiduki.net.empresis.clases.Referencia;

public class ProductosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener,
        ReferenciaAdapter.ChooseListener {

    RecyclerView recyclerReferencias;
    ArrayList<Referencia> listaRefe;
    ReferenciaAdapter miAdapter;
    List<Integer> listaPosiciones;
    boolean filterStatus;

    int quantity;

    private LinearLayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);


        layoutManager = new LinearLayoutManager(this);
        listaRefe=new ArrayList<Referencia>();
        recyclerReferencias = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerReferencias.setLayoutManager(layoutManager);
        recyclerReferencias.setHasFixedSize(true);

        quantity = 0;

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        datosLocales();
        implementarInterfaz();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            if (item.getItemId() == android.R.id.home) {
                List<Referencia> items = new ArrayList<>();
                for (int posicion : listaPosiciones){
                    items.add(listaRefe.get(posicion));
                }


                Intent intent = new Intent();
                intent.putExtra("Productos", (Serializable) items);
                intent.putIntegerArrayListExtra("posicion", (ArrayList<Integer>) (listaPosiciones.size() > 0 ? listaPosiciones : 0));
                setResult(RESULT_OK, intent);
                finish();
                super.onBackPressed();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    private void implementarInterfaz() {
        recyclerReferencias.addOnItemTouchListener(new RecyclerTouch(getApplication(), recyclerReferencias, new RecyclerClick() {
            @Override
            public void onClick(View view, int position) {

                if(obtenerSeleccionados(position))
                    numberPickerDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Cristo te ama", Toast.LENGTH_SHORT).show();
            }
        }));
    }


    private boolean obtenerSeleccionados(int position) {

        boolean valor = false;
        listaPosiciones = new ArrayList<>();
        valor = miAdapter.toggleSelection(position);

        boolean hasCheckedItems = miAdapter.getSelectedCount() > 0;


        SparseBooleanArray selected = miAdapter.getSelectedIds();

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                listaPosiciones.add(selected.keyAt(i));
            }
        }

        Snackbar.make(this.recyclerReferencias, selected.size() + " Productos seleccionados.", Snackbar.LENGTH_LONG).show();
        return valor;
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
        miAdapter.filter(query,listaRefe);
        filterStatus = true;
        return true;
    }

    //Dialog para escojer la cantidad
    private void numberPickerDialog(final int position){

        //final Referencia referencia = new Referencia();
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        NumberPicker.OnValueChangeListener changeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quantity = newVal;
                //listaRefe.get(position).setQuantity(""+quantity);
                listaRefe.get(position).setCantPed(""+quantity);
            }
        };

        numberPicker.setOnValueChangedListener(changeListener);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setView(numberPicker).setIcon(R.mipmap.ic_launcher);
        dialog.setInverseBackgroundForced(true);
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

        datosLocales();
    }

    @Override
    public void choose(String codigo) {
        for (int i = 0; i < listaRefe.size(); i++) {
            if (listaRefe.get(i).getCodRef().trim() == codigo.trim())
                Toast.makeText(this, "La posición es: "+i, Toast.LENGTH_SHORT).show();
        }
    }
}

