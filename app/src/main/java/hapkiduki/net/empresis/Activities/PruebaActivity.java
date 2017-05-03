package hapkiduki.net.empresis.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.orm.SugarContext;

import java.util.ArrayList;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.Vendedor_Dialog;
import hapkiduki.net.empresis.clases.Sincronizar;
import hapkiduki.net.empresis.fragments.HomeFragment;
import hapkiduki.net.empresis.fragments.PedidosFragment;
import hapkiduki.net.empresis.fragments.ReferenciaFragment;
import hapkiduki.net.empresis.fragments.TerceroFragment;

public class PruebaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener,TerceroFragment.OnFragmentInteractionListener,
        ReferenciaFragment.OnFragmentInteractionListener, PedidosFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SugarContext.init(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                String vendedor = pref.getString("vendedor_text", "0");
                if (vendedor.isEmpty() || vendedor.equals("1234")) {
                    Toast.makeText(PruebaActivity.this, "No hay un vendedor configurado!", Toast.LENGTH_LONG).show();
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    Vendedor_Dialog vendedorDialog = new Vendedor_Dialog();
                    vendedorDialog.show(fragmentManager, "dialogo");
                }else {
                Intent intent;
                intent= new Intent(PruebaActivity.this,PedidosActivity.class);
                startActivityForResult(intent, 1);
                }
            }
        });

        if (savedInstanceState == null){
            seleccion();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void seleccion() {

        Fragment fragment =  fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_prueba, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        setTitle("Inicio"); // Setear título actual
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prueba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        int id = item.getItemId();

        Fragment fragment = null;
        Boolean fragmentSeleccionado = false;

        if (id == R.id.nav_camera) {
            fragment = new HomeFragment();
            fragmentSeleccionado = true;
            setTitle(item.getTitle());
        } else if (id == R.id.nav_gallery) {
            fragment = new TerceroFragment();
            fragmentSeleccionado = true;
            setTitle(item.getTitle());
        } else if (id == R.id.nav_slideshow) {
            fragment = new ReferenciaFragment();
            fragmentSeleccionado = true;
            setTitle(item.getTitle());
        } else if (id == R.id.nav_manage) {
            fragment = new PedidosFragment();
            fragmentSeleccionado = true;
            setTitle(item.getTitle());
        }else if (id == R.id.nav_sinc){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea iniciar Sincronización?");
            builder.setTitle("!CONFIRMACIÓN¡");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("Sincronizar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    iniciarSincronizacion();
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.create();
            builder.show();

        }else if (id == R.id.nav_exit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea salir de la aplicación?");
            builder.setTitle("SALIR");
            builder.setIcon(R.drawable.ic_action_power_settings_new);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.create();
            builder.show();
        }

        if (fragmentSeleccionado){
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_prueba, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void iniciarSincronizacion() {

        final CharSequence[] items = {" Terceros "," Referencias"," Pedidos "};
        final boolean[] itemsele = {false, false, true};
        final ArrayList seletedItems=new ArrayList();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Seleccione los modulos a sincronizar")
                .setMultiChoiceItems(items, itemsele, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (indexSelected == 2){
                            Toast.makeText(PruebaActivity.this, "Selección no permitida!", Toast.LENGTH_LONG).show();
                            ((AlertDialog)dialog).getListView().setItemChecked(2, true);
                        }
                        else if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("Iniciar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Sincronizar.iniciarSincronizacion(PruebaActivity.this, seletedItems);


                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
