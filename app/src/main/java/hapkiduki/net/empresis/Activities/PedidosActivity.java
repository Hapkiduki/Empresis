package hapkiduki.net.empresis.Activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.TerceroDialog;
import hapkiduki.net.empresis.adapters.GestionPedidoAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

public class PedidosActivity extends AppCompatActivity implements TerceroDialog.TerceroDialogListner, SearchView.OnQueryTextListener{

    RecyclerView recyclerProdu;
    // ArrayList<Referencia> listaTerce;
    List<Tercero> listaTerce;
    GestionPedidoAdapter miAdapter;
    TextView dni, telefono, direccion;

    private static final int REQUEST_CODE = 1;

    List<Referencia> lista;
    int posFin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        lista = new ArrayList<>();
        listaTerce = new ArrayList<>();
        //listaTerce=new ArrayList<Referencia>();
        recyclerProdu = (RecyclerView) findViewById(R.id.recycler_produ);
        recyclerProdu.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerProdu.setHasFixedSize(true);

        dni = (TextView) findViewById(R.id.ed_dni);
        telefono = (TextView) findViewById(R.id.campo_telefono);
        direccion = (TextView) findViewById(R.id.campo_direccion);

        posFin = 0;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearPedido();
            }


        });

        dni.setOnClickListener(new View.OnClickListener() {
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
                // String result = data.getStringExtra("Productos");
                lista = (List<Referencia>) data.getExtras().getSerializable("Productos");
                for (Referencia referencia : lista){
                    // Toast.makeText(this, "Selecciona: "  + referencia.getNomref(), Toast.LENGTH_SHORT).show();
                    referencia.setPrice(""+Integer.parseInt(referencia.getQuantity()) * Double.parseDouble(referencia.getPrice()));
                }


                miAdapter = new GestionPedidoAdapter(this, lista);
                recyclerProdu.setAdapter(miAdapter);

            }
        }
    }

    private void mostrarTerceros() {
        FragmentManager fragmentManager = getFragmentManager();
        TerceroDialog terceroDialog = new TerceroDialog();
        terceroDialog.show(fragmentManager, "dialogTer");

    }


    @Override
    public void onDialogPositiveClick(ArrayList<Tercero> terceros, int posicion) {
        listaTerce.addAll(terceros);
        posFin = posicion;
        //Toast.makeText(this, "elemento: "+posicion+" Tercero: "+terceros.get(posicion).getTercero(), Toast.LENGTH_LONG).show();
        dni.setText(terceros.get(posicion).getDni());
        telefono.setText(terceros.get(posicion).getTelefono());
        direccion.setText(terceros.get(posicion).getDireccion());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        MenuItem itemBuscar = menu.findItem(R.id.item_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemBuscar);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.item_ready:
                generarPedido();
                Snackbar.make(this.recyclerProdu, "Pedido Creado!", Snackbar.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generarPedido() {

       /* String pedido = "\n PEDIDO PARA "+listaTerce.get(posFin).getTercero().toString();
        pedido += "\n ********PRODUCTOS***************";
        for (Referencia r : lista)
            pedido += "\n" + r.getNomref() + "\n Cantidad: " + r.getQuantity()+ "\n Precio: "+r.getPrice();
        Toast.makeText(this, "Su pedido fué: "+ pedido, Toast.LENGTH_LONG).show();


        intent.putExtra("Pedido", listaTerce.get(posFin).getTercero().toString());
*/
        Intent intent = new Intent();
        /**
         * Enviamos los parametros para generar el pedido
         */

        double costEnd = 0;
        Pedido mPedido = new Pedido();
        mPedido.setTercero(listaTerce.get(posFin).getDni().toString());
        mPedido.setProductos(lista);
        for (Referencia producto : lista) {
            costEnd += Double.parseDouble(producto.getQuantity());
        }
        mPedido.setCost_total(costEnd);

        intent.putExtra("ObjectPedido", mPedido);

        setResult(RESULT_OK, intent);
        finish();

    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
