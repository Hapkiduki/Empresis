package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Programa-PC on 01/03/2017.
 */

public class TerceroAdapter extends RecyclerView.Adapter<TerceroAdapter.ViewHolder>{

    private Context context;
    private List<Tercero> terceros;

    public TerceroAdapter(Context context, List<Tercero> terceros) {
        this.context = context;
        this.terceros = terceros;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_terce, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String nombre = terceros.get(position).getTercero()!= null && !isEmpty(terceros.get(position).getTercero())
                ? terceros.get(position).getTercero():"Desconocido";
        String dni = terceros.get(position).getDni()!= null && !isEmpty(terceros.get(position).getDni())
                ? terceros.get(position).getDni(): "Desconocido";
        String direccion = terceros.get(position).getDireccion() != null && !isEmpty(terceros.get(position).getDireccion())
                ? terceros.get(position).getDireccion():"Desconocido";
        String telefono = terceros.get(position).getTelefono() != null && !isEmpty(terceros.get(position).getTelefono())
                ? terceros.get(position).getTelefono(): "Deconocido";

        holder.nombre.setText(nombre);
        holder.dni.setText(dni);
        holder.direccion.setText(direccion);
        holder.telefono.setText(telefono);
    }

    @Override
    public int getItemCount() {
        return terceros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView nombre, dni, direccion, telefono;

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.tvTerce);
            dni = (TextView) itemView.findViewById(R.id.tvDNI);
            direccion = (TextView) itemView.findViewById(R.id.tvDireccion);
            telefono = (TextView) itemView.findViewById(R.id.tvTelefono);
        }
    }

    //Agregamos un filtro Scope a nuestro recycler view
    public void setFilter(ArrayList<Tercero> query){
        terceros = new ArrayList<>();
        terceros.addAll(query);
        notifyDataSetChanged();
    }
}
