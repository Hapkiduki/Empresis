package hapkiduki.net.empresis.fragments;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.adapters.PedidoAdapter;
import hapkiduki.net.empresis.clases.Pedido;
import hapkiduki.net.empresis.clases.Referencia;
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
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    boolean SINC = false;

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

        String pedido = "Pedido ";
        pedidos = Pedido.listAll(Pedido.class, "id");
        miAdapter=new PedidoAdapter(getContext(),pedidos);
        recyclerPedidos.setAdapter(miAdapter);
        miAdapter.notifyDataSetChanged();
        pedido += "Registros: "+pedidos != null && pedidos.size() > 0 ? pedidos.size() : 0;

        for (Pedido p : pedidos){
            try {
              /*  pedido += " Cliente: " +p.getTercero().getTercero();
                pedido += " Productos: "+p.getProducts().size();
                for (Referencia r : p.getProducts()) {
                    pedido += r.getNomref();
                    pedido += " Cantidad: "+r.getCantPed();
                }*/
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
        itemSync.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(sincronizarPedidos()) {Pedido.deleteAll(Pedido.class);}
                cargarWebService();
                contenedor.setVisibility(pedidos.size() > 0 ? View.INVISIBLE : View.VISIBLE);

                return true;
            }
        });
        // itemSync.setVisible(contenedor.getVisibility() == View.INVISIBLE ? true : false);
        itemSync.setVisible(true);
    }

    private boolean sincronizarPedidos() {

        pDialog=new ProgressDialog(vista.getContext());
        pDialog.setMessage("Sincronizando Pedidos...");
        pDialog.show();

        ConnectivityManager connMgr = (ConnectivityManager)
                vista.getContext().getSystemService(vista.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // for (Pedido p : pedidos) {

            String cliente =  "Carlos";
            String[] productos = {"Papaya", "Mango", "Pera", "Sandia"};
            String cantidad = "10";


            HashMap<String, String> map = new HashMap<>();// Mapeo previo

            map.put("cliente", cliente);
            // for (int i = 0; i < productos.length; i++) {
            map.put("producto", Arrays.toString(productos));
            //}
            map.put("cantidad", cantidad);

            // Crear nuevo objeto Json basado en el mapa
            JSONObject jobject = new JSONObject(map);

            // Depurando objeto Json...
            Log.d(TAG, jobject.toString());
            // Actualizar datos en el servidor
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            "http://192.168.0.103:81/Empresis/pedido.php",
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
            // }
            pDialog.dismiss();
        } else {
            SINC = false;
            Toast.makeText(vista.getContext(), "No se pudo sincronizar, Verifique que cuenta con acceso a Internet", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }


        return  SINC;
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
                    SINC = false;
                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    break;

                case "2":
                    SINC = false;
                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
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
