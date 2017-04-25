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
import android.widget.Toast;

import com.orm.SugarApp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.TerceroDialog;
import hapkiduki.net.empresis.adapters.GestionPedidoAdapter;
import hapkiduki.net.empresis.adapters.PedidoAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.PedidoReferencia;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

public class PedidosActivity extends AppCompatActivity implements TerceroDialog.TerceroDialogListner, SearchView.OnQueryTextListener{

    RecyclerView recyclerProdu;
    List<Tercero> listaTerce;
    GestionPedidoAdapter miAdapter;
    TextView dni, telefono, direccion;

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

                    referencia.setPrice(""+Integer.parseInt(referencia.getCantPed()) * Double.parseDouble(referencia.getPrice()));
                    List<Referencia> notes = Referencia.findWithQuery(Referencia.class, "Select * from referencia where cod_ref = ?", referencia.getCodRef());
                    notes.get(0).setCantPed(referencia.getCantPed());
                    notes.get(0).save();
                }
                try {
                    int cantidad = Integer.parseInt(String.valueOf(data.getExtras().getIntegerArrayList("posicion").size()));
                    posPro = new int[cantidad];
                    for (int i = 0; i < cantidad; i++)
                        posPro[i] = Integer.parseInt(String.valueOf(data.getExtras().getIntegerArrayList("posicion").get(i)));
                }catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                consultar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void consultar() {
        List<Pedido> pedidos;
        String pedido = "Pedido ";
        pedidos = Pedido.listAll(Pedido.class, "id");
        pedido += "Registros: "+pedidos != null ? pedidos.size() : 0;

        for (Pedido p : pedidos){
            try {
                String cantPed = "0";
                pedido += " Cliente: " +p.getTercero().getTercero();
                pedido += " Productos: "+p.getProducts().size();
                for (Referencia r : p.getProducts()){
                    cantPed = r.getCantPed();
                    pedido += " Producto: "+r.getNomref();
                    pedido += " Cantidad: "+ cantPed;
                    pedido += "costo: "+ r.getPrice();
                }
            }catch (Exception e){
                pedido += " Error: "+e.getMessage();
            }
            pedido+= " Total: "+ DecimalFormat.getCurrencyInstance(Locale.US).format(p.getPrecioTotal());
        }
        System.out.println("Su pedido fué: "+pedido);
        Toast.makeText(this, pedido , Toast.LENGTH_LONG).show();

    }

    private void generarPedido() {

        double costEnd = 0;

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
        }catch (Exception e){
            Toast.makeText(this, String.format("Ocurrió un %1$s excepción", e.getMessage()), Toast.LENGTH_LONG).show();
        }
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
