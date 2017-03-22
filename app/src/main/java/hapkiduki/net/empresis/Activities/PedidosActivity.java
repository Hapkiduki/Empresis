package hapkiduki.net.empresis.Activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.TerceroDialog;
import hapkiduki.net.empresis.adapters.PedidoAdapter;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

public class PedidosActivity extends AppCompatActivity implements TerceroDialog.TerceroDialogListner{

    RecyclerView recyclerProdu;
    ArrayList<Referencia> listaTerce;
    PedidoAdapter miAdapter;
    TextView dni, telefono, direccion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaTerce=new ArrayList<Referencia>();
        recyclerProdu = (RecyclerView) findViewById(R.id.recycler_produ);
        recyclerProdu.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerProdu.setHasFixedSize(true);

        dni = (TextView) findViewById(R.id.ed_dni);
        telefono = (TextView) findViewById(R.id.campo_telefono);
        direccion = (TextView) findViewById(R.id.campo_direccion);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
*/

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
        startActivity(intent);
    }



    private void mostrarTerceros() {
        FragmentManager fragmentManager = getFragmentManager();
        TerceroDialog terceroDialog = new TerceroDialog();
        terceroDialog.show(fragmentManager, "dialogTer");

    }


    @Override
    public void onDialogPositiveClick(ArrayList<Tercero> listaTerce, int posicion) {
        Toast.makeText(this, "elemento: "+posicion+" Tercero: "+listaTerce.get(posicion).getTercero(), Toast.LENGTH_LONG).show();
        dni.setText(listaTerce.get(posicion).getDni());
        telefono.setText(listaTerce.get(posicion).getTelefono());
        direccion.setText(listaTerce.get(posicion).getDireccion());

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


}
