package hapkiduki.net.empresis.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.PedidoAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.VolleySingleton;


public class PedidosFragment extends Fragment implements SearchView.OnQueryTextListener{

    /**
     * Etiqueta para depuración
     */
    private static final String TAG = PedidosFragment.class.getSimpleName();

    View vista;

    RecyclerView recyclerPedidos;
    List<Pedido> pedidos;
    PedidoAdapter miAdapter;
    LinearLayout contenedor;
    ProgressDialog pDialog;

    public PedidosFragment() {

    }

    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


    public static PedidosFragment newInstance() {
        PedidosFragment fragment = new PedidosFragment();
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

        pedidos = new ArrayList<>();
        recyclerPedidos = (RecyclerView) vista.findViewById(R.id.recycler_pedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(vista.getContext()));
        recyclerPedidos.setHasFixedSize(true);
        contenedor = (LinearLayout) vista.findViewById(R.id.content_sinc);

        cargarWebService();
        contenedor.setVisibility(pedidos.size() > 0 ? View.INVISIBLE : View.VISIBLE);

        return vista;
    }

    private void cargarWebService() {

        pedidos = Pedido.listAll(Pedido.class, "id");
        miAdapter=new PedidoAdapter(getContext(),pedidos);
        recyclerPedidos.setAdapter(miAdapter);
        miAdapter.notifyDataSetChanged();
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
        itemSync.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sincronizarPedidos();
                cargarWebService();
                contenedor.setVisibility(pedidos.size() > 0 ? View.INVISIBLE : View.VISIBLE);

                return true;
            }
        });
        itemSync.setVisible(contenedor.getVisibility() == View.INVISIBLE ? true : false);
        itemBuscar.setVisible(contenedor.getVisibility() == View.INVISIBLE ? true : false);
    }

    private void sincronizarPedidos() {

        pDialog=new ProgressDialog(getActivity());
        pDialog.setMessage("Sincronizando Pedidos...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            for (Pedido p : pedidos) {

                String[] productos = new String[p.getProducts().size()];
                String[] cantidades = new String[p.getProducts().size()];
                String[] precios = new String[p.getProducts().size()];

                String cliente =  p.getTercero().getDni();
                for(int i = 0; i < p.getProducts().size(); i++) {
                    productos[i] = p.getProducts().get(i).getCodRef();
                    cantidades[i] = p.getProducts().get(i).getCantPed();
                    precios[i] = p.getProducts().get(i).getPrice();
                }
                String total = ""+p.getPrecioTotal();

                HashMap<String, String> map = new HashMap<>();// Mapeo previo

                map.put("cliente", cliente);

                map.put("producto", Arrays.toString(productos));
                map.put("cantidad", Arrays.toString(cantidades));
                map.put("precio", Arrays.toString(precios));


                // Crear nuevo objeto Json basado en el mapa
                JSONObject jobject = new JSONObject(map);

                // Depurando objeto Json...
                Log.d(TAG, jobject.toString());
                // Actualizar datos en el servidor
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                "https://empresis.000webhostapp.com/pedido.php",
                                jobject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta del servidor
                                        procesarRespuesta(response);

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "Ocurrió un error al sincronizar: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }
            pDialog.dismiss();

        } else {
            Toast.makeText(vista.getContext(), "No se pudo sincronizar, Verifique que cuenta con acceso a Internet", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

    private void procesarRespuesta(JSONObject response) {
        try {
            // Obtener estado
            String estado = null;
            try {
                estado = response.getString("estado");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    Pedido.deleteAll(Pedido.class);
                    refresh();
                    break;

                case "2":

                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    Log.d("Grave error", "Error :"+mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
