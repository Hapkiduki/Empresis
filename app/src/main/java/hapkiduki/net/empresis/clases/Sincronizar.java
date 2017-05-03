package hapkiduki.net.empresis.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hapkiduki.net.empresis.Activities.PruebaActivity;
import hapkiduki.net.empresis.R;

/**
 * Created by Programa-PC on 25/04/2017.
 */

public class Sincronizar {

    public static RequestQueue request;
    public static JsonObjectRequest jsonObjectRequest;
    static ProgressDialog dialog;

    private static SharedPreferences pref;
    private static String HOST;

    public static void iniciarSincronizacion(PruebaActivity pruebaActivity, ArrayList seletedItems) {

        pref = PreferenceManager.getDefaultSharedPreferences(pruebaActivity);
        HOST = pref.getString("host_text", "https://www.ingesis.co");
        dialog = new ProgressDialog(pruebaActivity);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Iniciando Sincronización");
        dialog.setMessage("Espere mientras se sincroniza.. Este proceso puede tardar unos minutos.");
        dialog.show();

        request = VolleySingleton.getInstance(pruebaActivity).getRequestQueue();
        ConnectivityManager connMgr = (ConnectivityManager)
                pruebaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            sincronizarPedidos(pruebaActivity, HOST);
            int seleccion = seletedItems.size();
            if (seleccion > 0) {
                if (seleccion > 1) {
                    sincronizarReferencias(pruebaActivity, HOST);
                    sincronizarterceros(pruebaActivity, HOST);

                } else {
                    switch ((int) seletedItems.get(0)) {
                        case 0:
                            sincronizarterceros(pruebaActivity, HOST);
                            break;
                        case 1:
                            sincronizarReferencias(pruebaActivity, HOST);
                            break;
                    }
                }
            }

            Toast.makeText(pruebaActivity, "Sincronización Finalizada ", Toast.LENGTH_SHORT).show();

        }else{
            dialog.dismiss();
            Toast.makeText(pruebaActivity, "No se pudo sincronizar, Verifique que " +
                    "cuenta con acceso a Internet", Toast.LENGTH_LONG).show();
        }
    }

    private static void sincronizarterceros(final PruebaActivity pruebaActivity, String HOST) {

        dialog.show();
        Toast.makeText(pruebaActivity, "Inicia Sincronización de terceros ", Toast.LENGTH_SHORT).show();
        try{
            //String url = "https://empresis.000webhostapp.com/WsJSONConsultaTercero.php";
            String url = Sincronizar.HOST +"/WsJSONConsultaTercero.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Tercero terceros;
                    /**
                     * Eliminamos todos los terceros de la lista local con el metodo de Sugar
                     */
                    Tercero.deleteAll(Tercero.class);

                    try {

                        JSONArray json = response.optJSONArray("Customer");

                        for (int i = 0; i < json.length(); i++) {
                            terceros = new Tercero();
                            JSONObject jsonArrayChild = json.getJSONObject(i);
                            terceros.setDni(jsonArrayChild.optString("CodigoCli"));
                            terceros.setDireccion(jsonArrayChild.optString("Direccion"));
                            terceros.setTercero(jsonArrayChild.optString("NombreCli"));
                            terceros.setTelefono(jsonArrayChild.optString("Telefono"));

                            /**
                             * Guardamos la lista de terceros en la tabla local con Sugar
                             */
                            terceros.save();
                        }
                        dialog.dismiss();

                    } catch (JSONException e) {
                        dialog.dismiss();
                        System.out.println();
                        Log.e("Ocurrió un error: ", "El error es: " + e.getMessage());
                        e.printStackTrace();
                        System.out.println(response);
                        Toast.makeText(pruebaActivity, "No se ha podido establecer conexión con el servidor" +
                                " " + response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    if (error.toString().contains("com.android.volley.NoConnectionError")){
                        Toast.makeText(pruebaActivity, "No se puede conectar, verifique que el servidor se encuentre disponible", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(pruebaActivity, "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(pruebaActivity).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            dialog.dismiss();
            Toast.makeText(pruebaActivity, "Ocurrió un error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void sincronizarReferencias(final PruebaActivity pruebaActivity, String HOST) {
        dialog.show();
        Toast.makeText(pruebaActivity, "Inicia Sincronización de referencias ", Toast.LENGTH_SHORT).show();
        try{
            //String url = "https://empresis.000webhostapp.com/conexion.php";
            String url = HOST+"/conexion.php";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Referencia referencias;
                    Referencia.deleteAll(Referencia.class);
                    try {

                        JSONArray json=response.optJSONArray("Product");

                        for (int i=0; i<json.length();i++){
                            referencias=new Referencia();
                            JSONObject jsonArrayChild=json.getJSONObject(i);
                            referencias.setNomref(jsonArrayChild.optString("NameRef"));
                            referencias.setCodRef(jsonArrayChild.optString("CodRef"));
                            referencias.setPrice(jsonArrayChild.optString("Vr_Veniva"));
                            referencias.setQuantity("1");
                            referencias.setCantPed("1");
                            /**
                             * Guardamos la lista de referencias de manera local con sugar
                             * */
                            referencias.save();
                            dialog.dismiss();
                        }
                        dialog.dismiss();

                    } catch (JSONException e) {
                        dialog.dismiss();
                        System.out.println();
                        e.printStackTrace();
                        System.out.println(response);
                        Toast.makeText(pruebaActivity, "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    if (error.toString().contains("com.android.volley.NoConnectionError")){
                        Toast.makeText(pruebaActivity, "No se puede conectar, verifique que el servidor se encuentre disponible", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(pruebaActivity, "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(pruebaActivity).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            dialog.dismiss();
            Toast.makeText(pruebaActivity, "Ocurrió un error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void sincronizarPedidos(PruebaActivity pruebaActivity, String HOST) {
        dialog.show();
        Toast.makeText(pruebaActivity, "Inicia Sincronización de pedidos ", Toast.LENGTH_SHORT).show();
        int cantPed = Pedido.listAll(Pedido.class).size();
        Toast.makeText(pruebaActivity, "Pedidos pendientes: " + cantPed, Toast.LENGTH_LONG).show();
        if (cantPed > 0) {
            List<Pedido> pedidos = new ArrayList<>();
            pedidos = Pedido.listAll(Pedido.class, "id");
            ordersinc(pedidos, pruebaActivity);
        }
        dialog.dismiss();
    }

    private static void ordersinc(List<Pedido> pedidos, final PruebaActivity pruebaActivity) {
        for (Pedido p : pedidos) {

            String[] productos = new String[p.getProducts().size()];
            String[] cantidades = new String[p.getProducts().size()];
            String[] precios = new String[p.getProducts().size()];

            String cliente =  p.getTercero().getTercero();
            String vendedor = p.getVendedor();
            for(int i = 0; i < p.getProducts().size(); i++) {
                productos[i] = p.getProducts().get(i).getCodRef();
                cantidades[i] = p.getProducts().get(i).getCantPed();
                precios[i] = p.getProducts().get(i).getPrice();
            }
            String total = ""+p.getPrecioTotal();

            HashMap<String, String> map = new HashMap<>();// Mapeo previo

            map.put("cliente", cliente);

            map.put("vendedor", vendedor);

            map.put("producto", Arrays.toString(productos));

            map.put("cantidad", Arrays.toString(cantidades));
            map.put("precio", Arrays.toString(precios));


            // Crear nuevo objeto Json basado en el mapa
            JSONObject jobject = new JSONObject(map);

            // Actualizar datos en el servidor
            VolleySingleton.getInstance(pruebaActivity).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            /*"https://empresis.000webhostapp.com/pedido.php",*/HOST+"/pedido.php",
                            jobject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Procesar la respuesta del servidor
                                    procesarRespuesta(response, pruebaActivity);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(PruebaActivity.class.getSimpleName(), "Error Volley: " + error.getMessage());
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

    }

    private static void procesarRespuesta(JSONObject response, PruebaActivity pruebaActivity) {
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
                            pruebaActivity,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    Pedido.deleteAll(Pedido.class);

                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            pruebaActivity,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
