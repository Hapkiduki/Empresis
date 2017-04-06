package hapkiduki.net.empresis.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.PedidoAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.Tercero;


public class PedidosFragment extends Fragment implements SearchView.OnQueryTextListener{

    View vista;

    RecyclerView recyclerPedidos;
    List<Pedido> pedidos;
    PedidoAdapter miAdapter;
    LinearLayout contenedor;


    public PedidosFragment() {

    }


    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_pedidos, container, false);
        setHasOptionsMenu(true);

        pedidos = new ArrayList<Pedido>();
        recyclerPedidos = (RecyclerView) vista.findViewById(R.id.recycler_pedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(vista.getContext()));
        recyclerPedidos.setHasFixedSize(true);
        contenedor = (LinearLayout) vista.findViewById(R.id.content_sinc);

        cargarWebService();

        contenedor.setVisibility(pedidos.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        return vista;
    }

    private void cargarWebService() {
        Tercero t = new Tercero();
        String pedido = "Pedido ";
        pedidos = Pedido.listAll(Pedido.class, "id");
        miAdapter=new PedidoAdapter(getContext(),pedidos);
        recyclerPedidos.setAdapter(miAdapter);
        pedido += "Registros: "+pedidos != null ? pedidos.size() : 0;

        for (Pedido p : pedidos){
            try {
                pedido += " Cliente: " +p.getTercero().getTercero();
            }catch (Exception e){
                pedido += " Error: "+e.getMessage();
            }
            pedido+= " Total: "+DecimalFormat.getCurrencyInstance(Locale.US).format(p.getPrecioTotal());
        }
        System.out.println("Su pedido fué: "+pedido);
        Toast.makeText(vista.getContext(), pedido , Toast.LENGTH_LONG).show();

    }


    //Agregamos los metodos necesarios para nuestro Scope
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_pedido,menu);
        MenuItem itemBuscar = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemBuscar);
        searchView.setOnQueryTextListener(this);

        MenuItem itemSync = menu.findItem(R.id.item_sync);
        itemSync.setVisible(contenedor.getVisibility() == View.INVISIBLE ? true : false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ArrayList<Pedido> consulta = new ArrayList<>();
        /*try {
            double precio = Double.parseDouble(query);
            for (Pedido pedido : listaPedido){
                double costo = pedido.getCost_total();

                if (costo == precio){
                    consulta.add(pedido);
                }
            }
        }catch (NumberFormatException e){
            String parametro = query;
            for (Pedido pedido : listaPedido){
                String cliente = pedido.getTercero();

                if (cliente.contains(query)){
                    consulta.add(pedido);
                }
            }
        }
        miAdapter.filter(consulta);*/
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<Pedido> query = new ArrayList<>();

        /*for (Pedido pedido : listaPedido){
            String nomCliente = pedido.getTercero().toLowerCase();
            double precio = pedido.getCost_total();

            if (nomCliente.contains(newText)){
                query.add(pedido);
            }
        }
        miAdapter.filter(query);*/
        return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
