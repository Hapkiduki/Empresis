package hapkiduki.net.empresis.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.PedidoAdapter;
import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.Referencia;


public class PedidosFragment extends Fragment implements SearchView.OnQueryTextListener{

    View vista;

    RecyclerView recyclerPedidos;
    ArrayList<Pedido> listaPedido;
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

        listaPedido = new ArrayList<Pedido>();
        recyclerPedidos = (RecyclerView) vista.findViewById(R.id.recycler_pedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(vista.getContext()));
        recyclerPedidos.setHasFixedSize(true);
        contenedor = (LinearLayout) vista.findViewById(R.id.content_sinc);

        cargarWebService();

        contenedor.setVisibility(listaPedido.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        return vista;
    }

    private void cargarWebService() {
        /*List<Referencia> productos = new ArrayList<>();
        Referencia referencia = new Referencia();
        referencia.setCodRef("fdffgg");
        referencia.setQuantity("2");
        referencia.setPrice("2500");
        referencia.setNomref("Pera");

        productos.add(referencia);
        Pedido pedido = new Pedido("Andrés Felipe Corrales Ortiz", productos, 25250.20);
        Pedido pedido2 = new Pedido("Julio Palacio", productos, 25250.20);
        Pedido pedido3 = new Pedido("Johnny Palacio", productos, 25250.20);
        Pedido pedido4 = new Pedido("Mauricio Castañeda", productos, 25250.20);
        listaPedido.add(pedido);
        listaPedido.add(pedido2);
        listaPedido.add(pedido3);
        listaPedido.add(pedido4);



        miAdapter = new PedidoAdapter(vista.getContext(), listaPedido);

        recyclerPedidos.setAdapter(miAdapter);
        miAdapter.notifyDataSetChanged();
        */

        listaPedido = (ArrayList<Pedido>) Pedido.listAll(Pedido.class);
        /*miAdapter = new PedidoAdapter(vista.getContext(), listaPedido);
        recyclerPedidos.setAdapter(miAdapter);*/

       /* String pedido = "Pedidos";
        for (Pedido p : listaPedido){
            pedido += "\n Cliente: "+ p.getTercero();
            pedido += "\n Costo Total del pedido: "+p.getCost_total();
                Toast.makeText(vista.getContext(), "esto es un msg"+(p.getProducto().size() > 0 ? "BN": "Mal"), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(vista.getContext(), pedido , Toast.LENGTH_LONG).show();
*/
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
        try {
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
        miAdapter.filter(consulta);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<Pedido> query = new ArrayList<>();

        for (Pedido pedido : listaPedido){
            String nomCliente = pedido.getTercero().toLowerCase();
            double precio = pedido.getCost_total();

            if (nomCliente.contains(newText)){
                query.add(pedido);
            }
        }
        miAdapter.filter(query);
        return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
