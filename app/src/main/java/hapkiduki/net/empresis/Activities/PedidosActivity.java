package hapkiduki.net.empresis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.GestionPedidoAdapter;
import hapkiduki.net.empresis.clases.DeleteListener;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.PedidoReferencia;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

public class PedidosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        DeleteListener{

    RecyclerView recyclerProdu;
    List<Tercero> listaTerce;
    GestionPedidoAdapter miAdapter;
    TextInputLayout til_dni, til_telefono, til_direccion;
    MenuItem itemBuscar;

    private static final int REQUEST_CODE = 1;

    List<Referencia> lista;
    int posFin;
    int[] posPro;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lista = new ArrayList<>();
        listaTerce = new ArrayList<>();

        recyclerProdu = (RecyclerView) findViewById(R.id.recycler_produ);
        recyclerProdu.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerProdu.setHasFixedSize(true);

        til_dni = (TextInputLayout) findViewById(R.id.til_dni);
        til_telefono = (TextInputLayout) findViewById(R.id.til_telefono);
        til_direccion = (TextInputLayout) findViewById(R.id.til_direccion);

        posFin = 0;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearPedido();
            }


        });

        findViewById(R.id.ed_dni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTerceros();
            }
        });
    }

    private void crearPedido() {
        Intent intent = new Intent(PedidosActivity.this, ProductosActivity.class);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK) {

                String dni = data.getExtras().getString("DNI");
                String tel = data.getExtras().getString("Tel");
                String dir = data.getExtras().getString("Dir");
                if (!TextUtils.isEmpty(dni)) {
                    pasaParametros(dni, TextUtils.isEmpty(tel) ? "Desconocido" : tel, TextUtils.isEmpty(dir) ? "Desconocido" : dir);
                }else {
                    lista = (List<Referencia>) data.getExtras().getSerializable("Productos");

                    for (Referencia referencia : lista) {

                        referencia.setPrice("" + Integer.parseInt(referencia.getCantPed()) * Double.parseDouble(referencia.getPrice()));
                        List<Referencia> notes = Referencia.findWithQuery(Referencia.class, "Select * from referencia where cod_ref = ?", referencia.getCodRef());
                        notes.get(0).setCantPed(referencia.getCantPed());
                        notes.get(0).save();
                    }
                    try {
                        int cantidad = Integer.parseInt(String.valueOf(data.getExtras().getIntegerArrayList("posicion").size()));
                        posPro = new int[cantidad];
                        for (int i = 0; i < cantidad; i++)
                            posPro[i] = Integer.parseInt(String.valueOf(data.getExtras().getIntegerArrayList("posicion").get(i)));
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    miAdapter = new GestionPedidoAdapter(this, lista);
                    recyclerProdu.setAdapter(miAdapter);
                    miAdapter.notifyDataSetChanged();
                    itemBuscar.setVisible(lista.size() > 0 ? true : false);
                }
            }
        }
    }

    private void pasaParametros(String dni, String tel, String dir) {
        til_dni.getEditText().setText(dni);
        til_telefono.getEditText().setText(tel);
        til_direccion.getEditText().setText(dir);
    }


    private void mostrarTerceros() {
        Intent intent = new Intent(PedidosActivity.this, TerceroActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        itemBuscar = menu.findItem(R.id.item_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemBuscar);
        searchView.setOnQueryTextListener(this);
        itemBuscar.setVisible(lista.size() > 0 ? true : false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.item_ready:
                generarPedido();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generarPedido() {

        double costEnd = 0;
        if (TextUtils.isEmpty(til_dni.getEditText().getText())){
            til_dni.setError("Debe asignar un cliente!");
            return;
        }
        if (lista.size() > 0) {
            for (Referencia producto : lista) {
                costEnd += Double.parseDouble(producto.getPrice());
            }
            Pedido miPedido = new Pedido(listaTerce.get(posFin), costEnd);
            miPedido.save();
            List<Referencia> productos = new ArrayList<>();
            productos = Referencia.listAll(Referencia.class);
            try {
                for (int i = 0; i < posPro.length; i++) {
                    //Toast.makeText(this, String.format("Existen %1$s productos", productos.get(posPro[i]).getCodRef()), Toast.LENGTH_SHORT).show();
                    PedidoReferencia pedidoReferencia = new PedidoReferencia(miPedido, productos.get(posPro[i]));
                    pedidoReferencia.save();
                }
            } catch (Exception e) {
                Toast.makeText(this, String.format("Ocurrió un %1$s excepción", e.getMessage()), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "No hay movimiento asignado!", Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
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
        for (Referencia referencia: lista){
            String nomReferencia = referencia.getNomref().toLowerCase();
            String codRef = referencia.getCodRef();
            if (nomReferencia.contains(newText) || codRef.contains(newText)){
                query.add(referencia);
            }
        }


        miAdapter.setFilter(query);
        return true;
    }

    @Override
    public void pinchado(String codigo) {

        try{
            int posCod = 0;
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getCodRef() == codigo){
                    posCod = i+1;
                }
            }
            if (posCod > 0)
                lista.remove(posCod - 1);

            miAdapter.notifyDataSetChanged();
        }catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
